package com.example.smartcook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class SavedIngredientsProfile extends Fragment {
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<MenuModel2, MenuViewHolder2> adapter;
    private FirebaseFirestore firestore;
    private FloatingActionButton fabIngredients;
    private boolean isSaved = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_ingredients, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        firestore = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext())); // Use requireContext() to get the context
        // Get the current user's ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the "saved_ingredients" for the current user
        CollectionReference collectionReference = firestore.collection("users")
                .document(userId)
                .collection("saved_ingredients");

        FirestoreRecyclerOptions<MenuModel2> options = new FirestoreRecyclerOptions.Builder<MenuModel2>()
                .setQuery(collectionReference, MenuModel2.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<MenuModel2, MenuViewHolder2>(options) {
            @NonNull
            @Override
            public MenuViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_listingredients, parent, false);
                return new MenuViewHolder2(itemView);
            }

            @Override
            protected void onBindViewHolder(MenuViewHolder2 holder, int position, MenuModel2 model) {
                // Bind data to your ViewHolder

                Picasso.get().load(model.getImageUrl()).into(holder.imageView);
                holder.titleTextView.setText(model.getTitle());
                holder.idTextView.setText(model.getId());

                String imageUrl = model.getImageUrl();
                String title = model.getTitle();
                String id = model.getId();

                holder.bindData(imageUrl, title, id, isSaved);
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

    @Override
    public void onPause() {
        super.onPause();
        adapter.stopListening();
    }
}
