package com.eli.movieexplorer.model;

public class Movie {

    private String genre;

    private String movieID;

    private String title;

    public String getGenre ()
    {
        return genre;
    }

    public void setGenre (String genre)
    {
        this.genre = genre;
    }

    public String getMovieID ()
    {
        return movieID;
    }

    public void setMovieID (String movieID)
    {
        this.movieID = movieID;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

}
