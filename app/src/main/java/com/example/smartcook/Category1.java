package com.example.smartcook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Category1 extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<MenuModel, MenuViewHolder> adapter;
    private boolean isSaved = false;

    public Category1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Recipes").child("Malay");

        FirebaseRecyclerOptions<MenuModel> options = new FirebaseRecyclerOptions.Builder<MenuModel>()
                .setQuery(databaseReference, MenuModel.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<MenuModel, MenuViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull MenuModel model) {
                // Bind data to your ViewHolder
                String recipeId = getSnapshots().getSnapshot(holder.getAdapterPosition()).getKey();

                Picasso.get().load(model.getImageUrl()).into(holder.imageView);
                holder.titleTextView.setText(model.getTitle());
                holder.ownerTextView.setText(model.getOwner());
                holder.timeTextView.setText(model.getTime());
                holder.idTextView.setText(recipeId);

                // Set click listener to open the RecipeDetails activity
                holder.recipe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Get the clicked recipe ID

                        isSaved = !isSaved;

                        if (isSaved) {
                            holder.checkAndAddToHistory();
                        }
                        // Intent to RecipeDetailsActivity with the recipe ID
                        Intent intent = new Intent(getContext(), Ingredients_Malay.class);
                        intent.putExtra("recipeId", recipeId);
                        startActivity(intent);
                    }
                });

                String imageUrl = model.getImageUrl();
                String title = model.getTitle();
                String owner = model.getOwner();
                String time = model.getTime();
                String id = recipeId;

                holder.bindData(imageUrl, title, owner, time, id, isSaved);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount(); // Or return the size of your dataset
            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listrecipes, parent, false);
                return new MenuViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);

        return view;
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
