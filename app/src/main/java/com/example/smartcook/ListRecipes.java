package com.example.smartcook;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.tabs.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListRecipes extends Fragment {

    private TabLayout tabLayout;
    private FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_recipes, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        fragmentManager = getChildFragmentManager(); // Use getChildFragmentManager() for nested fragments

        // Add tabs to the TabLayout with different titles
        addTab("Malay");
        addTab("Indian");
        addTab("Chinese");
        addTab("Italian");
        addTab("Korean");

        // Set an initial fragment (e.g., Fragment1)
        switchFragment(new Category1());

        // Handle tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Handle tab selection and switch fragments
                switch (tab.getPosition()) {
                    case 0:
                        switchFragment(new Category1());
                        break;
                    case 1:
                        switchFragment(new Category2());
                        break;
                    case 2:
                        switchFragment(new Category3());
                        break;
                    case 3:
                        switchFragment(new Category4());
                        break;
                    case 4:
                        switchFragment(new Category5());
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

        return view;
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
}
