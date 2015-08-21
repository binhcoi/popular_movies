package com.nanodegree.bpham.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

import com.nanodegree.bpham.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.zip.Inflater;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    @Bind(R.id.textView_detail_title)
    TextView titleView;
    @Bind(R.id.textView_detail_release_date)
    TextView releaseDateView;
    @Bind(R.id.textView_detail_user_rating)
    TextView userRatingView;
    @Bind(R.id.textView_detail_synopsis)
    TextView synopsisView;
    @Bind(R.id.imageView_detail_poster)
    ImageView posterView;
    @Bind(R.id.linearlayout_trailers)
    LinearLayout trailersView;
    @Bind(R.id.linearlayout_reviews)
    LinearLayout reviewsView;

    static final String DETAIL_URI = "URI";

    private static final int DETAILS_LOADER = 0;
    private static final int TRAILERS_LOADER = 1;
    private static final int REVIEWS_LOADER = 2;

    private final String BASE_TRAILER_URL = "http://youtube.com/v";

    private ShareActionProvider mShareActionProvider;
    private Uri mUri;
    private Uri mShareUri;

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAILS_LOADER, null, this);
        getLoaderManager().initLoader(TRAILERS_LOADER, null, this);
        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            mUri = args.getParcelable(DETAIL_URI);
        }
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void onSortByChanged() {
        mUri = null;
        mShareUri = null;
        getLoaderManager().restartLoader(DETAILS_LOADER, null, this);
        getLoaderManager().restartLoader(TRAILERS_LOADER, null, this);
        getLoaderManager().restartLoader(REVIEWS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            Uri uri;
            switch (id) {
                case DETAILS_LOADER:
                    return new CursorLoader(
                            getActivity(),
                            mUri,
                            null,
                            null,
                            null,
                            null
                    );

                case TRAILERS_LOADER:
                    mShareUri = null;
                    uri = MovieContract.TrailerEntry.buildTrailerUri(
                            MovieContract.MovieEntry.getIdFromUri(mUri));
                    return new CursorLoader(
                            getActivity(),
                            uri,
                            null,
                            null,
                            null,
                            null
                    );
                case REVIEWS_LOADER:
                    uri = MovieContract.ReviewEntry.buildReviewUri(
                            MovieContract.MovieEntry.getIdFromUri(mUri));
                    return new CursorLoader(
                            getActivity(),
                            uri,
                            null,
                            null,
                            null,
                            null
                    );
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case DETAILS_LOADER:
                cursor.moveToFirst();
                final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";
                Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                        .appendEncodedPath(cursor.getString(
                                cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER)))
                        .build();
                if (builtUri != null)
                    Picasso.with(getActivity()).load(builtUri).into(posterView);
                titleView.setText(cursor.getString(
                        cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
                releaseDateView.setText(cursor.getString(
                        cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
                userRatingView.setText(cursor.getString(
                        cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
                synopsisView.setText(cursor.getString(
                        cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_SYNOPSIS)));
                break;
            case TRAILERS_LOADER:
                while (cursor.moveToNext()) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_trailer, null);
                    final Uri uri = Uri.parse(BASE_TRAILER_URL).
                            buildUpon().
                            appendPath(cursor.getString(
                                    cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_KEY))).
                            build();
                    if (mShareUri == null)
                        mShareUri = uri;
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        }
                    });
                    TrailerViewHolder holder = new TrailerViewHolder(view);
                    holder.nameView.setText(cursor.getString(
                            cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_NAME)));
                    trailersView.addView(view);
                }
                if (mShareActionProvider != null && mShareUri != null) {
                    mShareActionProvider.setShareIntent(createShareTrailerIntent());
                }
                break;
            case REVIEWS_LOADER:
                while (cursor.moveToNext()) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_review, null);
                    ReviewViewHolder holder = new ReviewViewHolder(view);
                    holder.authorView.setText(cursor.getString(
                            cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR)));
                    holder.contextView.setText(cursor.getString(
                            cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT)));
                    reviewsView.addView(view);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case DETAILS_LOADER:

                break;
            case TRAILERS_LOADER:
                mShareUri = null;
                break;
            case REVIEWS_LOADER:
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate menu resource file.
        inflater.inflate(R.menu.menu_detail_fragment, menu);

        MenuItem item = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mShareUri != null) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
        }
    }


    private Intent createShareTrailerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mShareUri.toString());
        return shareIntent;
    }

    static class ReviewViewHolder {
        @Bind(R.id.textview_review_author)
        TextView authorView;
        @Bind(R.id.textview_review_content)
        TextView contextView;

        public ReviewViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class TrailerViewHolder {
        @Bind(R.id.textview_trailer_name)
        TextView nameView;

        public TrailerViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
