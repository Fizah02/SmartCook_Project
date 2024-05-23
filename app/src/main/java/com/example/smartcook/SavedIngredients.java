package com.example.smartcook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SavedIngredients extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button generateButton;
    private Button addIngredientsButton;
    private FirestoreRecyclerAdapter<MenuModel2, MenuViewHolder3> adapter;
    private FirebaseFirestore firestore;
    public String recipeId;
    private boolean isSaved = false;
    private boolean isTick = false;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_ingredients);

        recyclerView = findViewById(R.id.recyclerView);
        generateButton = findViewById(R.id.generateButton);
        addIngredientsButton = findViewById(R.id.addIngredientsButton);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Get the current user's ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the "saved_ingredients" for the current user
        // Replace your existing databaseReference with Firestore CollectionReference
        CollectionReference collectionReference = firestore.collection("users")
                .document(userId)
                .collection("saved_ingredients");

        FirestoreRecyclerOptions<MenuModel2> options = new FirestoreRecyclerOptions.Builder<MenuModel2>()
                .setQuery(collectionReference, MenuModel2.class)
                .build();


        adapter = new FirestoreRecyclerAdapter<MenuModel2, MenuViewHolder3>(options) {
            @NonNull
            @Override
            public MenuViewHolder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = getLayoutInflater().inflate(R.layout.item_savedingredients, parent, false);
                return new MenuViewHolder3(itemView);
            }

            @Override
            protected void onBindViewHolder(MenuViewHolder3 holder, int position, MenuModel2 model) {
                // Bind data to your ViewHolder

                Picasso.get().load(model.getImageUrl()).into(holder.imageView);
                holder.titleTextView.setText(model.getTitle());
                holder.idTextView.setText(model.getId());

                String imageUrl = model.getImageUrl();
                String title = model.getTitle();
                String id = model.getId();

                holder.bindData(imageUrl, title, id, isSaved);
                holder.bindTickData(imageUrl,title, id, isTick);
            }
        };

        recyclerView.setAdapter(adapter);

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                filterIngredients();

                Intent intent = new Intent(SavedIngredients.this, GenerateRecipes.class);
                intent.putExtra("recipeId", recipeId);
                startActivity(intent);

                // Display the recipe details (you can pass them to the RecipeDetailsActivity or update UI directly)

            }
        });

        addIngredientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the ListIngredientsActivity when the "Add Ingredients" button is clicked
                Intent intent = new Intent(SavedIngredients.this, ListIngredients.class);
                startActivity(intent);
            }
        });

    }

    private void displayRecipeDetails(String imageUrl, String title, String owner, String time, String id, String category, String recipeId) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            CollectionReference savedRecipesCollection = firestore.collection("users").document(userId).collection("saved_recipes");

            // Check if the recipe with the given ID already exists
            savedRecipesCollection.whereEqualTo("id", recipeId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                // Recipe with the given ID doesn't exist, save it
                                saveRecipeDetails(savedRecipesCollection, imageUrl, title, owner, time, id, category, recipeId);
                            } else {
                                // Recipe with the given ID already exists, inform the user or take further action
                                Log.d("SavedIngredients", "Recipe with ID " + recipeId + " already saved");
                                Toast.makeText(SavedIngredients.this, "Recipe already saved!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("SavedIngredients", "Error checking for existing recipe: " + task.getException());
                            // Handle the error, e.g., show an error message to the user
                            Toast.makeText(SavedIngredients.this, "Failed to check for existing recipe", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void saveRecipeDetails(CollectionReference savedRecipesCollection, String imageUrl, String title, String owner, String time, String id, String category, String recipeId) {
        // Create a new document for the saved recipe
        Map<String, Object> savedRecipeData = new HashMap<>();
        savedRecipeData.put("imageUrl", imageUrl);
        savedRecipeData.put("title", title);
        savedRecipeData.put("owner", owner);
        savedRecipeData.put("time", time);
        savedRecipeData.put("id", recipeId);
        savedRecipeData.put("category", category);
        savedRecipeData.put("recipeId", recipeId);

        savedRecipesCollection.add(savedRecipeData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("SavedIngredients", "Recipe details saved in Firestore. Document ID: " + documentReference.getId());
                    // Now you can inform the user or take further action
                    Toast.makeText(SavedIngredients.this, "Recipe saved!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("SavedIngredients", "Error saving recipe details in Firestore", e);
                    // Handle the error, e.g., show an error message to the user
                    Toast.makeText(SavedIngredients.this, "Failed to save recipe details", Toast.LENGTH_SHORT).show();
                });
    }

    private void filterIngredients(){
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Reference to the generate_ingredients collection for the current user
            db.collection("users").document(userId)
                    .collection("generate_ingredients")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                // List to store ingredient IDs
                                List<String> ingredientIds = new ArrayList<>();

                                for (DocumentSnapshot document : task.getResult()) {
                                    // Add each ingredient ID to the list
                                    ingredientIds.add(document.getString("id"));
                                }

                                Log.d("GenerateRecipes", "Ingredient IDs from Firestore: " + ingredientIds);

                                // Search for recipe IDs and ingredients in the Realtime Database under "Malay"
                                for (String ingredientId : ingredientIds) {
                                    searchRecipesInAllCategories(ingredientId);
                                }
                            } else {
                                Log.e("GenerateRecipes", "Error getting documents from Firestore: ", task.getException());
                            }
                        }
                    });
        }
    }

    public void searchRecipesInAllCategories(String ingredientId) {
        DatabaseReference recipesReference = mDatabase.child("Recipes");

        recipesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        String category = categorySnapshot.getKey();
                        Log.d("GenerateRecipes", "Category: " + category);

                        // Navigate to the "recipes" node under each category
                        DataSnapshot categoryRecipesSnapshot = categorySnapshot;

                        for (DataSnapshot recipeSnapshot : categoryRecipesSnapshot.getChildren()) {
                            String recipeId = recipeSnapshot.getKey();
                            Log.d("GenerateRecipes", "Recipe ID: " + recipeId);

                            // Navigate to the "ingredients" node under each recipe
                            DataSnapshot recipeIngredientsSnapshot = recipeSnapshot.child("ingredients");

                            // Check if the current recipe contains the target ingredient ID
                            if (recipeIngredientsSnapshot.hasChild(ingredientId)) {
                                // Retrieve the data for the specific ingredient ID
                                Map<String, Object> ingredientDataMap = (Map<String, Object>) recipeIngredientsSnapshot.child(ingredientId).getValue();
                                if (ingredientDataMap != null) {
                                    // Now you can access specific fields in the HashMap
                                    String ingredientData = (String) ingredientDataMap.get("title");
                                    // get current id recipe here
                                    Log.d("GenerateRecipes", "Ingredient ID: " + ingredientId + ", Data: " + ingredientData);

                                    // Fetch and add recipe details to the list
                                    fetchAndAddRecipes(category, recipeId);
                                } else {
                                    Log.e("GenerateRecipes", "Ingredient data is null or not a HashMap");
                                }
                            }
                        }
                    }
                } else {
                    Log.d("GenerateRecipes", "No data found in Recipes");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("GenerateRecipes", "Error reading from Realtime Database: ", databaseError.toException());
            }
        });
    }

    Set<String> fetchedRecipeIds = new HashSet<>();

    public void fetchAndAddRecipes(String category, String recipeId) {
        DatabaseReference recipeReference = mDatabase.child("Recipes").child(category).child(recipeId);

        recipeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Check if the recipe ID has already been fetched
                    if (!fetchedRecipeIds.contains(recipeId)) {
                        // Mark this recipe ID as fetched
                        fetchedRecipeIds.add(recipeId);

                        // Now you have the details of the specific recipe
                        // Fetch and add recipe details to the list
                        MenuModel menuModel = dataSnapshot.getValue(MenuModel.class);
                        if (menuModel != null) {
                            Log.d("SavedIngredients", "Recipe details: " + recipeId);
                            Log.d("GenerateRecipes", "Recipe details: " + menuModel.getOwner());
                            Log.d("GenerateRecipes", "Recipe details: " + menuModel.getImageUrl());
                            Log.d("GenerateRecipes", "Recipe details: " + menuModel.getTime());

                            String imageUrl = menuModel.getImageUrl();
                            String title = menuModel.getTitle();
                            String owner = menuModel.getOwner();
                            String time = menuModel.getTime();
                            String id = menuModel.getId();

                            displayRecipeDetails(imageUrl, title, owner, time, id, category, recipeId);

                        } else {
                            Log.e("GenerateRecipes", "Recipe data is null or not a MenuModel");
                        }
                    }
                } else {
                    Log.d("GenerateRecipes", "No data found for Recipe ID: " + recipeId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("GenerateRecipes", "Error reading from Realtime Database: ", databaseError.toException());
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Check if the user has navigated away from the application
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        removeGeneratedRecipes();
        Intent intent = new Intent(SavedIngredients.this, BottomTab.class);
        startActivity(intent);
        finish(); // Finish the current activity
    }

    private void removeGeneratedRecipes() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            CollectionReference generatedRecipesCollection = firestore.collection("users")
                    .document(userId)
                    .collection("generate_ingredients");

            generatedRecipesCollection
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Delete each document in the collection
                                firestore.collection("users")
                                        .document(userId)
                                        .collection("generate_ingredients")
                                        .document(document.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> Log.d("SavedIngredients", "Generated recipes deleted"))
                                        .addOnFailureListener(e -> Log.e("SavedIngredients", "Error deleting generated recipes", e));
                            }
                        } else {
                            Log.e("SavedIngredients", "Error getting generated recipes: ", task.getException());
                        }
                    });
        }
    }


}
