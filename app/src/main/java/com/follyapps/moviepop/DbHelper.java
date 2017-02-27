package com.follyapps.moviepop;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.follyapps.moviepop.DetailsContract.*;

public class DbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "favorites.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 2;

    // Constructor
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i("DbHelper", "onCreate");
        // Create a table to hold waitlist data
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + DetailsContract.DetailsEntry.TABLE_NAME + " (" +
                DetailsContract.DetailsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DetailsContract.DetailsEntry.COLUMN_POSTERNAME +
                DetailsContract.DetailsEntry.COLUMN_TITLE +
                DetailsContract.DetailsEntry.COLUMN_DATE +
                DetailsContract.DetailsEntry.COLUMN_RATE +
                DetailsContract.DetailsEntry.COLUMN_SYNOP +
                DetailsContract.DetailsEntry.COLUMN_ID +
                "); ";
        Log.i("DbHelper", "post table string");
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
        Log.i("DbHelper", "sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DetailsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

