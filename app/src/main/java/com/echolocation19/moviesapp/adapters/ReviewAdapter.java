package com.echolocation19.moviesapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.echolocation19.moviesapp.R;
import com.echolocation19.moviesapp.data.Review;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

  private ArrayList<Review> reviews;

  public void setReviews(ArrayList<Review> reviews) {
    this.reviews = reviews;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
    return new ReviewViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
    Review review = reviews.get(position);
    holder.textViewAuthor.setText(review.getAuthor());
    holder.textViewContent.setText(review.getContent());
  }

  @Override
  public int getItemCount() {
    return reviews.size();
  }

  public class ReviewViewHolder extends RecyclerView.ViewHolder {

    private TextView textViewAuthor;
    private TextView textViewContent;

    public ReviewViewHolder(@NonNull View itemView) {
      super(itemView);
      textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
      textViewContent = itemView.findViewById(R.id.textViewContent);
    }
  }
}
