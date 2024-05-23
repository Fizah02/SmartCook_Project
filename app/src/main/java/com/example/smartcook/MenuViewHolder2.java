package com.example.smartcook;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class MenuViewHolder2 extends RecyclerView.ViewHolder {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    ImageView imageView;
    TextView titleTextView;
    TextView idTextView;
    ImageButton savedIngredients;
    ImageButton tickIngredients;
    boolean isSaved = false; // To track the saved state

    public MenuViewHolder2(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.menu_image);
        titleTextView = itemView.findViewById(R.id.menu_title);
        idTextView = itemView.findViewById(R.id.ingredient_id);
        savedIngredients = itemView.findViewById(R.id.add_button);
        tickIngredients = itemView.findViewById(R.id.tick_button);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        savedIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the saved state
                isSaved = !isSaved;

                if (isSaved) {
                    // If data is saved, make the button green
                    onFavoriteButtonClick(v);
                    savedIngredients.setBackgroundResource(R.drawable.green_saved_button);
                    // Save the image URL and title to Firestore

                } else {
                    // If data is not saved, make the button white
                    onRemoveButtonClick(v);
                    savedIngredients.setBackgroundResource(R.drawable.white_saved_button);
                    // Remove the data from Firestore

                }
            }


        });
    }



    // Method to update the button background based on the saved state
    private void updateButtonBackground() {
        if (isSaved) {
            // If data is saved, make the button green
            savedIngredients.setBackgroundResource(R.drawable.green_saved_button);
        } else {
            // If data is not saved, make the button white
            savedIngredients.setBackgroundResource(R.drawable.button_saved_selector);
        }
    }


    public void onFavoriteButtonClick(View view) {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            // Reference to the user's document
            DocumentReference userDocument = firestore.collection("users").document(userId);

            // Reference to the "favorites" sub-collection under the user's document
            CollectionReference favoritesCollection = userDocument.collection("saved_ingredients");

            // Get the image URL and title from the views
            String imageUrl = imageView.getTag().toString();
            String title = titleTextView.getText().toString();
            String id = idTextView.getText().toString();

            // Create a map with the data you want to save to Firestore.
            Map<String, Object> dataToSave = new HashMap<>();
            dataToSave.put("title", title); // Save the title
            dataToSave.put("imageUrl", imageUrl); // Save the image URL
            dataToSave.put("id", id);

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
            CollectionReference savedIngredientsCollection = userDocument.collection("saved_ingredients");

            // Get the image URL from the views (you can use the image URL or another unique identifier)
            String imageUrl = imageView.getTag().toString();

            // Query the "saved_ingredients" sub-collection to find the document with the matching image URL
            savedIngredientsCollection.whereEqualTo("imageUrl", imageUrl)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            // Delete the document(s) that match the query
                            savedIngredientsCollection.document(snapshot.getId())
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

    public void bindData(String imageUrl, String title, String id, boolean isSaved) {
        // Set the image URL as a tag to the imageView
        imageView.setTag(imageUrl);

        // Set the title to the titleTextView
        titleTextView.setText(title);

        idTextView.setText(id);

        // Set the saved state of the button
        this.isSaved = isSaved;

        checkDataInFirestore(imageUrl);
    }

    private void checkDataInFirestore(String imageUrl) {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DocumentReference userDocument = firestore.collection("users").document(userId);
            CollectionReference savedIngredientsCollection = userDocument.collection("saved_ingredients");

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




