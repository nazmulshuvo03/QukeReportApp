package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nazmul on 9/14/17.
 */

public class EarthquakeLoader extends AsyncTaskLoader <List<Earthquake>> {

    private static final String LOG_TAG = EarthquakeLoader.class.getName();
    private String mUrl;

    public EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        //Log.i(LOG_TAG, "onStartLoading() is called");

        forceLoad();
    }

    @Override
    public List<Earthquake> loadInBackground() {
        //Log.i(LOG_TAG, "loadInBackground() is called");

        if (mUrl == null) {
            return null;
        }
        ArrayList<Earthquake> result = QueryUtils.fetchEarthquakeData(mUrl);
        return result;
    }
}
