package com.example.smartcook;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Procedures_Indian extends AppCompatActivity {

    private static final int REQUEST_WRITE_STORAGE = 112;

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private DatabaseReference recipeRef;
    private FirebaseRecyclerAdapter<ProcedureModel, ProcedureViewHolder> adapter;
    private VideoView recipeVideoView;
    String recipeId;
    private ImageButton playButton;
    private ImageButton forwardButton;
    private ImageButton backwardButton;

    private static final long BUTTON_VISIBILITY_DURATION = 3000; // 3 seconds
    private boolean isPlaying = false;
    private boolean areButtonsVisible = true;
    int resumePosition = 0;
    private static final long BUTTON_HIDE_DELAY = 3000; // 3 seconds
    private final Handler handler = new Handler();
    private final Runnable hideButtonsRunnable = this::hideButtons;
    private long touchDownTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipedetails_procedures);

        // Initialize views
        recipeVideoView = findViewById(R.id.recipeVideoView);
        recyclerView = findViewById(R.id.recyclerView);
        playButton = findViewById(R.id.playButton);
        forwardButton = findViewById(R.id.forwardButton);
        backwardButton = findViewById(R.id.rewindButton);

        Intent intent = getIntent();
        if (intent != null) {
            recipeId = intent.getStringExtra("recipeId");
        }

        playButton.setOnClickListener(v -> togglePlayPause());
        forwardButton.setOnClickListener(v -> forwardVideo());
        backwardButton.setOnClickListener(v -> backwardVideo());

        recipeVideoView.setOnTouchListener(new View.OnTouchListener() {
            private long touchDownTime = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchDownTime = System.currentTimeMillis();
                        showButtons();
                        handler.removeCallbacks(hideButtonsRunnable);
                        break;

                    case MotionEvent.ACTION_UP:
                        long touchDuration = System.currentTimeMillis() - touchDownTime;
                        if (touchDuration >= BUTTON_VISIBILITY_DURATION) {
                            hideButtons();
                        } else {
                            // Handle video view click
                            togglePlayPause();
                            // Schedule the hiding of buttons after the specified duration
                            handler.postDelayed(hideButtonsRunnable, BUTTON_HIDE_DELAY);
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        // Check if the touch event occurred on the left or right side
                        float touchX = event.getX();
                        float viewWidth = v.getWidth();
                        if (touchX < viewWidth / 2) {
                            // Touch on the left side, rewind the video
                            backwardVideo();
                        } else {
                            // Touch on the right side, forward the video
                            forwardVideo();
                        }
                        break;
                }
                return true; // Consume the event
            }
        });


        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Recipes").child("Indian").child(recipeId).child("procedure");

        FirebaseRecyclerOptions<ProcedureModel> options = new FirebaseRecyclerOptions.Builder<ProcedureModel>()
                .setQuery(databaseReference, ProcedureModel.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<ProcedureModel, ProcedureViewHolder>(options) {
            @Override
            protected void onBindViewHolder(ProcedureViewHolder holder, int position, ProcedureModel model) {
                holder.procedureNumberTextView.setText(model.getNo());
                holder.procedureDescriptionTextView.setText(model.getSteps());
                // Add other bindings based on your data model
            }

            @NonNull
            @Override
            public ProcedureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_procedure, parent, false);
                return new ProcedureViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);

        // Load and display the recipe video
        recipeRef = FirebaseDatabase.getInstance().getReference().child("Recipes").child("Indian").child(recipeId);

    }


    private void togglePlayPause() {
        if (isPlaying) {
            pauseVideo();
        } else {
            playVideo();
        }
    }

    private void playVideo() {
        if (!recipeVideoView.isPlaying()) {
            // If VideoView is not prepared, set the video URI and prepare it
            if (!isVideoViewPrepared()) {
                prepareVideoView();
            }

            // If the video was previously paused, resume from the last position
            if (!isPlaying && resumePosition > 0) {
                recipeVideoView.seekTo(resumePosition);
            }

            recipeVideoView.start();
            playButton.setImageResource(R.drawable.ic_baseline_pause_24);
            isPlaying = true;

        }
    }

    private void pauseVideo() {
        if (recipeVideoView.isPlaying()) {
            // Pause the video and save the current position
            recipeVideoView.pause();
            playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            isPlaying = false;
            resumePosition = recipeVideoView.getCurrentPosition();
        }
    }

    // Check if VideoView is prepared
    private boolean isVideoViewPrepared() {
        return recipeVideoView.getDuration() > 0;
    }

    // Prepare VideoView with the video URI
    private void prepareVideoView() {
        // Fetch recipe details from Realtime Database
        recipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming you have a "videoUrl" field in your recipe data
                    String gsVideoUrl = dataSnapshot.child("videoUrl").getValue(String.class);

                    // Convert gs:// URL to HTTP/HTTPS URL using Firebase Storage
                    StorageReference gsReference = FirebaseStorage.getInstance().getReferenceFromUrl(gsVideoUrl);
                    gsReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Now uri is the HTTP/HTTPS URL
                        Log.d("HttpVideoUrl", uri.toString());

                        // Set the video URI
                        recipeVideoView.setVideoURI(uri);

                        // Start playing the video
                        recipeVideoView.setOnPreparedListener(mp -> {
                            mp.setLooping(true); // Set looping if needed
                            // Do not start the video here; it will be started in playVideo() method
                        });
                    }).addOnFailureListener(e -> {
                        // Handle the failure to get the download URL
                        Log.e("DownloadUrlError", e.getMessage());
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }



    private void forwardVideo() {
        int currentPosition = recipeVideoView.getCurrentPosition();
        int newPosition = currentPosition + 10000; // 10 seconds forward

        if (newPosition < recipeVideoView.getDuration()) {
            recipeVideoView.seekTo(newPosition);
            resumePosition = newPosition; // Update resumePosition when forwarding
            Log.d("VideoPosition", "Forward: " + newPosition);
        } else {
            recipeVideoView.seekTo(recipeVideoView.getDuration());
            resumePosition = recipeVideoView.getDuration(); // Update resumePosition at the end of the video
            Log.d("VideoPosition", "Forward: End of video");
        }
    }

    private void backwardVideo() {
        int currentPosition = recipeVideoView.getCurrentPosition();
        int newPosition = currentPosition - 10000; // 10 seconds backward

        if (newPosition > 0) {
            recipeVideoView.seekTo(newPosition);
            resumePosition = newPosition; // Update resumePosition when rewinding
            Log.d("VideoPosition", "Rewind: " + newPosition);
        } else {
            recipeVideoView.seekTo(0);
            resumePosition = 0; // Update resumePosition at the start of the video
            Log.d("VideoPosition", "Rewind: Start of video");
        }
    }



    private void hideButtons() {
        // Hide the buttons with animation
        playButton.setVisibility(View.GONE);
        forwardButton.setVisibility(View.GONE);
        backwardButton.setVisibility(View.GONE);

        areButtonsVisible = false;

    }

    private void showButtons() {
        // Make the buttons visible with animation
        playButton.setVisibility(View.VISIBLE);
        forwardButton.setVisibility(View.VISIBLE);
        backwardButton.setVisibility(View.VISIBLE);

        areButtonsVisible = true;

        // Schedule the hiding of buttons after the specified duration
        handler.postDelayed(hideButtonsRunnable, BUTTON_HIDE_DELAY);
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
        Intent intent = new Intent(Procedures_Indian.this, Ingredients_Indian.class);
        intent.putExtra("recipeId", recipeId);
        startActivity(intent);
        finish(); // Finish the current activity
    }
}
