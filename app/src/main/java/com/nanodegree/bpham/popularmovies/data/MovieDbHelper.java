package com.nanodegree.bpham.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nanodegree.bpham.popularmovies.Movie;
import com.nanodegree.bpham.popularmovies.data.MovieContract.MovieEntry;
import com.nanodegree.bpham.popularmovies.data.MovieContract.ReviewEntry;
import com.nanodegree.bpham.popularmovies.data.MovieContract.TrailerEntry;

/**
 * Created by binh on 8/17/15.
 *
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + "("+
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COLUMN_TMDB_ID + " INTEGER NOT NULL, "+
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_POSTER + " TEXT NOT NULL," +
                MovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL," +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL," +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSITION + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_FAVORITE + " INTEGER NOT NULL DEFAULT 0, " +
                " UNIQUE (" + MovieEntry.COLUMN_TMDB_ID + ") ON CONFLICT IGNORE);";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + "("+
                TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TrailerEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL," +
                TrailerEntry.COLUMN_TMDB_ID + " TEXT NOT NULL," +
                TrailerEntry.COLUMN_NAME + " TEXT NOT NULL," +
                TrailerEntry.COLUMN_KEY + " TEXT NOT NULL," +
                TrailerEntry.COLUMN_SITE + " TEXT NOT NULL," +
                " FOREIGN KEY (" + TrailerEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_TMDB_ID + ")," +
                " UNIQUE (" + TrailerEntry.COLUMN_TMDB_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + "("+
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReviewEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL," +
                ReviewEntry.COLUMN_TMDB_ID + " TEXT NOT NULL," +
                ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL," +
                ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL," +
                " FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_TMDB_ID + "), " +
                " UNIQUE (" + TrailerEntry.COLUMN_TMDB_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}