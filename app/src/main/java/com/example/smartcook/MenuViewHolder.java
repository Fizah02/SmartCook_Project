package com.example.smartcook;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuViewHolder extends RecyclerView.ViewHolder {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    public ImageView imageView;
    public TextView titleTextView;
    public TextView ownerTextView;
    public TextView timeTextView;
    public TextView idTextView;
    public ImageButton favoriteButton;
    public CardView recipe;
    private boolean isSaved = false;
    private DatabaseReference mDatabase;
    private FirebaseFirestore db;

    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.menu_image);
        titleTextView = itemView.findViewById(R.id.menu_title);
        ownerTextView = itemView.findViewById(R.id.menu_owner);
        timeTextView = itemView.findViewById(R.id.menu_time);
        idTextView = itemView.findViewById(R.id.menu_id);
        favoriteButton = itemView.findViewById(R.id.favorite_button);
        recipe = itemView.findViewById(R.id.recipe);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        db = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        favoriteButton.setOnClickListener(v -> {
            // Toggle the saved state
            isSaved = !isSaved;

            if (isSaved) {
                // Check if the recipeId already exists in favourite_recipes
                checkAndAddToFavorites();
            } else {
                // If data is not saved, make the button white
                favoriteButton.setBackgroundResource(R.drawable.ic_favourite_unselected);
                // Remove the data from Firestore
                onRemoveButtonClick(v);
            }
        });

        recipe.setOnClickListener(v -> {
            // Toggle the saved state

        });

    }

    private void checkAndAddToFavorites() {
        String userId = mAuth.getCurrentUser().getUid();

        // Reference to the "favourite_recipes" for the current user
        DocumentReference recipeReference = firestore.collection("users")
                .document(userId)
                .collection("favourite_recipes")
                .document(titleTextView.getText().toString());

        recipeReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Recipe already exists in favourites
                    Toast.makeText(itemView.getContext(), "Recipe already in favorites", Toast.LENGTH_SHORT).show();
                } else {
                    // Recipe does not exist, add it to favourites
                    favoriteButton.setBackgroundResource(R.drawable.ic_favourite_selected);
                    onFavoriteButtonClick();
                }
            } else {
                Toast.makeText(itemView.getContext(), "Error checking recipe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onFavoriteButtonClick() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            // Reference to the user's document
            DocumentReference userDocument = firestore.collection("users").document(userId);

            // Reference to the "favorites" sub-collection under the user's document
            CollectionReference favoritesCollection = userDocument.collection("favourite_recipes");

            // Get the image URL and title from the views
            String imageUrl = imageView.getTag().toString();
            String title = titleTextView.getText().toString();
            String owner = ownerTextView.getText().toString();
            String time = timeTextView.getText().toString();
            String id = idTextView.getText().toString();

            // Create a map with the data you want to save to Firestore.
            Map<String, Object> dataToSave = new HashMap<>();
            dataToSave.put("title", title); // Save the title
            dataToSave.put("imageUrl", imageUrl); // Save the image URL
            dataToSave.put("owner",owner);
            dataToSave.put("time",time);
            dataToSave.put("id",id);

            // Add a new document to the "favorites" sub-collection
            favoritesCollection.add(dataToSave)
                    .addOnSuccessListener(documentReference -> {
                        // Data saved successfully.
                        // You can update UI or provide a confirmation message.
                    })
                    .addOnFailureListener(e -> {
                        // Handle the error here.
                    });
        } else {
            // The user is not authenticated. Handle this case as needed (e.g., show a login screen).
        }
    }

    private void onRemoveButtonClick(View v) {
        String userId = mAuth.getCurrentUser().getUid();

        // Reference to the "favourite_recipes" sub-collection under the user's document
        CollectionReference favouriteRecipesCollection = firestore.collection("users")
                .document(userId)
                .collection("favourite_recipes");

        // Get the image URL from the views (you can use the image URL or another unique identifier)
        String imageUrl = imageView.getTag().toString();

        // Query the "favourite_recipes" sub-collection to find the document with the matching image URL
        favouriteRecipesCollection.whereEqualTo("imageUrl", imageUrl)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        // Delete the document(s) that match the query
                        favouriteRecipesCollection.document(snapshot.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Deletion successful. Update UI or provide a confirmation message.
                                    // For example, change the button background to indicate it's not saved.
                                    isSaved = false;
                                    updateButtonBackground();
                                })
                                .addOnFailureListener(e -> {
                                    // Handle the error here.
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the error here.
                });
    }

    private void updateButtonBackground() {
        if (isSaved) {
            // If data is saved, make the button green
            favoriteButton.setBackgroundResource(R.drawable.ic_favourite_selected);
        } else {
            // If data is not saved, make the button white
            favoriteButton.setBackgroundResource(R.drawable.ic_favourite_unselected);
        }
    }

    public void onHistoryClick() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            // Reference to the user's document
            DocumentReference userDocument = firestore.collection("users").document(userId);

            // Reference to the "favorites" sub-collection under the user's document
            CollectionReference historyCollection = userDocument.collection("history_recipes");

            // Get the image URL and title from the views
            String imageUrl = imageView.getTag().toString();
            String title = titleTextView.getText().toString();
            String owner = ownerTextView.getText().toString();
            String time = timeTextView.getText().toString();
            String id = idTextView.getText().toString();

            // Create a map with the data you want to save to Firestore.
            Map<String, Object> dataSave = new HashMap<>();
            dataSave.put("title", title); // Save the title
            dataSave.put("imageUrl", imageUrl); // Save the image URL
            dataSave.put("owner",owner);
            dataSave.put("time",time);
            dataSave.put("id",id);

            // Add a new document to the "favorites" sub-collection
            historyCollection.add(dataSave)
                    .addOnSuccessListener(documentReference -> {
                        // Data saved successfully.
                        // You can update UI or provide a confirmation message.
                    })
                    .addOnFailureListener(e -> {
                        // Handle the error here.
                    });
        } else {
            // The user is not authenticated. Handle this case as needed (e.g., show a login screen).
        }
        Log.d("History", "Adding to history");
    }

    public void checkAndAddToHistory() {
        String userId = mAuth.getCurrentUser().getUid();

        // Reference to the "favourite_recipes" for the current user
        DocumentReference recipeReference = firestore.collection("users")
                .document(userId)
                .collection("history_recipes")
                .document(titleTextView.getText().toString());

        recipeReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Recipe already exists in favourites

                } else {
                    onHistoryClick();
                }
            } else {
                Toast.makeText(itemView.getContext(), "Error checking recipe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindData(String imageUrl, String title, String owner, String time, String id, boolean isSaved) {
        // Set the image URL as a tag to the imageView
        imageView.setTag(imageUrl);

        // Set the title to the titleTextView
        titleTextView.setText(title);

        ownerTextView.setText(owner);

        timeTextView.setText(time);

        idTextView.setText(id);

        // Set the saved state of the button
        this.isSaved = isSaved;
        checkDataInFirestore(imageUrl);
    }

    private void checkDataInFirestore(String imageUrl) {
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference userDocument = firestore.collection("users").document(userId);
        CollectionReference favouriteRecipesCollection = userDocument.collection("favourite_recipes");

        // Query the "favourite_recipes" sub-collection to find the document with the matching image URL
        favouriteRecipesCollection.whereEqualTo("imageUrl", imageUrl)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Data exists in Firestore, make the button green
                        isSaved = true;
                        updateButtonBackground();
                    } else {
                        // Data does not exist in Firestore, make the button white
                        isSaved = false;
                        updateButtonBackground();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the error here.
                });
    }
}