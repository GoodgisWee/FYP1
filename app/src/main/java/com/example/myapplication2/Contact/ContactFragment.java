package com.example.myapplication2.Contact;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication2.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ContactFragment extends Fragment implements ContactAdapter.DetailsClickListener{

    private EditText searchBar;
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private String sectionTitle, contactPhoneNo, contactName;
    private FirebaseFirestore db;

    public void setUidAndType(String uid, String type) {
        // Set uid and type as needed
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        db = FirebaseFirestore.getInstance();

        searchBar=view.findViewById(R.id.searchBar);
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //xxx here need implement search function
                Toast.makeText(getActivity(), "Search bar clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        showContact(view);

        return  view;
    }

    private void showContact(View view){
        // Recycle view setup
        recyclerView = view.findViewById(R.id.recyclerViewContacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<ContactItem> contactItemList = new ArrayList<>();
        contactAdapter = new ContactAdapter(contactItemList, this);
        recyclerView.setAdapter(contactAdapter);

        db.collection("Customer").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshot) {
                for(QueryDocumentSnapshot document : queryDocumentSnapshot){
                    String name = document.getString("name");
                    String phoneNo = document.getString("phoneNo");
                    String sectionTitle = name.substring(0,1).toUpperCase();
                    ContactItem contactItem = new ContactItem(sectionTitle, phoneNo, name);
                    contactItemList.add(contactItem);
                }
                contactAdapter.notifyDataSetChanged();
            }
        });

    }
    @Override
    public void detailsClick(ContactItem contactItem) {

        ContactDetailsFragment contactDetailsFragment = new ContactDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", contactItem.getContactName());
        contactDetailsFragment.setArguments(bundle);
        requireActivity().getSupportFragmentManager().beginTransaction().
                replace(R.id.frame_layout, contactDetailsFragment).
                addToBackStack(null).commit();
    }
}