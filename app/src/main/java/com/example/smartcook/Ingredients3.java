package com.example.smartcook;

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

public class Ingredients3 extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<MenuModel2, MenuViewHolder2> adapter;
    private boolean isSaved = false; // To track if the data is saved

    public Ingredients3() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ingredients, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewIngredients);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Grains and Pasta");

        // Set the initial state of the button

        FirebaseRecyclerOptions<MenuModel2> options = new FirebaseRecyclerOptions.Builder<MenuModel2>()
                .setQuery(databaseReference, MenuModel2.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<MenuModel2, MenuViewHolder2>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder2 holder, int position, @NonNull MenuModel2 model) {
                // Bind data to your ViewHolder
                String ingredientId = getSnapshots().getSnapshot(holder.getAdapterPosition()).getKey();

                Picasso.get().load(model.getImageUrl()).into(holder.imageView);
                holder.titleTextView.setText(model.getTitle());
                holder.idTextView.setText(ingredientId);

                String imageUrl = model.getImageUrl();
                String title = model.getTitle();
                String id = ingredientId;

                holder.bindData(imageUrl, title, id, isSaved);

            }


            @NonNull
            @Override
            public MenuViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listingredients, parent, false);
                return new MenuViewHolder2(view);
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
