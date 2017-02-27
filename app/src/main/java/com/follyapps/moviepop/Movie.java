package com.follyapps.moviepop;

import java.io.Serializable;
import java.net.URL;


public class Movie {

    private String mPoster = "poster_path";
    private String mOverview = "overview";
    private String mDate = "release_date";
    private String mTitle = "original_title";
    private String mId = "id";
    private String mAverage = "vote_average";
    private String mAuthor = "author";
    private String mContent = "content";
    private String mKey = "content";


    // Constructs a new Movie object.
    public Movie(String poster, String overview, String release_date, String original_title,
                 String id, String vote_average, String author, String content, String key) {
        mPoster = poster;
        mOverview = overview;
        mDate = release_date;
        mTitle = original_title;
        mId = id;
        mAverage = vote_average;
        mAuthor = author;
        mContent = content;
        mKey = key;
    }

    // getter methods to return the data
    public String getPoster() {
        return mPoster;
    }
    public String getOverview() {
        return mOverview;
    }
    public String getReleaseDate() {
        return mDate;
    }
    public String getOriginalTitle() {return mTitle;}
    public String getId() {
        return mId;
    }
    public String getVoteAverage() {
        return mAverage;
    }
    public String getAuthor() {
        return mAuthor;
    }
    public String getContent() {
        return mContent;
    }
    public String getKey() {
        return mKey;
    }
}
