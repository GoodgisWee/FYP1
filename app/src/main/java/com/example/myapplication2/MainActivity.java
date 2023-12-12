package com.example.myapplication2;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication2.Contact.ContactFragment;
import com.example.myapplication2.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private String uid, type, customerName;
    private double customerLatitude, customerLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //FRAGMENT BINDING
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bottomNavigation.setSelectedItemId(R.id.home);
        replaceFragment(new LoadingFragment());
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Use if-else instead of switch to avoid constant expression error
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.nearby) {
                replaceFragment(new NearbyFragment());
            } else if (itemId == R.id.sales) {
                replaceFragment(new SalesFragment());
            } else if (itemId == R.id.contact) {
                replaceFragment(new ContactFragment());
            } else if (itemId == R.id.calendar) {
                replaceFragment(new CalendarFragment());
            }

            return true;
        });
    }

    //switch to other fragment
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (fragment instanceof HomeFragment) {
            ((HomeFragment) fragment).setUidAndType(uid, type);
        } else if (fragment instanceof NearbyFragment) {
            ((NearbyFragment) fragment).setUidAndType(uid, type);
        } else if (fragment instanceof SalesFragment) {
            ((SalesFragment) fragment).setUidAndType(uid, type);
        } else if (fragment instanceof ContactFragment) {
            ((ContactFragment) fragment).setUidAndType(uid, type);
        } else if (fragment instanceof CalendarFragment) {
            ((CalendarFragment) fragment).setUidAndType(uid, type);
        }

        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

}