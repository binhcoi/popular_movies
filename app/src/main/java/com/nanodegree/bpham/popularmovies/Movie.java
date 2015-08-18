package com.nanodegree.bpham.popularmovies;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.nanodegree.bpham.popularmovies.data.MovieContract;
import com.nanodegree.bpham.popularmovies.tmdbAPI.Discovery;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Binh on 7/9/2015.
 *
 */
public class Movie implements Parcelable {

    private final String LOG_TAG = Movie.class.getSimpleName();

    private long mId;
    private String mTitle;
    private String mPoster;
    private String mSynopsis;
    private double mVoteAverage;
    private String mReleaseDate;

    public Movie(Discovery.Result result) {
        mId = result.getId();
        mTitle = result.getTitle();
        mPoster = result.getPosterPath();
        mSynopsis = result.getOverview();
        mVoteAverage = result.getVoteAverage();
        mReleaseDate = result.getReleaseDate();
    }

    public Movie(Cursor cursor){
        mId = cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TMDB_ID));
        mTitle = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
        mPoster = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER));
        mSynopsis = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_SYNOPSIS));
        mVoteAverage = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
        mReleaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(mId);
        out.writeString(mTitle);
        out.writeString(mPoster);
        out.writeString(mSynopsis);
        out.writeDouble(mVoteAverage);
        out.writeString(mReleaseDate);
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mPoster = in.readString();
        mSynopsis = in.readString();
        mVoteAverage = in.readDouble();
        mReleaseDate = in.readString();
    }

    public long getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPoster() {
        return mPoster;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }
}
