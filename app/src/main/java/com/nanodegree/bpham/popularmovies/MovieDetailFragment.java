package com.nanodegree.bpham.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Cursor cursor = getActivity().getContentResolver().query(getActivity().getIntent().getData(), null, null, null, null);
        cursor.moveToFirst();
        Movie movie = new Movie(cursor);

        ImageView posterImageView = (ImageView) rootView.findViewById(R.id.imageView_detail_poster);
        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";
        Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                .appendEncodedPath(movie.getPoster())
                .build();
        if (builtUri != null)
            Picasso.with(getActivity()).load(builtUri).into(posterImageView);
        ((TextView) rootView.findViewById(R.id.textView_detail_title)).setText(movie.getTitle());
        ((TextView) rootView.findViewById(R.id.textView_detail_release_date)).setText(movie.getReleaseDate());
        ((TextView) rootView.findViewById(R.id.textView_detail_user_rating)).setText(Double.toString(movie.getVoteAverage()));
        ((TextView) rootView.findViewById(R.id.textView_detail_synopsis)).setText(movie.getSynopsis());


        return rootView;
    }
}
