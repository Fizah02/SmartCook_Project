package com.example.smartcook;

import android.annotation.SuppressLint;
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

public class Recommendation3 extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<RecommendationModel, RecommendationViewHolder> adapter;
    private boolean isSaved = false;

    public Recommendation3() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommendation, container, false);

        recyclerView = view.findViewById(R.id.recommendationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Recipes").child("Chinese");

        FirebaseRecyclerOptions<RecommendationModel> options = new FirebaseRecyclerOptions.Builder<RecommendationModel>()
                .setQuery(databaseReference.limitToLast(5), RecommendationModel.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<RecommendationModel, RecommendationViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RecommendationViewHolder holder, int position, @NonNull RecommendationModel model) {
                // Bind data to your ViewHolder
                String recipeId = getSnapshots().getSnapshot(holder.getAdapterPosition()).getKey();
                Picasso.get().load(model.getImageUrl()).into(holder.imageView);
                holder.titleTextView.setText(model.getTitle());
                holder.ownerTextView.setText(model.getOwner());
                holder.idTextView.setText(recipeId);

                // Set click listener to open the RecipeDetails activity
                holder.recipe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Get the clicked recipe ID

                        // Intent to RecipeDetailsActivity with the recipe ID
                        Intent intent = new Intent(getContext(), Ingredients_Chinese.class);
                        intent.putExtra("recipeId", recipeId);
                        startActivity(intent);
                    }
                });
                String imageUrl = model.getImageUrl();
                String title = model.getTitle();
                String owner = model.getOwner();
                String id = recipeId;

                holder.bindData(imageUrl, title,owner,id, isSaved);

                // Set click listener to open the RecipeDetailActivity

            }

            @Override
            public int getItemCount() {
                return super.getItemCount(); // Or return the size of your dataset
            }

            @NonNull
            @Override
            public RecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommendation, parent, false);
                return new RecommendationViewHolder(view);

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
