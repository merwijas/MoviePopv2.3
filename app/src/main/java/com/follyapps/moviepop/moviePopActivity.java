package com.follyapps.moviepop;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class moviePopActivity extends AppCompatActivity implements Adapter.AdapterOnClickHandler {


    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;
    private Adapter mAdapter;
    private SQLiteDatabase mDb;


    public static final String COLUMN_POSTERNAME = "postername";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_RATE = "rate";
    public static final String COLUMN_SYNOP = "synop";
    public static final String COLUMN_ID = "id";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_pop);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie);

        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
        else{
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }


        mRecyclerView.setHasFixedSize(true);
        mAdapter = new Adapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // database stuff
        DbHelper dbHelper = new DbHelper(this);
        Log.i("moviePop", "DbHelper dbHelper = new DbHelper(this);");
        mDb = dbHelper.getWritableDatabase();
        Log.i("moviePop", "mDb = dbHelper.getWritableDatabase();");
        makeSearchQuery("popular");
    }

    @Override
    public void onClick(Movie movieChoice) {
        Context context = this;

        //get the parsed data from the movie object and set as strings
        Movie movieIntent = movieChoice;
        String posterName = movieIntent.getPoster();
        String title = movieIntent.getOriginalTitle();
        String date = movieIntent.getReleaseDate();
        String rate = movieIntent.getVoteAverage();
        String synop = movieIntent.getOverview();
        String id = movieIntent.getId();

        // package the strings in a bundle to send it off with the intent
        Bundle bundle = new Bundle();
        bundle.putString("BUNDLE_POSTERNAME", posterName);
        bundle.putString("BUNDLE_TITLE", title);
        bundle.putString("BUNDLE_DATE", date);
        bundle.putString("BUNDLE_RATE", rate);
        bundle.putString("BUNDLE_SYNOP", synop);
        bundle.putString("BUNDLE_ID", id);

        // send with the intent
        Class destinationActivity = DetailsActivity.class;
        Intent startChildActivityIntent = new Intent(context, destinationActivity);
        startChildActivityIntent.putExtras(bundle);

        startActivity(startChildActivityIntent);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_pop) {
            String choice = "popular";
            makeSearchQuery(choice);
            return true;
        }
        if (itemThatWasClickedId == R.id.action_rate) {
            String choice = "ratings";
            makeSearchQuery(choice);
            return true;

        }if (itemThatWasClickedId == R.id.action_favorites) {
            Cursor cursor = favoritesList();
            List<Movie> movieList = new ArrayList<Movie>();
            movieList = extractMovies(cursor);
            mAdapter.setMovieData(movieList);
            cursor.close();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // this one will be called by the menu item. It creates an async task for the network and starts it
    private void makeSearchQuery(String choice) {
        if(choice == "favorites"){

        }else{

            String mChoice = choice;
            URL mUrl = NetworkUtils.buildUrl(mChoice, null);
            new queryTask().execute(mUrl);
        }
    }

    public class queryTask extends AsyncTask<URL, Void, List<Movie>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(URL... params) {

            if (params.length == 0) {
                return null;
            }
            URL searchUrl = params[0];


            try {
                String getSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                String origin = "mp";
                // this is where I need to call the parsing step to convert the string to an array
                List<Movie> simpleJsonMovieData = JSONParseUtility
                        .getSimpleMovieStringsFromJson(moviePopActivity.this, getSearchResults, origin);

                // return the array of movies
                return simpleJsonMovieData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(List<Movie> SearchResults) {

            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (SearchResults != null && !SearchResults.equals("")) {

                showJsonDataView();
                mAdapter.setMovieData(SearchResults);
            } else {

                showErrorMessage();
            }
        }
    }

    // there last two methods handle displaying an error message
    private void showJsonDataView() {
        // First, make sure the error is invisible
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        // Then, make sure the JSON data is visible
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        Log.i("moviePop", "showErrorMessage()" );
        // First, hide the currently visible data
        mRecyclerView.setVisibility(View.INVISIBLE);
        // Then, show the error
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private Cursor favoritesList() {
        return mDb.query(
                DetailsContract.DetailsEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

    }

    private List<Movie> extractMovies(Cursor cursor) {
        Cursor mCursor = cursor;

        List<Movie> movieList = new ArrayList<Movie>();

        for (int i = 0; i < mCursor.getCount(); i++) {

            /* These are the values that will be collected */
            String poster_path;
            String overview;
            String release_date;
            String original_title;
            String id;
            String vote_average;

            mCursor.moveToPosition(i);

            // then call get() methods on that array to get the info and store it as description
            poster_path = mCursor.getString(cursor.getColumnIndex(COLUMN_POSTERNAME));
            overview = mCursor.getString(cursor.getColumnIndex(COLUMN_SYNOP));
            release_date = mCursor.getString(cursor.getColumnIndex(COLUMN_DATE));
            original_title = mCursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
            id = mCursor.getString(cursor.getColumnIndex(COLUMN_ID));
            vote_average = mCursor.getString(cursor.getColumnIndex(COLUMN_RATE));


            Movie Movie = new Movie(poster_path, overview, release_date, original_title, id, vote_average, null, null, null);

            movieList.add(Movie);

        }
        mCursor.close();
        return movieList;

    }}



