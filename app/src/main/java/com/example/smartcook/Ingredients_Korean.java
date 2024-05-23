package com.example.smartcook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Ingredients_Korean extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private DatabaseReference recipeRef;
    private FirebaseRecyclerAdapter<IngredientModel, IngredientViewHolder> adapter;
    private Button startCookingButton;
    private ImageButton commentButton;
    private ImageView recipeImageView;
    private String recipeId;
    private TextView titleTextView;
    private TextView ownerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipedetails_ingredients);

        // Initialize views
        recipeImageView = findViewById(R.id.recipeImageView);
        recyclerView = findViewById(R.id.recyclerView);
        startCookingButton = findViewById(R.id.btn_startCooking);
        commentButton = findViewById(R.id.btn_comment);
        titleTextView = findViewById(R.id.tv_title);
        ownerTextView = findViewById(R.id.tv_owner);

        Intent intent = getIntent();
        if (intent != null) {
            recipeId = intent.getStringExtra("recipeId");
        }

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Recipes").child("Korean").child(recipeId).child("ingredients");

        FirebaseRecyclerOptions<IngredientModel> options = new FirebaseRecyclerOptions.Builder<IngredientModel>()
                .setQuery(databaseReference, IngredientModel.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<IngredientModel, IngredientViewHolder>(options) {
            @Override
            protected void onBindViewHolder(IngredientViewHolder holder, int position, IngredientModel model) {
                // Bind data to the ViewHolder
                Picasso.get().load(model.getImageUrl()).into(holder.ingredientImageView);
                holder.ingredientNameTextView.setText(model.getTitle());

                // Convert the integer to a string before setting it as the text
                holder.ingredientAmountTextView.setText(model.getAmount());

                // Add other bindings based on your data model
            }

            @Override
            public int getItemCount() {
                return super.getItemCount(); // Or return the size of your dataset
            }

            @NonNull
            @Override
            public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredients, parent, false);
                return new IngredientViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);

        // Load and display the recipe image
        recipeRef = FirebaseDatabase.getInstance().getReference().child("Recipes").child("Korean").child(recipeId);

        // Fetch recipe details from Realtime Database
        recipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming you have a "imageUrl" field in your recipe data
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    String title = dataSnapshot.child("title").getValue(String.class);
                    String owner = dataSnapshot.child("owner").getValue(String.class);

                    // Load recipe image into the ImageView using Picasso or any other image loading library
                    Picasso.get().load(imageUrl).into(recipeImageView);
                    titleTextView.setText(title);
                    ownerTextView.setText(owner);

                }
            }

            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });

        // Set up the "Start Cooking" button click listener
        startCookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Ingredients_Korean.this, Procedures_Korean.class);
                intent.putExtra("recipeId", recipeId);
                startActivity(intent);
            }
        });

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Ingredients_Korean.this, Feedback_Korean.class);
                intent.putExtra("recipeId", recipeId);
                startActivity(intent);
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
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Ingredients_Korean.this, BottomTab.class);
        startActivity(intent);
        finish(); // Finish the current activity
    }
}
