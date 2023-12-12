package com.example.myapplication2.Contact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication2.NearbyFragment;
import com.example.myapplication2.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ContactDetailsFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private String customerName;
    private FirebaseFirestore db;
    private TextView nameTextView, positionTextView, companyTextView, phoneNoTextView, emailTextView, addressTextView;
    private ImageButton messageBtn, callBtn, addressBtn, telegramBtn, backButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_details, container, false);

        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigation);
        db = FirebaseFirestore.getInstance();

        nameTextView = view.findViewById(R.id.name);
        positionTextView = view.findViewById(R.id.position);
        companyTextView = view.findViewById(R.id.company);
        phoneNoTextView = view.findViewById(R.id.phoneNo);
        emailTextView = view.findViewById(R.id.email);
        addressTextView = view.findViewById(R.id.address);

        messageBtn = view.findViewById(R.id.messageBtn);
        callBtn = view.findViewById(R.id.callBtn);
        addressBtn = view.findViewById(R.id.addressBtn);
        telegramBtn = view.findViewById(R.id.telegramBtn);
        backButton = view.findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Simulate a back button press
                //requireActivity().onBackPressed();
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle message button click
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle call button click
            }
        });

        addressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a bundle to pass data to the fragment
                Bundle bundle = new Bundle();
                bundle.putString("customerName", nameTextView.getText().toString());

                // Replace the current fragment with NearbyFragment
                NearbyFragment nearbyFragment = new NearbyFragment();
                nearbyFragment.setArguments(bundle);

                bottomNavigationView.setSelectedItemId(R.id.nearby);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, nearbyFragment)
                        .addToBackStack(null) // Optional: Add this line if you want to add the transaction to the back stack
                        .commit();
            }
        });

        telegramBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle telegram button click
            }
        });

        Bundle args = getArguments();
        if(args!=null){
            customerName = args.getString("name");

            if(customerName != null) {
                db.collection("Customer").document(customerName).get().addOnSuccessListener(dataSnapShot-> {
                    String name = dataSnapShot.getString("name");
                    String phoneNo = dataSnapShot.getString("phoneNo");
                    String email = dataSnapShot.getString("email");
                    String address = dataSnapShot.getString("address");
                    String company = dataSnapShot.getString("company");
                    String position = dataSnapShot.getString("position");

                    // Set text to TextViews
                    nameTextView.setText(name);
                    phoneNoTextView.setText(phoneNo);
                    emailTextView.setText(email);
                    addressTextView.setText(address);
                    companyTextView.setText(company);
                    positionTextView.setText(position);
                });
            }
        }


        return view;
    }
}
