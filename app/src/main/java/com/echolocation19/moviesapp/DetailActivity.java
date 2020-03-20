package com.echolocation19.moviesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.echolocation19.moviesapp.adapters.ReviewAdapter;
import com.echolocation19.moviesapp.adapters.TrailersAdapter;
import com.echolocation19.moviesapp.data.FavouriteMovie;
import com.echolocation19.moviesapp.data.MainViewModel;
import com.echolocation19.moviesapp.data.Movie;
import com.echolocation19.moviesapp.data.Review;
import com.echolocation19.moviesapp.data.Trailer;
import com.echolocation19.moviesapp.utils.JSONUtils;
import com.echolocation19.moviesapp.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

  private ImageView imageViewAddToFavourite;
  private ImageView imageViewBigPoster;
  private TextView textViewTitle;
  private TextView textViewOriginalTitle;
  private TextView textViewRating;
  private TextView textViewReleaseDate;
  private TextView textViewOverview;
  private ScrollView scrollViewInfo;

  private RecyclerView recyclerViewTrailers;
  private RecyclerView recyclerViewReviews;
  private ReviewAdapter reviewAdapter;
  private TrailersAdapter trailersAdapter;

  private int id;

  private MainViewModel viewModel;
  private Movie movie;
  private FavouriteMovie favouriteMovie;

  private String lang;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
    lang = Locale.getDefault().getLanguage();

    imageViewAddToFavourite = findViewById(R.id.imageViewAddToFavourite);
    imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
    textViewTitle = findViewById(R.id.textViewTitle);
    textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
    textViewRating = findViewById(R.id.textViewRating);
    textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
    textViewOverview = findViewById(R.id.textViewOverview);
    scrollViewInfo = findViewById(R.id.scrollViewInfo);
    Intent intent = getIntent();
    if (intent != null && intent.hasExtra("id")) {
      id = intent.getIntExtra("id", -1);
    } else {
      finish();
    }
    viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    movie = viewModel.getMovieById(id);
    Picasso.get().load(movie.getBigPosterPath()).into(imageViewBigPoster);
    textViewTitle.setText(movie.getTitle());
    textViewOriginalTitle.setText(movie.getOriginalTitle());
    textViewOverview.setText(movie.getOverview());
    textViewReleaseDate.setText(movie.getReleaseDate());
    textViewRating.setText(Double.toString(movie.getVoteAverage()));
    setFavourite();

    recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);
    recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
    reviewAdapter = new ReviewAdapter();
    trailersAdapter = new TrailersAdapter();
    trailersAdapter.setOnTrailerClickListener(new TrailersAdapter.OnTrailerClickListener() {
      @Override
      public void onTrailerClick(String url) {
        Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intentToTrailer);
      }
    });
    recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
    recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
    recyclerViewTrailers.setAdapter(trailersAdapter);
    recyclerViewReviews.setAdapter(reviewAdapter);
    JSONObject jsonObjectTrailers = NetworkUtils.getJSONForVideos(movie.getId(), lang);
    JSONObject jsonObjectReviews = NetworkUtils.getJSONForReviews(movie.getId(), lang);
    ArrayList<Trailer> trailers = JSONUtils.getTrailersFromJSON(jsonObjectTrailers);
    ArrayList<Review> reviews = JSONUtils.getReviewsFromJSON(jsonObjectReviews);
    trailersAdapter.setTrailers(trailers);
    reviewAdapter.setReviews(reviews);
    scrollViewInfo.smoothScrollTo(0, 0);
  }

  public void onClickChangeFavourite(View view) {
    if (favouriteMovie == null) {
      viewModel.insertFavouriteMovie(new FavouriteMovie(movie));
      Toast.makeText(this, R.string.add_to_favourite, Toast.LENGTH_SHORT).show();
    } else {
      viewModel.deleteFavouriteMovie(favouriteMovie);
      Toast.makeText(this, R.string.remove_from_favourites, Toast.LENGTH_SHORT).show();
    }
    setFavourite();
  }

  private void setFavourite() {
    favouriteMovie = viewModel.getFavouriteMovieById(id);
    if (favouriteMovie == null) {
      imageViewAddToFavourite.setImageResource(R.drawable.favourite_add_to);
    } else {
      imageViewAddToFavourite.setImageResource(R.drawable.favourite_remove);
    }
  }

//  private void downloadData(int methodOfSort, int page) {
//    JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(methodOfSort, page, lang);
//    ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
//    if (movies != null && !movies.isEmpty()) {
//      viewModel.deleteAllMovies();
//      for (Movie movie : movies) {
//        viewModel.insertMovie(movie);
//      }
//    }
//  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.itemMain:
        startActivity(new Intent(DetailActivity.this, MainActivity.class));
        break;
      case R.id.itemFavourite:
        startActivity(new Intent(DetailActivity.this, FavouriteActivity.class));
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}
