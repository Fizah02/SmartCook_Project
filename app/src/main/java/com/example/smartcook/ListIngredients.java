package com.example.smartcook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

public class ListIngredients extends AppCompatActivity {

    private TabLayout tabLayout;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        tabLayout = findViewById(R.id.tabLayout);
        fragmentManager = getSupportFragmentManager();

        // Add tabs to the TabLayout with different titles
        addTab("Meat and Seafood");
        addTab("Fruits and Vegetables");
        addTab("Grains and Pasta");
        addTab("Dairy and Eggs");
        addTab("Baking Supplies");
        addTab("Condiments");
        addTab("Oils");
        addTab("Spices and Herbs");

        // Set an initial fragment (e.g., Fragment1)
        switchFragment(new Ingredients1());

        // Handle tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Handle tab selection and switch fragments
                switch (tab.getPosition()) {
                    case 0:
                        switchFragment(new Ingredients1());
                        break;
                    case 1:
                        switchFragment(new Ingredients2());
                        break;
                    case 2:
                        switchFragment(new Ingredients3());
                        break;
                    case 3:
                        switchFragment(new Ingredients4());
                        break;
                    case 4:
                        switchFragment(new Ingredients5());
                        break;
                    case 5:
                        switchFragment(new Ingredients6());
                        break;
                    case 6:
                        switchFragment(new Ingredients7());
                        break;
                    case 7:
                        switchFragment(new Ingredients8());
                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void addTab(String title) {
        TabLayout.Tab tab = tabLayout.newTab();
        tab.setText(title);
        tabLayout.addTab(tab);
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ListIngredients.this, SavedIngredients.class);
        startActivity(intent);
        finish(); // Finish the current activity
    }
}
