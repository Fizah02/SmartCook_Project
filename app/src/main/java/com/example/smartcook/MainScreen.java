package com.example.smartcook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class MainScreen extends Fragment {

    private TabLayout tabLayout;
    private TextView username;
    private FragmentManager fragmentManager;
    private FragmentManager fragmentManager2;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private ListenerRegistration userListener;

    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_mainscreen, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);

        username = view.findViewById(R.id.username);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        userRef = db.collection("users").document(userId);

        userListener = userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle errors
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String userNameString = documentSnapshot.getString("name");
                    username.setText(userNameString);

                }
            }
        });

        fragmentManager = getChildFragmentManager();
        fragmentManager2 = getChildFragmentManager();


        // Initialize the Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Add tabs to the TabLayout with different titles
        addTab("Malay");
        addTab("Indian");
        addTab("Chinese");
        addTab("Italian");
        addTab("Korean");

        // Set an initial fragment (e.g., Fragment1)
        switchFragment(new Recommendation1());
        switchFragment2(new NewRecipes());
        // Handle tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Handle tab selection and switch fragments
                switch (tab.getPosition()) {
                    case 0:
                        switchFragment(new Recommendation1());
                        break;
                    case 1:
                        switchFragment(new Recommendation2());
                        break;
                    case 2:
                        switchFragment(new Recommendation3());
                        break;
                    case 3:
                        switchFragment(new Recommendation4());
                        break;
                    case 4:
                        switchFragment(new Recommendation5());
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

    private void switchFragment2(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager2.beginTransaction();
        transaction.replace(R.id.fragmentContainer2, fragment);
        transaction.commit();
    }
}
