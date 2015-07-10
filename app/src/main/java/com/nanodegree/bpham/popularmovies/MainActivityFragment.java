package com.nanodegree.bpham.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MoviesGridAdapter mMoviesGridAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mMoviesGridAdapter = new MoviesGridAdapter(getActivity());
        final GridView moviesPosterGridView = (GridView) rootView.findViewById(R.id.gridview_movies_posters);
        moviesPosterGridView.setAdapter(mMoviesGridAdapter);
        moviesPosterGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) mMoviesGridAdapter.getItem(position);
                Intent detailIntent = new Intent(getActivity(), MovieDetailActivity.class);
                detailIntent.putExtra("MOVIE", movie);
                startActivity(detailIntent);
            }
        });
        return rootView;
    }

    private void updateMovies() {
        FetchPopularMovieTask movieTask = new FetchPopularMovieTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        movieTask.execute(prefs.getString(getString(R.string.pref_sorting_key),getString(R.string.pref_sorting_popularity)));
    }

    public class FetchPopularMovieTask extends AsyncTask<String, Void, Movie[]> {
        private final String LOG_TAG = FetchPopularMovieTask.class.getSimpleName();

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null) {
                mMoviesGridAdapter.clear();
                for (Movie movie : movies) {
                    mMoviesGridAdapter.addMovie(movie);
                }
            }
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonString = null;
            String apiKey = "cba9c860b69488321984a8c81ce0e1f3";
            String sortBy = "";
            if (params[0].equals(getString(R.string.pref_sorting_popularity))){
                sortBy = "popularity.desc";
            }else if (params[0].equals(getString(R.string.pref_sorting_rating))){
                sortBy = "vote_average.desc";
            }

            try {
                final String POPULAR_MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(POPULAR_MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY_PARAM, sortBy)
                        .appendQueryParameter(API_KEY_PARAM, apiKey)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                movieJsonString = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error:", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream:`", e);
                    }
                }
            }

            try {
                return getPopularMovieFromJson(movieJsonString);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error ", e);
            }
            return null;
        }

        private Movie[] getPopularMovieFromJson(String popularMovieJsonString)
                throws JSONException {
            final String TMDB_RESULTS = "results";
            JSONObject popularMovieJson = new JSONObject(popularMovieJsonString);
            JSONArray moviesArray = popularMovieJson.getJSONArray(TMDB_RESULTS);
            Movie[] moviesList = new Movie[moviesArray.length()];
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieObject = moviesArray.getJSONObject(i);
                moviesList[i] = new Movie(movieObject);
            }
            return moviesList;
        }
    }
}
