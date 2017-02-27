package com.follyapps.moviepop;

import android.provider.BaseColumns;

public class DetailsContract {

    public static final class DetailsEntry implements BaseColumns {
        public static final String TABLE_NAME = "favoriteDetails";
        public static final String COLUMN_POSTERNAME = "postername";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_RATE = "rate";
        public static final String COLUMN_SYNOP = "synop";
        public static final String COLUMN_ID = "movieId";
    }

}

