package com.example.smartcook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private List<FeedbackModel> feedbackList;
    private FirebaseFirestore db;
    private ListenerRegistration userListener;
    private FirebaseAuth mAuth;
    private DocumentReference userRef;

    public FeedbackAdapter(List<FeedbackModel> feedbackList) {
        this.feedbackList = feedbackList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        FeedbackModel feedback = feedbackList.get(position);

        holder.usernameTextView.setText("Username: " + capitalizeFirstLetter(feedback.getUsername()));
        holder.dateTextView.setText("Date: " + feedback.getDate());
        holder.timeTextView.setText("Time: " +feedback.getCurrentTime());
        holder.feedbackContentTextView.setText(feedback.getFeedbackContent());
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, dateTextView, timeTextView, feedbackContentTextView;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            feedbackContentTextView = itemView.findViewById(R.id.feedbackContentTextView);
        }
    }

    public void updateData(ArrayList<FeedbackModel> feedbackList) {
        // Update your adapter's dataset with the new feedback data
        this.feedbackList.clear();
        this.feedbackList.addAll(feedbackList);
        notifyDataSetChanged();
    }

    public void clearListeners() {
        // Remove Firestore snapshot listener to avoid memory leaks
        if (userListener != null) {
            userListener.remove();
        }
    }
}
