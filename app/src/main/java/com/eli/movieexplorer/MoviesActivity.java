package com.eli.movieexplorer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.eli.movieexplorer.helpers.NetworkChecker;
import com.eli.movieexplorer.helpers.UTILITIES;
import com.eli.movieexplorer.pojos.Movie;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
* The Main activity that displays MOVIES
* Enables Search functionality
* 4th September, 2019
* @author Elisha Chirchir
* */

public class MoviesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.generic_empty_view)
    TextView noMovies;

    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    private List<com.eli.movieexplorer.model.Movie> movies;
    private MovieAdapter adapter;

    private NetworkChecker networkChecker;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        networkChecker = new NetworkChecker(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setOnRefreshListener(() -> {

            if (networkChecker.isConnected()) {
                new RefreshMoviesAsync().execute();
            } else {
                Toast.makeText(this, "Turn ON your Wi-fi or 4G data", Toast.LENGTH_LONG).show();
            }
        });

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    protected void onResume() {
        super.onResume();

        queryMovies();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    /*
    * Async operation to avoid overworking
    * The Main Thread; runs in the background
    * */
    public class RefreshMoviesAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            doHttpRequest();

            return null;
        }

        @Override
        protected void onPostExecute(String results) {

            super.onPostExecute(results);

            swipeRefreshLayout.setRefreshing(false);

            queryMovies();
        }
    }

    /*
    * Makes the HTTP GET request to fetch the movies;
    * */
    public void doHttpRequest() {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(UTILITIES.URL)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("Failure Response", mMessage);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String movies = response.body().string();

                Log.w("Success Response", movies);

                //store to local database;

                Realm localInstance = Realm.getDefaultInstance();

                Gson gson = new Gson();

                try {
                    JSONArray jsonArray = new JSONArray(movies);

                    int sizeOfMovies = jsonArray.length();

                    if ( sizeOfMovies > 0 ){

                        localInstance.beginTransaction();

                        for (int i = 0; i < sizeOfMovies; i++){

                            Movie movie = gson.fromJson(jsonArray.get(i).toString(), Movie.class);

                            int fetchedId = movie.getMovieID();

                            //update or insert a fresh
                            RealmResults<com.eli.movieexplorer.db.Movie> current = localInstance.where(com.eli.movieexplorer.db.Movie.class).findAll().sort("movie_id", Sort.ASCENDING);

                            com.eli.movieexplorer.db.Movie newMovie;

                            if (!current.isEmpty()){

                                com.eli.movieexplorer.db.Movie existing = localInstance.where(com.eli.movieexplorer.db.Movie.class).equalTo("movie_id", fetchedId).findFirst();

                                if (existing == null){

                                    newMovie = new com.eli.movieexplorer.db.Movie();
                                    newMovie.setGenre(movie.getGenre());
                                    newMovie.setMovie_id(movie.getMovieID());
                                    newMovie.setTitle(movie.getTitle());
                                    localInstance.copyToRealm(newMovie);

                                }

                            }else{

                                newMovie = new com.eli.movieexplorer.db.Movie();
                                newMovie.setMovie_id(movie.getMovieID());
                                newMovie.setGenre(movie.getGenre());
                                newMovie.setTitle(movie.getTitle());
                                localInstance.copyToRealm(newMovie);
                            }

                        }

                        localInstance.commitTransaction();
                        localInstance.close();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /*
    * Read movies from the Movie table
    *  and populates the adapter
    *
    * */
    private void queryMovies(){

        RealmResults<com.eli.movieexplorer.db.Movie> existingMovies = realm.where(com.eli.movieexplorer.db.Movie.class).findAll().sort("movie_id", Sort.ASCENDING);

        movies = new ArrayList<>();

        if (!existingMovies.isEmpty()){

            for (com.eli.movieexplorer.db.Movie movie : existingMovies){

                com.eli.movieexplorer.model.Movie model = new com.eli.movieexplorer.model.Movie();
                model.setGenre(movie.getGenre());
                model.setTitle(movie.getTitle());

                movies.add(model );
            }
        }

        adapter = new MovieAdapter(movies);

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (adapter.getItemCount() > 0) {
            noMovies.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.search, menu);

        final MenuItem item = menu.findItem(R.id.action_search);

        item.expandActionView();

        final SearchView searchView = (SearchView) item.getActionView();

        searchView.setQueryHint(getString(R.string.search));

        searchView.setOnQueryTextListener(this);

        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (newText.equals("")){
            queryMovies();
            return true;
        }else{
            final List<com.eli.movieexplorer.model.Movie> filteredModelList = filter(movies, newText);

            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            adapter.animateTo(filteredModelList);
            recyclerView.scrollToPosition(0);
            return true;
        }
    }

    private List<com.eli.movieexplorer.model.Movie> filter(List<com.eli.movieexplorer.model.Movie> models, String query) {

        query = query.toLowerCase();

        final List<com.eli.movieexplorer.model.Movie> filteredModelList = new ArrayList<>();

        if(query.equals("")) { return movies; }

        for (com.eli.movieexplorer.model.Movie model : models) {
            final String text = model.getTitle().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
    }
}
