package com.follyapps.moviepop;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

import com.follyapps.moviepop.DetailsContract.*;
import com.follyapps.moviepop.DbHelper.*;

import static android.R.attr.id;
import static android.R.attr.name;
import static com.follyapps.moviepop.DetailsContract.DetailsEntry.COLUMN_ID;
import static com.follyapps.moviepop.DetailsContract.DetailsEntry.TABLE_NAME;
import static com.follyapps.moviepop.R.id.date;
import static com.follyapps.moviepop.R.id.rate;
import static com.follyapps.moviepop.R.id.review;
import static com.follyapps.moviepop.R.id.synopsis;

public class DetailsActivity extends AppCompatActivity {

    private String mTitle;
    private String mDate;
    private String mRate;
    private String mSynopsis;
    private String mPosterName;
    private String mReview;
    private String reviewChoice = "review";
    private String videoKey;
    private SQLiteDatabase mDb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //finding references to all the UI components
        TextView Title = (TextView) findViewById(R.id.title);
        TextView Date = (TextView) findViewById(date);
        TextView Rate = (TextView) findViewById(rate);
        TextView Synopsis = (TextView) findViewById(synopsis);
        ImageView Poster = (ImageView) findViewById(R.id.poster);

        // retrieving the bundle
        Bundle bundle;
        Intent intentThatStartedThisActivity = getIntent();
        bundle = intentThatStartedThisActivity.getExtras();

        // getting the strings back from the bundle
        mPosterName = bundle.getString("BUNDLE_POSTERNAME");
        mTitle = bundle.getString("BUNDLE_TITLE");
        mDate = bundle.getString("BUNDLE_DATE");
        mRate = bundle.getString("BUNDLE_RATE");
        mSynopsis = bundle.getString("BUNDLE_SYNOP");
        mReview = bundle.getString("BUNDLE_ID");

        //updating UI
        Title.setText(mTitle);
        Date.setText(mDate);
        Rate.setText(mRate);
        Synopsis.setText(mSynopsis);

        Picasso.with(this).load("http://image.tmdb.org/t/p/w500/" + mPosterName)
                .resize(800,900)
                .into(Poster);

        Log.i("DetailsActivity", "title = " + mTitle + "BUNDLE_ID = " + mReview);

        //Database stuff
        DbHelper dbHelper = new DbHelper(this);
        Log.i("DetailsActivity", "DbHelper dbHelper = new DbHelper(this);");
        mDb = dbHelper.getWritableDatabase();
        Log.i("DetailsActivity", "mDb = dbHelper.getWritableDatabase();");


        // we need a way to check if the movie is already in the favorites and if so add check

        checkDatabaseForId(mReview);

        String source = "reviewRequest";
        // make a http request for the review

