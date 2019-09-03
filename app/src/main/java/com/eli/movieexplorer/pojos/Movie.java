package com.eli.movieexplorer.pojos;

public class Movie {

    private String genre;

    private int movieID;

    private String title;

    public String getGenre ()
    {
        return genre;
    }

    public void setGenre (String genre)
    {
        this.genre = genre;
    }

    public int getMovieID ()
    {
        return movieID;
    }

    public void setMovieID (int movieID)
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
