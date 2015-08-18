package com.nanodegree.bpham.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by binh on 8/17/15.
 *
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.nanodegree.bpham.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";


    public static final class ReviewEntry implements BaseColumns {

//        public static final Uri CONTENT_URI =
//                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
//
//        public static final String CONTENT_TYPE =
//                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
//        public static final String CONTENT_ITEM_TYPE =
//                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String TABLE_NAME = "reviews";

        public static final String COLUMN_MOVIE_KEY = "movie_id";

        public static final String COLUMN_AUTHOR = "author";

        public static final String COLUMN_CONTENT = "content";

//        public static Uri buildLocationUri(long id) {
//            return ContentUris.withAppendedId(CONTENT_URI, id);
//        }
    }

    public static final class TrailerEntry implements BaseColumns {
        public static final String TABLE_NAME = "trailers";

        public static final String COLUMN_MOVIE_KEY = "movie_id";

        public static final String COLUMN_KEY = "key";

        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_SITE = "site";
    }

    public static final class MovieEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_TMDB_ID = "tmdb_id";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_POSTER = "poster";

        public static final String COLUMN_SYNOPSIS = "synopsis";

        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
    }
}
