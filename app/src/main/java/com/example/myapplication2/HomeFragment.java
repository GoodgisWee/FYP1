package com.example.myapplication2;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication2.Formula.GeoUtils;
import com.example.myapplication2.Login.LoginActivity;
import com.example.myapplication2.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Collection;
import java.util.List;


public class HomeFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db;
    private String userName;
    private LatLng userLocation;
    private double userLatitude, userLongitude;
    private int numberOfCustomersNearby = 0;
    private TextView nearbyCustomerNumber, currentAddress, logoutTextview;
    private ImageView menuImage;
    private RelativeLayout relativeLayoutSideBar;
    public void setUidAndType(String uid, String type) {
        // Set uid and type as needed
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigation);

        //-----------------------------------------------------------------------------------------
        //Side bar operation
        relativeLayoutSideBar = view.findViewById(R.id.relativeLayoutSideBar);
        menuImage = view.findViewById(R.id.menuImage);
        logoutTextview = view.findViewById(R.id.logoutTextview);

        menuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayoutSideBar.setVisibility(View.VISIBLE);
            }
        });

        relativeLayoutSideBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayoutSideBar.setVisibility(View.GONE);
            }
        });

        logoutTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireActivity(), "Successfully Logout", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        //-----------------------------------------------------------------------------------------
        ImageButton navigateBtn = view.findViewById(R.id.navigateBtn);
        navigateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("findUserLocation", true);
                NearbyFragment nearbyFragment = new NearbyFragment();
                nearbyFragment.setArguments(bundle);

                bottomNavigationView.setSelectedItemId(R.id.nearby);
                requireActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.frame_layout, new NearbyFragment()).
                        addToBackStack(null).commit();
            }
        });

        nearbyCustomerNumber = view.findViewById(R.id.nearbyCustomerNumber);
        currentAddress = view.findViewById(R.id.currentAddress);
        getCurrentUser();

        return view;
    }

    private void getCurrentUser() {
        DocumentReference currentUserRef = db.collection("User").document("currentUser");
        currentUserRef.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                userName = documentSnapshot.getString("username");
                if(userName != null){
                    getUserLocation();
                    //Toast.makeText(requireActivity(), "Successfully get the User Location from Database", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireActivity(), "Cannot get the current user from firebase", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getUserLocation(){
        DocumentReference userLocationRef = db.collection("User").document(userName);
        userLocationRef.get().addOnSuccessListener(documentSnapshot -> {
            userLatitude = documentSnapshot.getDouble("latitude");
            userLongitude = documentSnapshot.getDouble("longitude");
            userLocation = new LatLng(userLatitude, userLongitude);
            convertLatLngToAddress(userLocation);
            getCustomerNearby();
        });
    }

    private void getCustomerNearby() {
        CollectionReference customerCollection = db.collection("Customer");
        // Define the maximum distance (in kilometers) you want to consider
        double maxDistance = 3.0; // Example distance in kilometers

        // Perform the query
        customerCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String customerName = document.getId();
                            Double customerLatitude = document.getDouble("latitude");
                            Double customerLongitude = document.getDouble("longitude");

                            if (customerLatitude != null && customerLongitude != null) {
                                // Check if the customer is within the specified distance
                                double distance = GeoUtils.calculateDistance(userLatitude, userLongitude, customerLatitude, customerLongitude);

                                if (distance <= maxDistance) {
                                    numberOfCustomersNearby ++;
                                } else {
                                    Log.d(TAG, "Customer " + customerName + " is not within 5km.");
                                }
                            }
                        }
                        nearbyCustomerNumber.setText(String.valueOf(numberOfCustomersNearby));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting documents.", e);
                    }
                });
    }

    private void convertLatLngToAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(requireContext());

        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);

                // Use the address details as needed
                String fullAddress = address.getAddressLine(0); // Full address line

                // Set the address to the TextView
                currentAddress.setText(fullAddress);
            } else {
                // Handle the case where no address was found
                currentAddress.setText("No address can be converted");
                //Toast.makeText(requireActivity(), "No address can be converted", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}