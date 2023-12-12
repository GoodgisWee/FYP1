package com.example.myapplication2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication2.Contact.ContactDetailsFragment;
import com.example.myapplication2.Login.LoginActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;


import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class NearbyFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private LatLng userLocation, customerLocation;
    private boolean findUserLocation, findCustomerLocation;
    private ImageButton currentLocationBtn;
    private String customerName, userName;
    private FirebaseFirestore db;
    private TextView customerNameTextView, positionTextView, companyTextView;
    private RelativeLayout popoutLayout;
    private Button goBtn, moreBtn;
    private BottomNavigationView bottomNavigationView;
    public void setUidAndType(String uid, String type) {
        // Set uid and type as needed
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigation);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_nearby, container, false);

        // Inflate the layout for this fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        companyTextView = view.findViewById(R.id.company);
        positionTextView = view.findViewById(R.id.position);
        customerNameTextView = view.findViewById(R.id.customerNameTextView);
        popoutLayout = view.findViewById(R.id.popoutLayout);
        goBtn = view.findViewById(R.id.goBtn);
        moreBtn = view.findViewById(R.id.moreBtn);

        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userLocation != null && customerLocation != null) {
                    //calculateDirections(userLocation, customerLocation);
                    intentToGoogleMaps();
                }
            }
        });

        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomNavigationView.setSelectedItemId(R.id.contact);
                ContactDetailsFragment contactDetailsFragment = new ContactDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", customerNameTextView.getText().toString());
                contactDetailsFragment.setArguments(bundle);
                requireActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.frame_layout, contactDetailsFragment).
                        addToBackStack(null).commit();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize currentLocationBtn here
        currentLocationBtn = view.findViewById(R.id.currentLocationBtn);

        // Set the click listener for currentLocationBtn
        currentLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userLocation != null && googleMap != null) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                }
            }
        });

        popoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popoutLayout.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        getCurrentUser();
        Bundle args = getArguments();
        if (args != null) {
            // Retrieve values from arguments
            findUserLocation = args.getBoolean("findUserLocation");
            customerName = args.getString("customerName");
            if(customerName != null){
                getCustomerLocation();
            }
        }

        // Set up marker click listener
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Handle marker click here
                if (marker.getTag() != null){
                    db.collection("Customer").document(marker.getTag().toString()).
                            get().addOnSuccessListener(documentSnapshot->{
                            if(documentSnapshot.exists()){
                                String position = documentSnapshot.getString("position");
                                String company = documentSnapshot.getString("company");
                                String name = documentSnapshot.getString("name");
                                Double latitude = documentSnapshot.getDouble("latitude");
                                Double longitude = documentSnapshot.getDouble("longitude");
                                customerLocation = new LatLng(latitude, longitude);
                                customerNameTextView.setText(name);
                                positionTextView.setText(position);
                                companyTextView.setText(company);
                                // Move the camera to the clicked marker's position
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(customerLocation, 15));
                                popoutLayout.setVisibility(View.VISIBLE);

                            }
                    });
                    return true;
                }
                return false;
            }
        });

    }

    private void updateMapWithLocation(LatLng currentLocation, Bitmap currentBitmap, String userName) {
        if (googleMap != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currentLocation)
                    .zoom(15)
                    .build();

            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            Bitmap resizedCurrentBitmap = Bitmap.createScaledBitmap(currentBitmap, 100, 100, false);
            BitmapDescriptor resizedCurrentMarkerIcon = BitmapDescriptorFactory.fromBitmap(resizedCurrentBitmap);
            MarkerOptions currentMarkerOptions = new MarkerOptions()
                    .position(currentLocation)  // Set the marker's position
                    .icon(resizedCurrentMarkerIcon); // Set the custom marker image
            // Add the marker to the map
            Marker currentMarker = googleMap.addMarker(currentMarkerOptions);
            if(userName.equalsIgnoreCase("customer")){
                currentMarker.setTag("customer");
            } else if (userName.equalsIgnoreCase("user")){
                currentMarker.setTag("user");
            } else if (userName.equalsIgnoreCase("default")){
                currentMarker.setTag("default");
            } else {
                currentMarker.setTag(userName);
            }
        } else {
            // Handle the case where googleMap is null
            Toast.makeText(requireActivity(), "Google Map is not available", Toast.LENGTH_SHORT).show();
        }
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
            Double latitude = documentSnapshot.getDouble("latitude");
            Double longitude = documentSnapshot.getDouble("longitude");
            userLocation = new LatLng(latitude, longitude);
            displayAllCustomerOnMap();
        });
    }
    private void getCustomerLocation() {
        DocumentReference customerLocationRef = db.collection("Customer").document(customerName);
        customerLocationRef.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                Double latitude = documentSnapshot.getDouble("latitude");
                Double longitude = documentSnapshot.getDouble("longitude");
                customerLocation = new LatLng(latitude, longitude);
                displayAllCustomerOnMap();
                Toast.makeText(requireActivity(), "Successfully get the User Location from Database", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireActivity(), "Cannot get the User Location from Database", Toast.LENGTH_SHORT).show();
            }
        });

    }



    private void displayAllCustomerOnMap() {
        db.collection("Customer")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // List to store customer locations
                        List<LatLng> customerLocations = new ArrayList<>();
                        List<String> customerNames = new ArrayList<>();

                        // Iterate through documents and get customer details
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Double latitude = document.getDouble("latitude");
                            Double longitude = document.getDouble("longitude");
                            String customerName = document.getString("name");
                            customerNames.add(customerName);
                            if (latitude != null && longitude != null) {
                                LatLng customerLocation = new LatLng(latitude, longitude);
                                customerLocations.add(customerLocation);
                            }
                        }

                        // Display all customer locations on the map
                        displayCustomerLocations(customerLocations, customerNames);
                    } else {
                        Toast.makeText(requireActivity(), "Error retrieving customer locations", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayCustomerLocations(List<LatLng> customerLocations, List<String> customerNames) {
        // Check if the map is ready
        if (googleMap != null) {
            int customerCount = 0;
            // Iterate through customer locations and add markers to the map
            for (LatLng location : customerLocations) {
                Bitmap customerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.anson_seabra_face);
                updateMapWithLocation(location, customerBitmap, customerNames.get(customerCount));
                customerCount++;
            }
            showLocation();
        } else {
            Toast.makeText(requireActivity(), "Google Map is not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLocation() {
        // Add your map customization and functionality here
        if (customerName != null && customerLocation != null) {
            Bitmap customerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.anson_seabra_face);
            updateMapWithLocation(customerLocation, customerBitmap, customerName);
        } else if (userLocation != null) {
            Bitmap userBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.current_location_icon);
            updateMapWithLocation(userLocation, userBitmap, "user");
        } else {
            Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img);
            LatLng defaultLocation = new LatLng(37.7749, -122.4194);
            updateMapWithLocation(defaultLocation, defaultBitmap, "default");
        }
    }

    private void intentToGoogleMaps(){
        // Convert LatLng to String for the source and destination in the URL
        String source = userLocation.latitude + "," + userLocation.longitude;
        String destination = customerLocation.latitude + "," + customerLocation.longitude;
        Uri uri = Uri.parse("https://www.google.com/maps/dir/"+source+"/"+destination);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
