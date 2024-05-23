package com.example.smartcook;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BottomTab extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_tab);

        // Inside onCreateView or wherever you are using FirebaseUser

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            // Check which item was clicked and create the corresponding fragment
            switch (item.getItemId()) {
                case R.id.menu_item1:
                    selectedFragment = new MainScreen();
                    break;
                case R.id.menu_item2:
                    selectedFragment = new ListRecipes();
                    break;
                case R.id.menu_item3:
                    selectedFragment = new Favourite();
                    break;
                case R.id.menu_item4:
                    selectedFragment = new Profile();
                    break;
            }

            // Replace the fragment within the fragmentContainer
            if (selectedFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, selectedFragment);
                // Do not add to back stack
                transaction.commit();
            }

            return true;
        });

        // Initially, set the first fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new MainScreen()).commit();
    }
}
