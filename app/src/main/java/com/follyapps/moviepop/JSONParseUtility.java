package com.follyapps.moviepop;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.author;
import static android.R.attr.description;
import static android.R.attr.id;
import static com.follyapps.moviepop.R.id.review;


public class JSONParseUtility {

    public static List<Movie> getSimpleMovieStringsFromJson(Context context, String IMDBJsonStr, String origin)
            throws JSONException {

        // Create an empty ArrayList that we can start adding movies to
        List<Movie> movieList = new ArrayList<>();

        // movie information. Each movie is an element of the "results" array
        final String IMDB_RESULTS = "results";

        // this is the data I need to recover from each element of the results array
        final String IMDB_POSTER = "poster_path";
        final String IMDB_OVERVIEW = "overview";
        final String IMDB_DATE = "release_date";
        final String IMDB_TITLE = "original_title";
        final String IMDB_ID = "id";
        final String IMDB_AVERAGE = "vote_average";
        final String IMDB_AUTHOR = "author";
        final String IMDB_CONTENT = "content";
        final String IMDB_KEY = "key";

        final String IMDB_MESSAGE_CODE = "cod";


        // here we store the inputstream into the JSONObject
        JSONObject movieJson = new JSONObject(IMDBJsonStr);

        // Is there an error?
        if (movieJson.has(IMDB_MESSAGE_CODE)) {
            int errorCode = movieJson.getInt(IMDB_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    //Location invalid
                    return null;
                default:
                    // Server down
                    return null;
            }
        }

        // capture the complete stream as an array of objects in the "results" array
        JSONArray IMDBArray = movieJson.getJSONArray(IMDB_RESULTS);

        if (origin == "mp") {
            Log.i("JSONParse", "origin == \"mp\"" );
            // loop through the array until the iterations match the length
            for (int i = 0; i < IMDBArray.length(); i++) {

            /* These are the values that will be collected */
                String poster_path;
                String overview;
                String release_date;
                String original_title;
                String id;
                String vote_average;


                // Get the JSON object in the first position of the array
                JSONObject movie = IMDBArray.getJSONObject(i);

                // then call get() methods on that array to get the info and store it as description
                poster_path = movie.getString(IMDB_POSTER);
                overview = movie.getString(IMDB_OVERVIEW);
                release_date = movie.getString(IMDB_DATE);
                original_title = movie.getString(IMDB_TITLE);
                id = movie.getString(IMDB_ID);
                vote_average = movie.getString(IMDB_AVERAGE);



                Movie Movie = new Movie(poster_path, overview, release_date, original_title, id, vote_average, null, null, null);

                movieList.add(Movie);
            }
        }

        if (origin == "daReview") {
            Log.i("JSONParse", "origin == \"daReview\"" );
            // loop through the array until the iterations match the length
            for (int i = 0; i < IMDBArray.length(); i++) {

            /* These are the values that will be collected */

                String author;
                String content;

                // Get the JSON object in the first position of the array
                JSONObject movie = IMDBArray.getJSONObject(i);

                author = movie.getString(IMDB_AUTHOR);
                content = movie.getString(IMDB_CONTENT);

                Movie Movie = new Movie(null, null, null, null, null, null, author, content, null);

                movieList.add(Movie);
            }
        }

        if (origin == "daVideo") {
            Log.i("JSONParse", "origin == \"daVideo\"" );
            // loop through the array until the iterations match the length
            for (int i = 0; i < IMDBArray.length(); i++) {

            /* These are the values that will be collected */

                String key;

                // Get the JSON object in the first position of the array
                JSONObject movie = IMDBArray.getJSONObject(i);

                key = movie.getString(IMDB_KEY);

                Movie Movie = new Movie(null, null, null, null, null, null, null, null, key);

                movieList.add(Movie);
            }
        }

        return movieList;
    }

}


