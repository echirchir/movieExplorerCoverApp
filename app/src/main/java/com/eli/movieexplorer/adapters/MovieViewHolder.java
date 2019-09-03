package com.eli.movieexplorer.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eli.movieexplorer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.movie_title)
    TextView title;

    @BindView(R.id.genre)
    TextView genre;

    public MovieViewHolder(@NonNull View itemView) {

        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
