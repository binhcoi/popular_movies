package com.nanodegree.bpham.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Binh on 7/9/2015.
 *
 */
public class MoviesGridAdapter extends BaseAdapter {
    private ArrayList<Movie> mMoviesList = new ArrayList<>();
    private Context mContext;

    public MoviesGridAdapter(Context context) {
        mContext = context;
    }

    public int getCount() {
        return mMoviesList.size();
    }

    public long getItemId(int position) {
        return position;
    }

    public Object getItem(int position) {
        return mMoviesList.get(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            imageView = (ImageView) inflater.inflate(R.layout.imageview_movie_poster, parent, false);
        } else {
            imageView = (ImageView) convertView;
        }

        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";
        Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                .appendEncodedPath(mMoviesList.get(position).getPoster())
                .build();
        if (builtUri != null)
            Picasso.with(mContext).load(builtUri).into(imageView);
        return imageView;
    }

    public void addMovie(Movie movie) {
        mMoviesList.add(movie);
        notifyDataSetChanged();
    }

    public void clear() {
        mMoviesList.clear();
        notifyDataSetChanged();
    }
}
