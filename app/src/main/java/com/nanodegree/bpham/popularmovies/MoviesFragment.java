package com.nanodegree.bpham.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.nanodegree.bpham.popularmovies.data.MovieContract;
import com.nanodegree.bpham.popularmovies.sync.MovieSyncAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    @Bind(R.id.gridview_movies_posters)
    GridView mGridView;

    private MoviesGridAdapter mMoviesGridAdapter;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TMDB_ID,
            MovieContract.MovieEntry.COLUMN_POSTER,
    };

    private static final int MOVIE_LOADER = 0;
    private static final String SELECTED_KEY = "selected_position";

    static final int COL_MOVIE_ID = 0;
    static final int COL_TMDB_ID = 1;
    static final int COL_POSTER = 2;

    private int mPosition = GridView.INVALID_POSITION;

    public MoviesFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mMoviesGridAdapter = new MoviesGridAdapter(getActivity(), null, 0);
        ButterKnife.bind(this, rootView);
        mGridView.setAdapter(mMoviesGridAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                ((Callback) getActivity()).onItemSelected(
                        MovieContract.MovieEntry.buildMovieUri(cursor.getLong(COL_TMDB_ID)));
                mPosition = position;
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }

    private void updateMovies() {
        MovieSyncAdapter.syncImmediately(getActivity());
    }

    public void onSortByChanged() {
        updateMovies();
        mPosition = GridView.INVALID_POSITION;
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = MovieContract.MovieEntry.COLUMN_POSITION + " ASC";
        String selection = MovieContract.MovieEntry.COLUMN_POSITION + "!=-1";

        String sortBy = Utility.getPreferenceSortBy(getActivity());
        if (sortBy.equals(getActivity().getString(R.string.pref_sorting_favorite))) {
            selection = MovieContract.MovieEntry.COLUMN_FAVORITE + "!=0";
            sortOrder = null;
        }

        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                selection,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviesGridAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            mGridView.smoothScrollToPosition(mPosition);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesGridAdapter.swapCursor(null);
    }

    public interface Callback {
        void onItemSelected(Uri movieUri);
    }
}
