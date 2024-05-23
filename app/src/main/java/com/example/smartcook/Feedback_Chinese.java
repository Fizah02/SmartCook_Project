package com.example.smartcook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Feedback_Chinese extends AppCompatActivity {

    private EditText editTextFeedback;
    private RecyclerView recyclerView;
    private Button submit;
    private FeedbackAdapter feedbackAdapter;

    private FirebaseAuth auth;
    private DatabaseReference feedbackReference;
    private FirebaseFirestore db;
    private ListenerRegistration userListener;
    private FirebaseAuth mAuth;
    private DocumentReference userRef;

    String recipeId;
    private String feedbackId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Intent intent = getIntent();
        if (intent != null) {
            recipeId = intent.getStringExtra("recipeId");
        }

        auth = FirebaseAuth.getInstance();
        feedbackReference = FirebaseDatabase.getInstance().getReference().child("Recipes").child("Chinese").child(recipeId).child("feedbacks");

        editTextFeedback = findViewById(R.id.editTextFeedback);
        recyclerView = findViewById(R.id.recyclerView);
        submit = findViewById(R.id.btn_submit);

        feedbackAdapter = new FeedbackAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(feedbackAdapter);

        feedbackReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<FeedbackModel> feedbackList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FeedbackModel feedback = snapshot.getValue(FeedbackModel.class);
                    if (feedback != null) {
                        feedbackList.add(feedback);
                    }
                }
                feedbackAdapter.updateData(feedbackList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Feedback_Chinese.this, "Failed to retrieve feedback data", Toast.LENGTH_SHORT).show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendFeedback(view);
            }
        });
    }

    public void onSendFeedback(View view) {
        String feedbackText = editTextFeedback.getText().toString().trim();

        if (!feedbackText.isEmpty()) {
            FirebaseUser user = auth.getCurrentUser();

            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            if (user != null) {
                String userId = mAuth.getCurrentUser().getUid();

                userRef = db.collection("users").document(userId);
                userRef.addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        // Handle errors
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("name");

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                        String date = sdf.format(new Date());
                        String time = df.format(new Date());

                        feedbackId = feedbackReference.push().getKey();
                        FeedbackModel feedback = new FeedbackModel(username, date, time, feedbackText);

                        feedbackReference.child(feedbackId).setValue(feedback);

                        editTextFeedback.setText("");

                        Toast.makeText(this, "Feedback sent!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter feedback.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Feedback_Chinese.this, Ingredients_Chinese.class);
        intent.putExtra("recipeId", recipeId);
        startActivity(intent);
        finish(); // Finish the current activity
    }

}
