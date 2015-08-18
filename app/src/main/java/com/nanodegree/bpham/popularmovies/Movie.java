package com.nanodegree.bpham.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.nanodegree.bpham.popularmovies.tmdbAPI.Discovery;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *  Created by Binh on 7/9/2015.
 */
public class Movie implements Parcelable {

    private final String LOG_TAG = Movie.class.getSimpleName();

    private String mTitle;
    private String mPoster;
    private String mSynopsis;
    private double mVoteAverage;
    private String mReleaseDate;

    public Movie(Discovery.Result result){
        mTitle = result.getTitle();
        mPoster = result.getPosterPath();
        mSynopsis = result.getOverview();
        mVoteAverage = result.getVoteAverage();
        mReleaseDate = result.getReleaseDate();
    }

    public Movie(JSONObject movieJson){
        final String TMDB_TITLE = "original_title";
        final String TMDB_POSTER = "poster_path";
        final String TMDB_SYNOPSIS = "overview";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        final String TMDB_RELEASE_DATE = "release_date";
        try {
            mTitle = movieJson.getString(TMDB_TITLE);
            mPoster = movieJson.getString(TMDB_POSTER);
            mSynopsis = movieJson.getString(TMDB_SYNOPSIS);
            mVoteAverage = movieJson.getDouble(TMDB_VOTE_AVERAGE);
            mReleaseDate = movieJson.getString(TMDB_RELEASE_DATE);
        } catch(JSONException e){
            Log.e(LOG_TAG, "Error:", e);
        }
    }

    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel out, int flags){
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

    private Movie(Parcel in){
        mTitle = in.readString();
        mPoster = in.readString();
        mSynopsis = in.readString();
        mVoteAverage = in.readDouble();
        mReleaseDate = in.readString();
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
