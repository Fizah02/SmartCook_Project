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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Recommendation1 extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<RecommendationModel, RecommendationViewHolder> adapter;
    private boolean isSaved = false;

    public Recommendation1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommendation, container, false);

        recyclerView = view.findViewById(R.id.recommendationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Recipes").child("Malay");

        FirebaseRecyclerOptions<RecommendationModel> options = new FirebaseRecyclerOptions.Builder<RecommendationModel>()
                .setQuery(databaseReference.limitToLast(5), RecommendationModel.class)
                .build();


        adapter = new FirebaseRecyclerAdapter<RecommendationModel, RecommendationViewHolder>(options) {

            @Override
            public void onDataChanged() {
                super.onDataChanged();

                if (adapter != null) {
                    // Shuffle the data after it has been loaded
                    List<RecommendationModel> dataList = new ArrayList<>();
                    for (int i = 0; i < 5; i++) {
                        dataList.add(getItem(i));
                    }

                    Collections.shuffle(dataList);
                    // Now dataList contains the shuffled data
                }
            }

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
                        Intent intent = new Intent(getContext(), Ingredients_Malay.class);
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
