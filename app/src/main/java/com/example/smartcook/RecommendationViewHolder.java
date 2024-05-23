package com.example.smartcook;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class RecommendationViewHolder extends RecyclerView.ViewHolder {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    public ImageView imageView;
    public TextView titleTextView;
    public TextView ownerTextView;
    public TextView idTextView;
    public ImageButton favoriteButton; // Add the favorite button here
    public LinearLayout recipe;
    boolean isSaved = false;

    public RecommendationViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.recipeImage);
        titleTextView = itemView.findViewById(R.id.recipeTitle);
        ownerTextView = itemView.findViewById(R.id.recipeOwner);
        idTextView = itemView.findViewById(R.id.idTextView);
        favoriteButton = itemView.findViewById(R.id.favoriteButton);
        recipe = itemView.findViewById(R.id.recipe);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();// Initialize the favorite button

        favoriteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Toggle the saved state
                isSaved = !isSaved;

                if (isSaved) {
                    // If data is saved, make the button green
                    favoriteButton.setBackgroundResource(R.drawable.ic_favourite_selected);
                    // Save the image URL and title to Firestore
                    onFavoriteButtonClick(v);
                } else {
                    // If data is not saved, make the button white
                    favoriteButton.setBackgroundResource(R.drawable.ic_favourite_unselected);
                    // Remove the data from Firestore
                    onRemoveButtonClick(v);
                }
            }

        });

        recipe.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Toggle the saved state
                isSaved = !isSaved;

                if (isSaved) {

                    onHistoryClick(v);
                }
            }
        });
    }

    public void onFavoriteButtonClick(View view) {
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
            String id = idTextView.getText().toString();

            // Create a map with the data you want to save to Firestore.
            Map<String, Object> dataToSave = new HashMap<>();
            dataToSave.put("title", title); // Save the title
            dataToSave.put("owner", owner);
            dataToSave.put("imageUrl", imageUrl); // Save the image URL
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

    public void onHistoryClick(View view) {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            // Reference to the user's document
            DocumentReference userDocument = firestore.collection("users").document(userId);

            // Reference to the "favorites" sub-collection under the user's document
            CollectionReference favoritesCollection = userDocument.collection("history_recipes");

            // Get the image URL and title from the views
            String imageUrl = imageView.getTag().toString();
            String title = titleTextView.getText().toString();
            String owner = ownerTextView.getText().toString();
            String id = idTextView.getText().toString();

            // Create a map with the data you want to save to Firestore.
            Map<String, Object> dataToSave = new HashMap<>();
            dataToSave.put("title", title); // Save the title
            dataToSave.put("owner", owner);
            dataToSave.put("imageUrl", imageUrl); // Save the image URL
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
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            // Reference to the user's document
            DocumentReference userDocument = firestore.collection("users").document(userId);

            // Reference to the "saved_ingredients" sub-collection under the user's document
            CollectionReference favouriteRecipesCollection = userDocument.collection("favourite_recipes");

            // Get the image URL from the views (you can use the image URL or another unique identifier)
            String imageUrl = imageView.getTag().toString();

            // Query the "saved_ingredients" sub-collection to find the document with the matching image URL
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
        } else {
            // The user is not authenticated. Handle this case as needed (e.g., show a login screen).
        }
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

    public void bindData(String imageUrl, String title, String owner, String id, boolean isSaved) {
        // Set the image URL as a tag to the imageView
        imageView.setTag(imageUrl);

        // Set the title to the titleTextView
        titleTextView.setText(title);

        ownerTextView.setText(owner);

        idTextView.setText(id);

        // Set the saved state of the button
        this.isSaved = isSaved;
        checkDataInFirestore(imageUrl);
    }

    private void checkDataInFirestore(String imageUrl) {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DocumentReference userDocument = firestore.collection("users").document(userId);
            CollectionReference savedIngredientsCollection = userDocument.collection("favourite_recipes");

            // Query the "saved_ingredients" sub-collection to find the document with the matching image URL
            savedIngredientsCollection.whereEqualTo("imageUrl", imageUrl)
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
        } else {
            // The user is not authenticated. Handle this case as needed (e.g., show a login screen).
        }
    }

}
