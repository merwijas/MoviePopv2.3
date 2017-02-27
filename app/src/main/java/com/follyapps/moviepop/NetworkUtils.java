package com.follyapps.moviepop;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;



public class NetworkUtils {

    final static String POPULAR =
            "http://api.themoviedb.org/3/movie/popular?api_key=removed";

    final static String RATINGS =
            "http://api.themoviedb.org/3/movie/top_rated?api_key=removed";

    final static String VIDEO =
            "http://api.themoviedb.org/3/movie/";

    final static String REVIEW =
            "http://api.themoviedb.org/3/movie/";

    final static String REVIEW_KEY =
            "/reviews?api_key=removed";

    final static String VIDEO_KEY =
            "/videos?api_key=removed";

    // this is the url builder, it will take in the type from the menu selection and
    // set the url to that query, then return it
    public static URL buildUrl(String searchType, String id) {
        Log.i("NetworkUtils", "buildUrl()" );
        String mSearchType = searchType;
        URL url = null;
        if (mSearchType == "popular"){
            try {
            url= new URL(POPULAR.toString());
            }catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }if (mSearchType == "ratings"){
            try {
                url= new URL(RATINGS.toString());
            }catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }if (mSearchType == "video"){
            try {
                url= new URL((VIDEO+id+VIDEO_KEY).toString());
            }catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }if (mSearchType == "review"){
            try {
                url= new URL((REVIEW+id+REVIEW_KEY).toString());
            }catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
