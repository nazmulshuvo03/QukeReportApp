package com.example.android.quakereport;

/**
 * Created by nazmul on 6/9/17.
 */

public class Earthquake {
    private double magnitude;
    private String location;
    private long timeInMilliseconds;

    public Earthquake (double mMag, String lLoc, long time){
        magnitude = mMag;
        location = lLoc;
        timeInMilliseconds = time;
    }

    public double getMagnitude(){
        return magnitude;
    }
    public String getLocation(){
        return location;
    }
    public long getTimeInMilliseconds(){
        return timeInMilliseconds;
    }
}
