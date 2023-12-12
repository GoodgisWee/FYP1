package com.example.myapplication2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class LoadingFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private LatLng userLocation;
    private double userLatitude, userLongitude;
    private BottomNavigationView bottomNavigationView;

    private String userName;
    private FirebaseFirestore db;
    public void setUidAndType(String uid, String type) {
        // Set uid and type as needed
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_loading, container, false);
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigation);

        db = FirebaseFirestore.getInstance();

        // Check if location services are enabled
        if (!isLocationEnabled(requireContext())) {
            // Location services are not enabled; show a message and provide an option to enable them
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage("Location services are not enabled. Enable them to use this feature.");
            builder.setPositiveButton("Enable Location Services", (dialog, which) -> {
                openLocationSetting();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                // Handle the case where the user doesn't enable location services
                Toast.makeText(requireContext(), "Location services are required for this feature.", Toast.LENGTH_SHORT).show();
            });
            builder.setCancelable(false);
            builder.show();
        } else {
            // Location services are enabled; proceed with location permission check
            checkLocationPermission();
        }

        return view;
    }

    //Check whether the location service is open or not
    private boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    //open the location setting
    private void openLocationSetting(){
        // Open device settings for location services
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    // Check location permission
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            requestLocationPermission();
        } else {
            // Permission granted, save user location
            saveUserLocation();
        }
    }

    //request for location permission
    private void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    // Handle user choice for granting location permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, save user location
                saveUserLocation();
            } else {
                // Permission denied, show a message
                Toast.makeText(requireContext(), "Location permission is required for this feature.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void saveUserLocation(){
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        @SuppressLint("MissingPermission")
        Task<Location> locationTask = fusedLocationClient.getLastLocation();

        locationTask.addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    //get user location
                    userLatitude = location.getLatitude();
                    userLongitude = location.getLongitude();
                    getCurrentUser();
                    userLocation = new LatLng(userLatitude, userLongitude);
                }
                //if user location cannot be get
                else {
                    //default location: TTDI MRT
                    userLatitude = 3.1358;
                    userLongitude = 101.6314;
                    saveLocationToFirestore(userLatitude, userLongitude);
                    userLocation = new LatLng(userLatitude, userLongitude);
                }

               /* if(userLocation!=null){
                    //bundle for set all variable to pass
                    Bundle bundle = new Bundle();
                    bundle.putDouble("userLatitude", userLatitude);
                    bundle.putDouble("userLongitude", userLongitude);

                    //add the bundle into the fragment
                    HomeFragment homeFragment = new HomeFragment();
                    homeFragment.setArguments(bundle);

                    //move the bottom navigation bar icon
                    bottomNavigationView.setSelectedItemId(R.id.home);

                    //replace the fragment in the frame layout
                    requireActivity().getSupportFragmentManager().
                            beginTransaction().replace(R.id.frame_layout,homeFragment).commit();
                } else {
                    Toast.makeText(requireActivity(), "Unable to get User Location", Toast.LENGTH_SHORT).show();
                }*/
            }
        });
    }

    private void getCurrentUser(){
        DocumentReference userLocationRef = db.collection("User").document("currentUser");
        userLocationRef.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                userName = documentSnapshot.getString("username");
                saveLocationToFirestore(userLatitude, userLongitude);
            }
        });
    }
    private void saveLocationToFirestore(double latitude, double longitude) {
        // Create a new user location document
        Map<String, Object> userLocation = new HashMap<>();
        userLocation.put("latitude", latitude);
        userLocation.put("longitude", longitude);

        // Add the document to the "user_locations" collection
        db.collection("User")
                .document(userName) //xxx Use the actual user ID here
                .update(userLocation)
                .addOnSuccessListener(aVoid -> {
                    // Location saved successfully
                    Toast.makeText(requireActivity(), "User location saved to Firestore", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().
                            beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
                })
                .addOnFailureListener(e -> {
                    // Error saving location
                    Toast.makeText(requireActivity(), "Error saving user location", Toast.LENGTH_SHORT).show();
                });
    }
}