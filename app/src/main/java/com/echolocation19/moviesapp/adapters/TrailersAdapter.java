package com.echolocation19.moviesapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.echolocation19.moviesapp.R;
import com.echolocation19.moviesapp.data.Trailer;

import java.util.ArrayList;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersViewHolder> {

  private ArrayList<Trailer> trailers;

  public void setTrailers(ArrayList<Trailer> trailers) {
    this.trailers = trailers;
    notifyDataSetChanged();
  }

  private OnTrailerClickListener onTrailerClickListener;

  public interface OnTrailerClickListener {
    void onTrailerClick(String url);
  }

  public void setOnTrailerClickListener(OnTrailerClickListener onTrailerClickListener) {
    this.onTrailerClickListener = onTrailerClickListener;
  }

  @NonNull
  @Override
  public TrailersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
    return new TrailersViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull TrailersViewHolder holder, int position) {
    Trailer trailer = trailers.get(position);
    holder.textViewVideoTitle.setText(trailer.getName());
  }

  @Override
  public int getItemCount() {
    return trailers.size();
  }

  public class TrailersViewHolder extends RecyclerView.ViewHolder {

    private TextView textViewVideoTitle;

    public TrailersViewHolder(@NonNull View itemView) {
      super(itemView);
      textViewVideoTitle = itemView.findViewById(R.id.textViewVideoTitle);
      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (onTrailerClickListener != null) {
            onTrailerClickListener.onTrailerClick(trailers.get(getAdapterPosition()).getKey());
          }
        }
      });
    }
  }
}
