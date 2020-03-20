package com.echolocation19.moviesapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.echolocation19.moviesapp.adapters.MovieAdapter;
import com.echolocation19.moviesapp.data.MainViewModel;
import com.echolocation19.moviesapp.data.Movie;
import com.echolocation19.moviesapp.utils.JSONUtils;
import com.echolocation19.moviesapp.utils.NetworkUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {

  private RecyclerView recyclerViewPosters;
  private MovieAdapter movieAdapter;
  private Switch switchSort;
  private TextView textViewPopularity;
  private TextView textViewTopRated;
  private ProgressBar progressBarLoading;

  private MainViewModel viewModel;

  private static final int LOADER_ID = 1919;
  private LoaderManager loaderManager;

  private static int page = 1;
  private static int methodOfSort;
  private static boolean isLoading = false;

  private static String lang;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    lang = Locale.getDefault().getLanguage();

    viewModel = new ViewModelProvider(this).get(MainViewModel.class);

    loaderManager = LoaderManager.getInstance(this);

    textViewPopularity = findViewById(R.id.textViewPopularity);
    textViewTopRated = findViewById(R.id.textViewTopRated);
    recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
    progressBarLoading = findViewById(R.id.progressBarLoading);
    recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, getColumnCount()));
    movieAdapter = new MovieAdapter();
    recyclerViewPosters.setAdapter(movieAdapter);

    switchSort = findViewById(R.id.switchSort);
    switchSort.setChecked(true);
    switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        page = 1;
        setMethodOfSort(isChecked);
      }
    });
    switchSort.setChecked(false);

    movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
      @Override
      public void onPosterClick(int position) {
        Movie movie = movieAdapter.getMovies().get(position);
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("id", movie.getId());
        startActivity(intent);
      }
    });

    movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
      @Override
      public void OnReachEnd() {
        if (!isLoading) {
          downloadData(methodOfSort, page, lang);
        }
      }
    });
    LiveData<List<Movie>> moviesFromLiveData = viewModel.getMovies();
    moviesFromLiveData.observe(this, new Observer<List<Movie>>() {
      @Override
      public void onChanged(List<Movie> movies) {
        if (page == 1) {
          movieAdapter.setMovies(movies);
        }
      }
    });
  }

  private int getColumnCount() {
    DisplayMetrics displayMetrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    int width = (int) (displayMetrics.widthPixels / displayMetrics.density);
    return width / 185 > 2 ? width / 185 : 2;
  }

  public void onClickSetPopularity(View view) {
    setMethodOfSort(false);
    switchSort.setChecked(false);
  }

  public void onClickSetTopRated(View view) {
    setMethodOfSort(true);
    switchSort.setChecked(true);
  }

  private void setMethodOfSort(boolean isTopRated) {
    if (isTopRated) {
      textViewTopRated.setTextColor(getResources().getColor(R.color.colorAccent));
      textViewPopularity.setTextColor(getResources().getColor(R.color.white_color));
      methodOfSort = NetworkUtils.TOP_RATED;
    } else {
      textViewPopularity.setTextColor(getResources().getColor(R.color.colorAccent));
      textViewTopRated.setTextColor(getResources().getColor(R.color.white_color));
      methodOfSort = NetworkUtils.POPULARITY;
    }
    downloadData(methodOfSort, page, lang);
  }

  private void downloadData(int methodOfSort, int page, String lang) {
    URL url = NetworkUtils.buildUrl(methodOfSort, page, lang);
    Bundle bundle = new Bundle();
    bundle.putString("url", url.toString());
    loaderManager.restartLoader(LOADER_ID, bundle, this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.itemMain:
        startActivity(new Intent(MainActivity.this, MainActivity.class));
        break;
      case R.id.itemFavourite:
        startActivity(new Intent(MainActivity.this, FavouriteActivity.class));
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @NonNull
  @Override
  public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle args) {
    NetworkUtils.JSONLoader jsonLoader = new NetworkUtils.JSONLoader(this, args);
    jsonLoader.setOnStartLoadingListener(new NetworkUtils.JSONLoader.OnStartLoadingListener() {
      @Override
      public void onStartLoading() {
        progressBarLoading.setVisibility(View.VISIBLE);
        isLoading = true;
      }
    });
    return jsonLoader;
  }

  @Override
  public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject data) {
    ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(data);
    if (movies != null && !movies.isEmpty()) {
      if (page == 1) {
        viewModel.deleteAllMovies();
        movieAdapter.clear();
      }
      for (Movie movie : movies) {
        viewModel.insertMovie(movie);
      }
      movieAdapter.addMovies(movies);
      page++;
    }
    isLoading = false;
    progressBarLoading.setVisibility(View.GONE);
    loaderManager.destroyLoader(LOADER_ID);
  }

  @Override
  public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

  }
}
