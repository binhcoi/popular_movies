package com.nanodegree.bpham.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.nanodegree.bpham.popularmovies.sync.MovieSyncAdapter;


public class MainActivity extends AppCompatActivity {

    private String mSortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MovieSyncAdapter.initializeSyncAdapter(this);
        mSortBy = Utility.getPreferenceSortBy(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortBy = Utility.getPreferenceSortBy(this);
        Log.e("OnResume", String.format("%s %s", sortBy, mSortBy));
        Log.e("OnResume", this.toString());
        //if setting changes
        if (sortBy != null && !sortBy.equals(mSortBy)) {
            MoviesFragment moviesFragment = (MoviesFragment) getSupportFragmentManager().
                    findFragmentById(R.id.fragment_movies);
            if (moviesFragment != null) {
                moviesFragment.onSortByChanged();
            }
            mSortBy = sortBy;
        }
    }
}
