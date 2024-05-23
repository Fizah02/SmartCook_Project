package com.example.smartcook;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class IngredientViewHolder extends RecyclerView.ViewHolder {
    public ImageView ingredientImageView;
    public TextView ingredientNameTextView;
    public TextView ingredientAmountTextView;

    public IngredientViewHolder(@NonNull View itemView) {
        super(itemView);

        ingredientImageView = itemView.findViewById(R.id.ingredientImageView);
        ingredientNameTextView = itemView.findViewById(R.id.ingredientNameTextView);
        ingredientAmountTextView = itemView.findViewById(R.id.ingredientAmountTextView);
    }

    public void bind(IngredientModel ingredient) {
        // Bind the data to the views
        Picasso.get().load(ingredient.getImageUrl()).into(ingredientImageView);
        ingredientNameTextView.setText(ingredient.getTitle());
        ingredientAmountTextView.setText(ingredient.getAmount());

    }

}
