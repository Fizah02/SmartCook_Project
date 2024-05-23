package com.example.smartcook;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class Profile extends Fragment {

    private static final int GALLERY_REQUEST_CODE = 0;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private ListenerRegistration userListener;

    private ImageView profilePicture;
    private ImageButton editProfileButton;
    private ImageButton menuButton;
    private TextView nameTextView;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TabLayout tabLayout;
    private FragmentManager fragmentManager;
    private Toolbar toolbar;
    private Intent intent;
    private FloatingActionButton fabIngredients;

    String userId = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("userId");
        }

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        userRef = db.collection("users").document(userId);
        fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager = getChildFragmentManager();

        profilePicture = view.findViewById(R.id.profilePicture);
        editProfileButton = view.findViewById(R.id.editProfilePictureButton); // Use the correct ID for the ImageView
        nameTextView = view.findViewById(R.id.nameTextView);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        menuButton = view.findViewById(R.id.menuButton);


        tabLayout = view.findViewById(R.id.tabLayout);
        fabIngredients = view.findViewById(R.id.fab);


        addTab("Saved Ingredients");
        addTab("History Recipes");

        // Set an initial fragment (e.g., Fragment1)
        switchFragment(new SavedIngredientsProfile());

        // Handle tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Handle tab selection and switch fragments
                switch (tab.getPosition()) {
                    case 0:
                        switchFragment(new SavedIngredientsProfile());
                        break;
                    case 1:
                        switchFragment(new HistoryRecipes());
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

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

        // Retrieve and display user data
        retrieveUserData();

        editProfileButton = view.findViewById(R.id.editProfilePictureButton);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Edit" button click event here
                openGallery();
            }
        });

        fabIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to ListIngredientsActivity when FAB is clicked
                Intent intent = new Intent(getContext(), SavedIngredients.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri imageUrl = data.getData();
                if (imageUrl != null) {
                    // Upload the selected image to Firebase Storage
                    uploadImageToStorage(imageUrl);
                }
            }

            // Navigate back to BottomTab class
            Intent intent = new Intent(getActivity(), BottomTab.class);
            // Pass any necessary data to the intent
            startActivity(intent);
        }
    }

    private void uploadImageToStorage(Uri imageUrl) {
        // Create a reference to the profile pictures folder in Firebase Storage
        StorageReference profilePicturesRef = storageRef.child("profile_pictures/" + userId);

        // Upload the image to Firebase Storage
        profilePicturesRef.putFile(imageUrl)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Image uploaded successfully
                        // Now, you can save the download URL to the user's document in Firestore
                        saveImageUrlToFirestore(profilePicturesRef);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure
                        Log.e("Profile", "Image upload failed: " + e.getMessage());
                        Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveImageUrlToFirestore(StorageReference imageRef) {
        // Get the download URL of the uploaded image
        imageRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        // Save the download URL to the user's document in Firestore
                        userRef.update("imageUrl", downloadUrl.toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Image URL saved successfully
                                        Toast.makeText(getContext(), "Profile picture updated", Toast.LENGTH_SHORT).show();
                                        // Reload user data to display the updated profile picture
                                        retrieveUserData();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle the failure
                                        Log.e("Profile", "Error updating image URL in Firestore: " + e.getMessage());
                                    }
                                });
                    }
                });
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());

        // Set item click listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle menu item clicks here
                switch (item.getItemId()) {
                    case R.id.menu_item1:
                        contactUs(view);
                        return true;
                    case R.id.menu_item2:
                        mAuth.signOut();
                        Intent intent = new Intent(getContext(),LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                        return true;
                    default:
                        return false;
                }
            }
        });

        // Show the popup menu
        popup.show();
    }

    public void contactUs(View view) {

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:csmartcook@gmail.com")); // Set the recipient email address
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Report an Issue"); // Set the email subject
        emailIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Hello,\n\nI would like to report the following issue:\n\n My email is" // Set the default email body
        );

        if (getActivity() != null && getActivity().getPackageManager().resolveActivity(emailIntent, 0) != null) {
            startActivity(emailIntent);
        }
    }

    // Add this method to handle the result of the image picker.
    private void retrieveUserData() {
        userListener = userRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                // Handle errors
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                // Retrieve user data from Firestore
                String name = documentSnapshot.getString("name");

                // Check if the name is not null or empty
                if (name != null && !name.isEmpty()) {
                    // Capitalize the first letter
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                }
                String username = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email");
                String profilePictureUrl = documentSnapshot.getString("imageUrl");

                // Set data in TextViews and load profile picture
                nameTextView.setText(name);
                usernameTextView.setText(username);
                emailTextView.setText(email);
                if (profilePictureUrl != null) {
                    Glide.with(getContext()).load(profilePictureUrl).into(profilePicture);
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userListener != null) {
            userListener.remove();
        }
    }

    private void addTab(String title) {
        TabLayout.Tab tab = tabLayout.newTab();
        tab.setText(title);
        tabLayout.addTab(tab);
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Enable options menu in the fragment
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_log_out:
                // Handle the "Log Out" menu item click here
                Toast.makeText(getContext(), "Log Out", Toast.LENGTH_SHORT).show();
                // Implement your logout logic here
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}