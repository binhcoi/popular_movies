package com.nanodegree.bpham.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.nanodegree.bpham.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Binh on 7/9/2015.
 *
 */
public class MoviesGridAdapter extends CursorAdapter {
    public MoviesGridAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.imageview_movie_poster, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";
        Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                .appendEncodedPath(cursor.getString(MoviesFragment.COL_POSTER))
                .build();
        if (builtUri != null)
            Picasso.with(context).load(builtUri).into((ImageView)view);
    }
}
