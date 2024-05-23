package com.example.smartcook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class GenerateRecipes extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<MenuModel, MenuViewHolder> adapter;
    private FirebaseFirestore firestore;
    private CollectionReference id;
    private String userId;  // Move userId to class level
    private boolean isSaved = false;
    String recipeId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);

        Intent intent = getIntent();
        if (intent != null) {
            recipeId = intent.getStringExtra("recipeId");
        }

        recyclerView = findViewById(R.id.recyclerView);
        firestore = FirebaseFirestore.getInstance();

        // Get the current user's ID
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the "favourite_recipes" for the current user
        CollectionReference collectionReference = firestore.collection("users")
                .document(userId)
                .collection("saved_recipes");

        FirestoreRecyclerOptions<MenuModel> options = new FirestoreRecyclerOptions.Builder<MenuModel>()
                .setQuery(collectionReference.whereNotEqualTo("id", recipeId), MenuModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<MenuModel, MenuViewHolder>(options) {
            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = getLayoutInflater().inflate(R.layout.item_listrecipes, parent, false);
                return new MenuViewHolder(itemView);
            }

            @Override
            protected void onBindViewHolder(MenuViewHolder holder, int position, MenuModel model) {
                String recipeId = getSnapshots().getSnapshot(holder.getAdapterPosition()).getId();

                Picasso.get().load(model.getImageUrl()).into(holder.imageView);
                holder.titleTextView.setText(model.getTitle());
                holder.ownerTextView.setText(model.getOwner());
                holder.timeTextView.setText(model.getTime());
                holder.idTextView.setText(model.getId());

                holder.recipe.setOnClickListener(view -> {
                    // Reference to the "favourite_recipes" collection for the current user
                    CollectionReference idcollection = firestore.collection("users")
                            .document(userId)
                            .collection("saved_recipes");

                    // Get the document corresponding to the clicked recipe ID
                    idcollection.document(recipeId).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Check if the "id" field starts with 'M'
                                String id = document.getString("id");
                                if (id != null && id.startsWith("M")) {
                                    // Call the method to handle opening the Ingredients_Malay activity
                                    openRecipeActivityMalay(id);
                                }
                                else if (id != null && id.startsWith("I")) {
                                    // Call the method to handle opening the Ingredients_Malay activity
                                    openRecipeActivityIndian(id);
                                }
                                else if (id != null && id.startsWith("C")) {
                                    // Call the method to handle opening the Ingredients_Malay activity
                                    openRecipeActivityChinese(id);
                                }
                                else if (id != null && id.startsWith("L")) {
                                    // Call the method to handle opening the Ingredients_Malay activity
                                    openRecipeActivityItalian(id);
                                }
                                else if (id != null && id.startsWith("K")) {
                                    // Call the method to handle opening the Ingredients_Malay activity
                                    openRecipeActivityKorean(id);
                                }
                            } else {
                                // Handle the case where the document does not exist
                                Toast.makeText(GenerateRecipes.this, "Recipe document not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle errors, if any
                            Toast.makeText(GenerateRecipes.this, "Firestore error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });


                String imageUrl = model.getImageUrl();
                String title = model.getTitle();
                String owner = model.getOwner();
                String time = model.getTime();
                String id = model.getId();

                holder.bindData(imageUrl, title, owner, time,id, isSaved);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount(); // Or return the size of your dataset
            }

        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void openRecipeActivityMalay(String recipeId) {
        Intent intent = new Intent(this, Ingredients_Malay.class);
        intent.putExtra("recipeId", recipeId);
        startActivity(intent);
    }

    private void openRecipeActivityIndian(String recipeId) {
        Intent intent = new Intent(this, Ingredients_Indian.class);
        intent.putExtra("recipeId", recipeId);
        startActivity(intent);
    }

    private void openRecipeActivityChinese(String recipeId) {
        Intent intent = new Intent(this, Ingredients_Chinese.class);
        intent.putExtra("recipeId", recipeId);
        startActivity(intent);
    }

    private void openRecipeActivityItalian(String recipeId) {
        Intent intent = new Intent(this, Ingredients_Italian.class);
        intent.putExtra("recipeId", recipeId);
        startActivity(intent);
    }

    private void openRecipeActivityKorean(String recipeId) {
        Intent intent = new Intent(this, Ingredients_Korean.class);
        intent.putExtra("recipeId", recipeId);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cleanupFirestoreData();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(GenerateRecipes.this, SavedIngredients.class);
        cleanupFirestoreData();
        startActivity(intent);
        finish(); // Finish the current activity
    }

    private void cleanupFirestoreData() {
        // Reference to the "saved_recipes" for the current user
        CollectionReference collectionReference = firestore.collection("users")
                .document(userId)
                .collection("saved_recipes");

        // Delete all documents in the "saved_recipes" collection
        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    collectionReference.document(document.getId()).delete();
                }
                Log.d("GenerateRecipes", "Firestore data deleted successfully");
            } else {
                Log.e("GenerateRecipes", "Error deleting Firestore data: ", task.getException());
            }
        });
    }
}
