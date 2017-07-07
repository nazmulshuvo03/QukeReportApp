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

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";

    private EarthquakeAdapter earthquakeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        ListView listView = (ListView) findViewById(R.id.list);

        earthquakeAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());
        listView.setAdapter(earthquakeAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Earthquake currentQuakeReport = earthquakeAdapter.getItem(i);
                Uri earthquakeUri = Uri.parse(currentQuakeReport.getUrl());

                Intent uriIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);
                startActivity(uriIntent);
            }
        });

        EathquakeAsyncTask task = new EathquakeAsyncTask();
        task.execute(USGS_REQUEST_URL);


    }

    public class EathquakeAsyncTask extends AsyncTask<String, Void, ArrayList<Earthquake>> {

        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            if (url == null) {
                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder output = new StringBuilder();
                    String line = bufferedReader.readLine();
                    while (line != null) {
                        output.append(line);
                        line = bufferedReader.readLine();
                    }
                    jsonResponse = output.toString();
                } else {
                    Log.e(LOG_TAG, "ERROR RESPONSE CODE : " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "PROBLEM GETTING THE DATA", e);
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
                if (inputStream != null) inputStream.close();
            }

            return jsonResponse;
        }

        private ArrayList<Earthquake> extractFeaturesFromJson(String earthquakeJson) {
            if (earthquakeJson.isEmpty()) {
                return null;
            }

            ArrayList<Earthquake> earthquakes = new ArrayList<>();

            try {
                JSONObject baseJsonResponse = new JSONObject(earthquakeJson);
                JSONArray earthquakeArray = baseJsonResponse.getJSONArray("features");

                for (int i = 0; i < earthquakeArray.length(); i++) {
                    JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);
                    JSONObject properties = currentEarthquake.getJSONObject("properties");

                    double magnitude = properties.getDouble("mag");
                    String location = properties.getString("place");
                    long time = properties.getLong("time");
                    String url = properties.getString("url");

                    Earthquake earthquake = new Earthquake(magnitude, location, time, url);
                    earthquakes.add(earthquake);
                }

            } catch (JSONException e) {
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
            }
            return earthquakes;
        }

        @Override
        protected ArrayList<Earthquake> doInBackground(String... urls) {

            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            URL url = createUrl(urls[0]);
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "PROBLEM GETTING DATA", e);
            }
            ArrayList<Earthquake> earthquakes = extractFeaturesFromJson(jsonResponse);
            return earthquakes;
        }


        @Override
        protected void onPostExecute(ArrayList<Earthquake> earthquakes) {
            if (earthquakes == null) return;

            //super.onPostExecute(earthquake);


            // set every item in the list to take to the url

//            ArrayList<Earthquake> earthquakes = null;
//            try {
//                earthquakes = extractEarthquakes();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            earthquakeAdapter.clear();
            if (earthquakes != null && !earthquakes.isEmpty()) {
                earthquakeAdapter.addAll(earthquakes);
            }
        }
    }
}
