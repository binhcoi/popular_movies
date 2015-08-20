package com.nanodegree.bpham.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.nanodegree.bpham.popularmovies.data.MovieContract;
import com.nanodegree.bpham.popularmovies.tmdbAPI.Discovery;
import com.nanodegree.bpham.popularmovies.tmdbAPI.TMDBService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit.RestAdapter;


/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment {

    private MoviesGridAdapter mMoviesGridAdapter;

    public MoviesFragment() {
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
        mMoviesGridAdapter = new MoviesGridAdapter(getActivity(), null, 0);
        final GridView moviesPosterGridView = (GridView) rootView.findViewById(R.id.gridview_movies_posters);
        moviesPosterGridView.setAdapter(mMoviesGridAdapter);
        moviesPosterGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                Intent detailIntent = new Intent(getActivity(), MovieDetailActivity.class);
                detailIntent.setData(MovieContract.MovieEntry.buildMovieUri(cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TMDB_ID))));
                startActivity(detailIntent);
            }
        });
        return rootView;
    }

    private void updateMovies() {
        FetchPopularMovieTask movieTask = new FetchPopularMovieTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        movieTask.execute(prefs.getString(getString(R.string.pref_sorting_key), getString(R.string.pref_sorting_popularity)));
    }

    public class FetchPopularMovieTask extends AsyncTask<String, Void, Movie[]> {
        private final String LOG_TAG = FetchPopularMovieTask.class.getSimpleName();

        @Override
        protected void onPostExecute(Movie[] movies) {
//            if (movies != null) {
//                mMoviesGridAdapter.clear();
//                for (Movie movie : movies) {
//                    mMoviesGridAdapter.addMovie(movie);
//                }
//            }
            mMoviesGridAdapter.swapCursor(getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null, null));
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            final String BASE_URL = "http://api.themoviedb.org/3";
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(BASE_URL)
                    .build();
            TMDBService service = restAdapter.create(TMDBService.class);
            String apiKey = "";
            String sortBy = "";
            if (params[0].equals(getString(R.string.pref_sorting_popularity))) {
                sortBy = "popularity.desc";
            } else if (params[0].equals(getString(R.string.pref_sorting_rating))) {
                sortBy = "vote_average.desc";
            }

            Discovery discovery = service.discoverMovies(apiKey, sortBy);
            return getPopularMovieFromDiscovery(discovery);

//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//
//            String movieJsonString = null;
//            String apiKey = "cba9c860b69488321984a8c81ce0e1f3";
//            String sortBy = "";
//            if (params[0].equals(getString(R.string.pref_sorting_popularity))){
//                sortBy = "popularity.desc";
//            }else if (params[0].equals(getString(R.string.pref_sorting_rating))){
//                sortBy = "vote_average.desc";
//            }
//
//            try {
//                final String POPULAR_MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
//                final String SORT_BY_PARAM = "sort_by";
//                final String API_KEY_PARAM = "api_key";
//
//                Uri builtUri = Uri.parse(POPULAR_MOVIES_BASE_URL).buildUpon()
//                        .appendQueryParameter(SORT_BY_PARAM, sortBy)
//                        .appendQueryParameter(API_KEY_PARAM, apiKey)
//                        .build();
//
//                URL url = new URL(builtUri.toString());
//
//                // Create the request, and open the connection
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                // Read the input stream into a String
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuilder buffer = new StringBuilder();
//                if (inputStream == null) {
//                    return null;
//                }
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line).append("\n");
//                }
//
//                if (buffer.length() == 0) {
//                    return null;
//                }
//                movieJsonString = buffer.toString();
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "Error:", e);
//                return null;
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e(LOG_TAG, "Error closing stream:`", e);
//                    }
//                }
//            }
//
//            try {
//                return getPopularMovieFromJson(movieJsonString);
//            } catch (JSONException e) {
//                Log.e(LOG_TAG, "Error ", e);
//            }
//            return null;
        }

        private Movie[] getPopularMovieFromDiscovery(Discovery discovery) {
            Movie[] moviesList = new Movie[discovery.getResults().size()];
            for (int i = 0; i < discovery.getResults().size(); i++) {
                Movie movie = new Movie(discovery.getResults().get(i));
                ContentValues values = new ContentValues();
                values.put(MovieContract.MovieEntry.COLUMN_TMDB_ID, movie.getId());
                values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
                values.put(MovieContract.MovieEntry.COLUMN_POSTER, movie.getPoster());
                values.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, movie.getSynopsis());
                values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
                values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
                moviesList[i] = new Movie(discovery.getResults().get(i));
            }
            return moviesList;
        }
    }
}