      makeDetailsQuery(reviewChoice, mReview, source);

    }

    private static final String MOVIEPOP_SHARE_HASHTAG = " #moviePop";

    public void checkBoxClicked(View view){
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        if (checkBox.isChecked()){
            addFavorite(mPosterName, mTitle, mDate, mRate, mSynopsis, mReview);
        } else {
            Log.i("DetailsActivity", "checkBoxClicked else");
            removeFavorite(mReview);

        }

    }

    // these last two methods handle the share action in the menu and launch the intent to send text
    private Intent createShareMovieIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mTitle + " - " + mSynopsis + MOVIEPOP_SHARE_HASHTAG)
                .getIntent();
        return shareIntent;
    }

    public void onClickOpenWebpageButton(View v) {
        String video = "video";
        URL mUrl = NetworkUtils.buildUrl(video, mReview);
        new DetailsActivity.queryVideo().execute(mUrl);
    }

    private void openVideo(String url){
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) !=null){
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareMovieIntent());
        return true;
    }

    // this one will be called by the menu item. It creates an async task for the network and starts it
    private void makeDetailsQuery(String choice, String id, String source) {
        Log.i("DetailsActivity", "makeDetailsQuery() called" );
        String mChoice = choice;
        URL mUrl = NetworkUtils.buildUrl(mChoice, id);

        if (source =="reviewRequest") {
            new DetailsActivity.queryReview().execute(mUrl);
        } else {
           // new DetailsActivity.queryVideo().execute(mUrl);
        }
    }

    public class queryReview extends AsyncTask<URL, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("DetailsActivity", "onPreExecute()" );
           // mLoadingIndicator.setVisibility(View.VISIBLE); Copy the methods from moviePop.java
        }

        @Override
        protected List<Movie> doInBackground(URL... params) {
            Log.i("DetailsActivity", "doInBackground()" );
            if (params.length == 0) {
                return null;
            }
            URL searchUrl = params[0];


            try {
                String getSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                String origin = "daReview";
                // this is where I need to call the parsing step to convert the string to an array
                // also I need to write a different NetworkUtils method that takes reviews instead of movies
                List<Movie> simpleJsonReviewData = JSONParseUtility
                        .getSimpleMovieStringsFromJson(DetailsActivity.this, getSearchResults, origin);

                // return the array of reviews
                return simpleJsonReviewData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(List<Movie> SearchResults) {
            Log.i("DetailsActivity", "onPostExecute()" );
            StringBuilder sb = new StringBuilder();

          //  mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (SearchResults != null) {
                Log.i("DetailsActivity", "SearchResults != null" );
                Log.i("DetailsActivity", "SearchResults = " + SearchResults);
                for (int i = 0; i < SearchResults.size(); i++) {

                    Movie r = SearchResults.get(i);
                    String Author = r.getAuthor();
                    String Content = r.getContent();

                    sb.append("\n");
                    sb.append(Author);
                    sb.append("\n\n");
                    sb.append(Content);
                    sb.append("\n\n\n");
                    Log.i("DetailsActivity", "onPostExecute() sb length = " + sb.length());
                }
                updateReview(sb);

            } else {
                Log.i("DetailsActivity", "onPostExecute() else" );
            }
        }
    }

    public class queryVideo extends AsyncTask<URL, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("DetailsActivity", "onPreExecute()" );
            // mLoadingIndicator.setVisibility(View.VISIBLE); Copy the methods from moviePop.java
        }

        @Override
        protected List<Movie> doInBackground(URL... params) {
            Log.i("DetailsActivity", "doInBackground()" );
            if (params.length == 0) {
                return null;
            }
            URL searchUrl = params[0];


            try {
                String getSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                String origin = "daVideo";
                // this is where I need to call the parsing step to convert the string to an array
                // also I need to write a different NetworkUtils method that takes reviews instead of movies
                List<Movie> simpleJsonVideoData = JSONParseUtility
                        .getSimpleMovieStringsFromJson(DetailsActivity.this, getSearchResults, origin);

                // return the array of videos
                return simpleJsonVideoData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        }
        @Override
        protected void onPostExecute(List<Movie> SearchResults) {
            Log.i("DetailsActivity", "onPostExecute()" );

            //  mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (SearchResults != null) {
                Log.i("DetailsActivity", "SearchResults != null" );
                Log.i("DetailsActivity", "SearchResults = " + SearchResults);

                    Movie r = SearchResults.get(0);
                    videoKey = r.getKey();
                String urlAsString = ("https://www.youtube.com/watch?v="+videoKey);
                openVideo(urlAsString);
                Log.i("DetailsActivity", "onPostExecute() url" + "https://www.youtube.com/watch?v="+ videoKey );

            } else {
                Log.i("DetailsActivity", "onPostExecute() else" );
            }
        }
    }

    public void updateReview(StringBuilder stringBuilder){

        if(stringBuilder.length()==0){
            String error = "No Reviews Available";
            TextView ReviewTv = (TextView) findViewById(R.id.reviewText);
            ReviewTv.setText(error);
        }else {

            TextView ReviewTv = (TextView) findViewById(R.id.reviewText);
            ReviewTv.setText(stringBuilder);
            Log.i("DetailsActivity", "updateReview ");
        }
    }

    // down here add the removeFavorites, and addFavorites to the checkbox

    private long addFavorite(String postername,String title,String date,String rate,String synop,String id) {
        Log.i("DetailsActivity", "addFavorite called");
        ContentValues cv = new ContentValues();
        cv.put(DetailsContract.DetailsEntry.COLUMN_POSTERNAME, postername);
        cv.put(DetailsContract.DetailsEntry.COLUMN_TITLE, title);
        cv.put(DetailsContract.DetailsEntry.COLUMN_DATE, date);
        cv.put(DetailsContract.DetailsEntry.COLUMN_RATE, rate);
        cv.put(DetailsContract.DetailsEntry.COLUMN_SYNOP, synop);
        cv.put(DetailsContract.DetailsEntry.COLUMN_ID, id);

        Log.i("DetailsActivity", "addFavorites about to call insert");
        return mDb.insert(TABLE_NAME, null, cv);
    }

    private boolean removeFavorite(String id) {

        Log.i("DetailsActivity", "removeFavorite about to remove database entry");
        String query = "DELETE FROM favoriteDetails WHERE id = " + mReview;
        return mDb.delete(DetailsContract.DetailsEntry.TABLE_NAME, DetailsContract.DetailsEntry.COLUMN_ID + "=" + id, null) > 0;
    }

    private void checkDatabaseForId(String mReview) {
        Log.i("DetailsActivity", "checkDatabaseForId ");

        Cursor cursor;
        cursor = mDb.rawQuery("SELECT * FROM favoriteDetails WHERE movieId = ?", new String[] {String.valueOf(mReview)});

        if (cursor != null) {
            cursor.moveToLast();
            int count = cursor.getCount();
            if (count == 0) {
                Log.i("DetailsActivity", "count==0");
                // The id is not present in the database - do nothing
            } else {
                // The id is present in the database
                Log.i("DetailsActivity", "else ");
                CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
                checkBox.setChecked(true);
            }
        }

       cursor.close();
    }

}
