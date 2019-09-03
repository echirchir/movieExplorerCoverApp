package com.eli.movieexplorer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eli.movieexplorer.model.Movie;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/*
* The Movie Adapter to load movies using a view holder
* Also handles dynamic searches through the activity's searchview widget
* */
public class MovieAdapter extends RecyclerView.Adapter<MovieViewHolder>{

    private List<Movie> movies;

    /*
    * Custom constructor
    * */
    public MovieAdapter(List<Movie> movies) {
        this.movies = new ArrayList<>(movies);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_card, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {

        holder.title.setText(movies.get(position).getTitle());
        holder.genre.setText("GENRE: ".concat(movies.get(position).getGenre()));

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void clear() {
        movies.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Movie> list) {
        movies.addAll(list);
        notifyDataSetChanged();
    }

    public void setModels(List<Movie> customers){
        movies = new ArrayList<>(customers);
    }

    public void animateTo(List<Movie> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<Movie> newModels) {
        for (int i = movies.size() - 1; i >= 0; i--) {
            final Movie model = movies.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Movie> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Movie model = newModels.get(i);
            if (!movies.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Movie> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Movie model = newModels.get(toPosition);
            final int fromPosition = movies.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private Movie removeItem(int position) {
        final Movie model = movies.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    private void addItem(int position, Movie model) {
        movies.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final Movie model = movies.remove(fromPosition);
        movies.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public Movie getItem(int position){
        return movies.get(position);
    }
}
