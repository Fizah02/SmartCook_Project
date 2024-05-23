package com.example.smartcook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class NewRecipes extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<NewRecipesModel, NewRecipesViewHolder> adapter;
    public boolean isSaved = false;

    public NewRecipes() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newrecipes, container, false);

        recyclerView = view.findViewById(R.id.newRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Initialize the adapter
        initAdapter();

        // Load one item from each category
        loadOneItemFromAllCategories();

        return view;
    }

    private void initAdapter() {
        FirebaseRecyclerOptions<NewRecipesModel> options = new FirebaseRecyclerOptions.Builder<NewRecipesModel>()
                .setQuery(databaseReference, NewRecipesModel.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<NewRecipesModel, NewRecipesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NewRecipesViewHolder holder, int position, @NonNull NewRecipesModel model) {
                // Bind data to your ViewHolder
                String recipeId = getSnapshots().getSnapshot(holder.getAdapterPosition()).getKey();
                Picasso.get().load(model.getImageUrl()).into(holder.imageView);
                holder.titleTextView.setText(model.getTitle());
                holder.ownerTextView.setText(model.getOwner());
                holder.idTextView.setText(recipeId);

                // Set click listener to open the RecipeDetails activity
                holder.recipe.setOnClickListener(view -> {
                    // Reference to the "favourite_recipes" collection for the current user
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Recipes");

                    String id = getSnapshots().getSnapshot(holder.getAdapterPosition()).getKey();
                    // Get the document corresponding to the clicked recipe ID
                    databaseReference.child(recipeId).get().addOnCompleteListener(task -> {
                                // Check if the "id" field starts with 'M'
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

                    });
                });
                String imageUrl = model.getImageUrl();
                String title = model.getTitle();
                String owner = model.getOwner();
                String id = recipeId;

                holder.bindData(imageUrl, title, owner, id, isSaved);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount(); // Or return the size of your dataset
            }

            @NonNull
            @Override
            public NewRecipesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_newrecipes, parent, false);
                return new NewRecipesViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
    }

    private void openRecipeActivityMalay(String recipeId) {
        Intent intent = new Intent(getContext(), Ingredients_Malay.class);
        intent.putExtra("recipeId", recipeId);
        startActivity(intent);

    }

    private void openRecipeActivityIndian(String recipeId) {
        Intent intent = new Intent(getContext(), Ingredients_Indian.class);
        intent.putExtra("recipeId", recipeId);
        startActivity(intent);

    }

    private void openRecipeActivityChinese(String recipeId) {
        Intent intent = new Intent(getContext(), Ingredients_Chinese.class);
        intent.putExtra("recipeId", recipeId);
        startActivity(intent);

    }

    private void openRecipeActivityItalian(String recipeId) {
        Intent intent = new Intent(getContext(), Ingredients_Italian.class);
        intent.putExtra("recipeId", recipeId);
        startActivity(intent);

    }

    private void openRecipeActivityKorean(String recipeId) {
        Intent intent = new Intent(getContext(), Ingredients_Korean.class);
        intent.putExtra("recipeId", recipeId);
        startActivity(intent);

    }

    private void loadOneItemFromAllCategories() {
        List<String> categories = Arrays.asList("Malay", "Chinese", "Indian", "Italian", "Korean");

        for (String category : categories) {
            loadOneItemFromCategory(category);
        }
    }

    private void loadOneItemFromCategory(String category) {
        Query databaseReference = FirebaseDatabase.getInstance().getReference().child("Recipes").child(category).limitToLast(1);

        // Update the adapter with the new query
        adapter.updateOptions(new FirebaseRecyclerOptions.Builder<NewRecipesModel>()
                .setQuery(databaseReference, NewRecipesModel.class)
                .build());
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
