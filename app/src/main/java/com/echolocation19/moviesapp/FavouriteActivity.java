package com.echolocation19.moviesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.echolocation19.moviesapp.adapters.MovieAdapter;
import com.echolocation19.moviesapp.data.FavouriteMovie;
import com.echolocation19.moviesapp.data.MainViewModel;
import com.echolocation19.moviesapp.data.Movie;
import com.echolocation19.moviesapp.utils.JSONUtils;
import com.echolocation19.moviesapp.utils.NetworkUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {

  private RecyclerView recyclerViewFavouriteMovies;
  private MovieAdapter adapter;
  private MainViewModel viewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_favourite);
    recyclerViewFavouriteMovies = findViewById(R.id.recyclerFavouriteMovies);
    recyclerViewFavouriteMovies.setLayoutManager(new GridLayoutManager(this, 2));
    adapter = new MovieAdapter();
    recyclerViewFavouriteMovies.setAdapter(adapter);
    viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    LiveData<List<FavouriteMovie>> favouriteMovies = viewModel.getFavouriteMovies();
    favouriteMovies.observe(this, new Observer<List<FavouriteMovie>>() {
      @Override
      public void onChanged(List<FavouriteMovie> favouriteMovies) {
        List<Movie> movies = new ArrayList<>();
        if (favouriteMovies != null) {
          movies.addAll(favouriteMovies);
          adapter.setMovies(movies);
        }
      }
    });

    adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
      @Override
      public void onPosterClick(int position) {
        Movie movie = adapter.getMovies().get(position);
        Intent intent = new Intent(FavouriteActivity.this, DetailActivity.class);
        intent.putExtra("id", movie.getId());
        startActivity(intent);
      }
    });
  }

//  private void downloadData(int methodOfSort, int page) {
//    JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(methodOfSort, 1);
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
        startActivity(new Intent(FavouriteActivity.this, MainActivity.class));
        break;
      case R.id.itemFavourite:
        startActivity(new Intent(FavouriteActivity.this, FavouriteActivity.class));
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}
