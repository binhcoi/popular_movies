package com.nanodegree.bpham.popularmovies;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        MoviesGridAdapter moviesGridAdapter = new MoviesGridAdapter(getActivity());
        moviesGridAdapter.addMovie(new Movie());
        moviesGridAdapter.addMovie(new Movie());
        moviesGridAdapter.addMovie(new Movie());
        moviesGridAdapter.addMovie(new Movie());
        moviesGridAdapter.addMovie(new Movie());
        moviesGridAdapter.addMovie(new Movie());
        moviesGridAdapter.addMovie(new Movie());
        moviesGridAdapter.addMovie(new Movie());
        moviesGridAdapter.addMovie(new Movie());
        moviesGridAdapter.addMovie(new Movie());
        final GridView moviesPosterGridView = (GridView)rootView.findViewById(R.id.gridview_movies_posters);
        moviesPosterGridView.setAdapter(moviesGridAdapter);
        return rootView;
    }
}
