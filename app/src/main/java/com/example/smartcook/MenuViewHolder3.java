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

public class MenuViewHolder3 extends RecyclerView.ViewHolder {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    public ImageView imageView;
    public TextView titleTextView;
    public TextView idTextView;
    public ImageButton addMoreIngredients;
    public ImageButton savedIngredients;
    public ImageButton tickIngredients;
    boolean isSaved = false; // To track the saved state
    boolean isTick = false;

    public MenuViewHolder3(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.menu_image);
        titleTextView = itemView.findViewById(R.id.menu_title);
        idTextView = itemView.findViewById(R.id.ingredient_id);
        addMoreIngredients = itemView.findViewById(R.id.addIngredientsButton);
        savedIngredients = itemView.findViewById(R.id.add_button);
        tickIngredients = itemView.findViewById(R.id.tick_button);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        savedIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the saved state
                isSaved = !isSaved;
                updateButtonBackground();

                if (isSaved) {
                    // If data is saved, make the button green
                    savedIngredients.setBackgroundResource(R.drawable.green_saved_button);
                    // Save the image URL and title to Firestore
                    onFavoriteButtonClick(v);
                } else {
                    // If data is not saved, make the button white
                    savedIngredients.setBackgroundResource(R.drawable.white_saved_button);

                    onRemoveButtonClick(v);
                }
            }
        });

        tickIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the saved state
                isTick = !isTick;

                if (isTick) {
                    // If data is saved, make the button green

                    tickIngredients.setBackgroundResource(R.drawable.ic_baseline_check_circle_24);
                    onGenerateButtonClick(v);
                    // Save the image URL and title to Firestore

                } else {
                    // If data is not saved, make the button white

                    tickIngredients.setBackgroundResource(R.drawable.ic_baseline_check_circle_outline_24);
                    // Remove the data from Firestore
                    onRemoveTickButtonClick(v);

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
            savedIngredients.setBackgroundResource(R.drawable.white_saved_button);
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

        // Check if the data is saved in Firestore and update the button background accordingly
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

    private void updateTickButtonBackground() {
        if (isTick) {
            // If data is saved, make the button green
            tickIngredients.setBackgroundResource(R.drawable.ic_baseline_check_circle_24);
        } else {
            // If data is not saved, make the button white
            tickIngredients.setBackgroundResource(R.drawable.ic_baseline_check_circle_outline_24);
        }
    }

    public void onGenerateButtonClick(View view){
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            // Reference to the user's document
            DocumentReference userDocument = firestore.collection("users").document(userId);

            // Reference to the "favorites" sub-collection under the user's document
            CollectionReference tickCollection = userDocument.collection("generate_ingredients");

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
            tickCollection.add(dataToSave)
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

    private void onRemoveTickButtonClick(View v) {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            // Reference to the user's document
            DocumentReference userDocument = firestore.collection("users").document(userId);

            // Reference to the "saved_ingredients" sub-collection under the user's document
            CollectionReference tickIngredientsCollection = userDocument.collection("generate_ingredients");

            // Get the image URL from the views (you can use the image URL or another unique identifier)
            String imageUrl = imageView.getTag().toString();

            // Query the "saved_ingredients" sub-collection to find the document with the matching image URL
            tickIngredientsCollection.whereEqualTo("imageUrl", imageUrl)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            // Delete the document(s) that match the query
                            tickIngredientsCollection.document(snapshot.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Deletion successful. Update UI or provide a confirmation message.
                                        // For example, change the button background to indicate it's not saved.
                                        isSaved = false;
                                        updateTickButtonBackground();
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

    public void bindTickData(String imageUrl, String title, String id, boolean isTick) {
        // Set the image URL as a tag to the imageView
        imageView.setTag(imageUrl);

        // Set the title to the titleTextView
        titleTextView.setText(title);

        idTextView.setText(id);

        // Set the saved state of the button
        this.isTick = isTick;

        checkTickDataInFirestore(imageUrl);
    }

    private void checkTickDataInFirestore(String imageUrl) {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DocumentReference userDocument = firestore.collection("users").document(userId);
            CollectionReference tickIngredientsCollection = userDocument.collection("generate_ingredients");

            // Query the "saved_ingredients" sub-collection to find the document with the matching image URL
            tickIngredientsCollection.whereEqualTo("imageUrl", imageUrl)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Data exists in Firestore, make the button green
                            isTick = true;
                            updateTickButtonBackground();
                        } else {
                            // Data does not exist in Firestore, make the button white
                            isTick = false;
                            updateTickButtonBackground();
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

