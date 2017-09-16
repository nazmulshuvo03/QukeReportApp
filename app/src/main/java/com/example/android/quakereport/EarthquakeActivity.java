/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.widget.ProgressBar;
import android.widget.TextView;

public class EarthquakeActivity extends AppCompatActivity implements LoaderCallbacks<List<Earthquake>>{

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    private EarthquakeAdapter earthquakeAdapter;
    private TextView mEmptyStateTextView;
    private ProgressBar mProgressbar;

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.i(LOG_TAG, "onCreate() is called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        ListView listView = (ListView) findViewById(R.id.list);

        earthquakeAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());
        listView.setAdapter(earthquakeAdapter);

//        check internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Earthquake currentQuakeReport = earthquakeAdapter.getItem(i);
                Uri earthquakeUri = Uri.parse(currentQuakeReport.getUrl());

                Intent uriIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);
                startActivity(uriIntent);
            }
        });

//        Empty state textview
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        listView.setEmptyView(mEmptyStateTextView);

        //EathquakeAsyncTask task = new EathquakeAsyncTask();
        //task.execute(USGS_REQUEST_URL);

        if (isConnected) {
            LoaderManager loaderManager = getLoaderManager();
            //Log.i(LOG_TAG, "initLoader() is called");
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        } else {
            View mProgressbar = findViewById(R.id.progressBar);
            mProgressbar.setVisibility(View.GONE);

            mEmptyStateTextView.setText(R.string.internetConnection);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
 *
Loader starts here
 *
 **/
    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        //Log.i(LOG_TAG, "onCreateLoader() is called");

//        constructing desired URL from user preferences using sharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPreferences.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));
        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "20");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {
        //Log.i(LOG_TAG, "onLoadFinished() is called");

        mEmptyStateTextView.setText(R.string.emptyStateText);

//      hide progressbar when loading is complete
        mProgressbar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressbar.setVisibility(View.GONE);


        if (earthquakes == null) return;
        earthquakeAdapter.clear();
        if (earthquakes != null && !earthquakes.isEmpty()) {
            earthquakeAdapter.addAll(earthquakes);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        //Log.i(LOG_TAG, "onLoaderReset() is called");

        earthquakeAdapter.clear();
    }

    /*
    public class EathquakeAsyncTask extends AsyncTask<String, Void, ArrayList<Earthquake>> {

        @Override
        protected ArrayList<Earthquake> doInBackground(String... urls) {

            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            ArrayList<Earthquake> result = QueryUtils.fetchEarthquakeData(urls[0]);
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Earthquake> earthquakes) {
            if (earthquakes == null) return;
            earthquakeAdapter.clear();
            if (earthquakes != null && !earthquakes.isEmpty()) {
                earthquakeAdapter.addAll(earthquakes);
            }
        }
    }
    */
}
