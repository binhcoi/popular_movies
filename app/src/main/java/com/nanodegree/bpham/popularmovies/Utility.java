package com.nanodegree.bpham.popularmovies;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.nanodegree.bpham.popularmovies.data.MovieContract;

/**
 * Created by binh on 8/19/15.
 *
 */
public class Utility {
    private static final String LOG_TAG = Utility.class.getSimpleName();

    public static String getPreferenceSortBy(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sorting_key),
                context.getString(R.string.pref_sorting_popularity));
    }

    public static void deleteExtraMovies(Context context) {
        ContentResolver resolver = context.getContentResolver();
        //remove movies that is not needed (position = -1 and not favorite)
        String selection = MovieContract.MovieEntry.COLUMN_FAVORITE + "=0 AND " +
                MovieContract.MovieEntry.COLUMN_POSITION + "=-1";
        String[] projection = {MovieContract.MovieEntry.COLUMN_TMDB_ID};

        Cursor moviesToDelete = resolver.query(MovieContract.MovieEntry.CONTENT_URI,
                projection,
                selection,
                null,
                null);

        resolver.delete(MovieContract.MovieEntry.CONTENT_URI,
                selection,
                null);

        while (moviesToDelete.moveToNext()) {
            resolver.delete(MovieContract.TrailerEntry.CONTENT_URI,
                    MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + "=?",
                    new String[]{moviesToDelete.getString(0)});

            resolver.delete(MovieContract.ReviewEntry.CONTENT_URI,
                    MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + "=?",
                    new String[]{moviesToDelete.getString(0)});

        }

    }
}
