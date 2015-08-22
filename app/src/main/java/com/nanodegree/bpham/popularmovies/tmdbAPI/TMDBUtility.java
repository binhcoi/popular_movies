package com.nanodegree.bpham.popularmovies.tmdbAPI;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.nanodegree.bpham.popularmovies.Utility;
import com.nanodegree.bpham.popularmovies.data.MovieContract;

import java.util.Vector;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by binh on 8/21/15.
 * .
 */
public class TMDBUtility {
    private final String LOG_TAG = TMDBUtility.class.getSimpleName();

    private final String BASE_URL = "http://api.themoviedb.org/3";
    private final String API_KEY = "";

    private TMDBService service;
    private Context mContext;

    public TMDBUtility(Context context) {
        mContext = context;
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASE_URL).build();
        service = restAdapter.create(TMDBService.class);
    }

    public void fetchMovies(String sortBy) {
        if (sortBy == null || sortBy.isEmpty())
            return;
        service.discoverMovies(API_KEY, sortBy, new Callback<Discovery>() {
            @Override
            public void success(Discovery discovery, Response response) {
                insertMovieFromDiscovery(discovery);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(LOG_TAG, "MOVIE " + error.getMessage());
            }
        });
    }

    public void fetchTrailers(int id) {
        final int movieId = id;
        service.getTrailers(movieId, API_KEY, new Callback<Trailers>() {
            @Override
            public void success(Trailers trailers, Response response) {
                insertTrailers(movieId, trailers);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(LOG_TAG, "TRAILER " + error.getMessage());
            }
        });
    }


    public void fetchReviews(int id) {
        final int movieId = id;
        service.getReviews(movieId, API_KEY, new Callback<Reviews>() {
            @Override
            public void success(Reviews reviews, Response response) {
                insertReviews(movieId, reviews);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(LOG_TAG, "REVIEW " + error.getMessage());
            }
        });
    }


    private void insertMovieFromDiscovery(Discovery discovery) {
        int[] idList = new int[discovery.getResults().size()];
        for (int i = 0; i < discovery.getResults().size(); i++) {
            Discovery.Result result = discovery.getResults().get(i);
            if (result.getPosterPath() == null || result.getTitle() == null)
                continue;
            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieEntry.COLUMN_TMDB_ID, result.getId());
            values.put(MovieContract.MovieEntry.COLUMN_TITLE, result.getTitle());
            values.put(MovieContract.MovieEntry.COLUMN_POSTER, result.getPosterPath());
            values.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, result.getOverview());
            values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, result.getVoteAverage());
            values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, result.getReleaseDate());
            values.put(MovieContract.MovieEntry.COLUMN_POSITION, i);
            Uri uri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,
                    values);
            int id = MovieContract.MovieEntry.getIdFromUri(uri);
            idList[i] = id;
        }

        new FetchExtraDataTask().execute(idList);
    }

    private void insertTrailers(int movieId, Trailers trailers) {
        Vector<ContentValues> valuesVector = new Vector<>(trailers.getResults().size());
        for (int i = 0; i < trailers.getResults().size(); i++) {
            Trailers.Result result = trailers.getResults().get(i);
            if (result.getId() == null || result.getKey() == null ||
                    result.getName() == null || result.getSite() == null)
                continue;
            ContentValues values = new ContentValues();
            values.put(MovieContract.TrailerEntry.COLUMN_TMDB_ID, result.getId());
            values.put(MovieContract.TrailerEntry.COLUMN_MOVIE_KEY, movieId);
            values.put(MovieContract.TrailerEntry.COLUMN_KEY, result.getKey());
            values.put(MovieContract.TrailerEntry.COLUMN_NAME, result.getName());
            values.put(MovieContract.TrailerEntry.COLUMN_SITE, result.getSite());

            valuesVector.add(values);
        }
        ContentValues[] values = new ContentValues[valuesVector.size()];
        valuesVector.toArray(values);
        mContext.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI,
                values);
    }

    private void insertReviews(int movieId, Reviews reviews) {
        Vector<ContentValues> valuesVector = new Vector<>(reviews.getResults().size());
        for (int i = 0; i < reviews.getResults().size(); i++) {
            Reviews.Result result = reviews.getResults().get(i);
            if (result.getId() == null || result.getAuthor() == null ||
                    result.getContent() == null)
                continue;
            ContentValues values = new ContentValues();
            values.put(MovieContract.ReviewEntry.COLUMN_TMDB_ID, result.getId());
            values.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY, movieId);
            values.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, result.getAuthor());
            values.put(MovieContract.ReviewEntry.COLUMN_CONTENT, result.getContent());
            valuesVector.add(values);
        }
        ContentValues[] values = new ContentValues[valuesVector.size()];
        valuesVector.toArray(values);
        mContext.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI,
                values);
    }

    private class FetchExtraDataTask extends AsyncTask<int[], Void, Void> {

        @Override
        protected Void doInBackground(int[]... params) {
            int[] idList = params[0];
            for (int id : idList) {
                fetchReviews(id);
                fetchTrailers(id);
                // TMDB API have a limit of 40 requests/10 sec
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Utility.deleteExtraMovies(mContext);
        }
    }
}
