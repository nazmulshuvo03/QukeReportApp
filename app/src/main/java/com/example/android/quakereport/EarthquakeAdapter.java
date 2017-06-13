package com.example.android.quakereport;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by nazmul on 6/9/17.
 */

public class EarthquakeAdapter extends ArrayAdapter<Earthquake>{
    private static final String LOG_TAG = EarthquakeAdapter.class.getSimpleName();

    public EarthquakeAdapter(Activity context, ArrayList<Earthquake> quakeReports){
        super(context, 0, quakeReports);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Earthquake currentQuakeReport = getItem(position);

        // magnitude textview

        TextView magnitudeTextView = (TextView) listItemView.findViewById(R.id.magnitude_text_view);
        DecimalFormat fomattedMagnitude = new DecimalFormat("0.0");
        String magnitudeText = fomattedMagnitude.format(currentQuakeReport.getMagnitude());
        magnitudeTextView.setText(magnitudeText);

        ////set proper magnitude color and circle
        
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeTextView.getBackground();
        int magnitudeColor = getMagnitudeColor(currentQuakeReport.getMagnitude());
        magnitudeCircle.setColor(magnitudeColor);


        // location textview
        String location = new String(currentQuakeReport.getLocation());

        //// offset location textview
        String offsetLocation;
        if (location.contains("of")){
            offsetLocation = location.substring(0, (location.indexOf("of")+2));
        }else {
            offsetLocation = "Near the";
        }
        TextView offsetLocationTextView = (TextView) listItemView.findViewById(R.id.offset_location_text_view);
        offsetLocationTextView.setText(offsetLocation);

        //// primary location textview
        String primaryLocation = "";
        if (location.contains("of")){
            primaryLocation = location.substring((location.indexOf("of")+2), location.length());
        }else {
            primaryLocation = location;
        }
        TextView locationTextView = (TextView) listItemView.findViewById(R.id.primary_location_text_view);
        locationTextView.setText(primaryLocation);


        // time and date textview

        //// date textview
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date_view);
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        String formattedDate = dateFormat.format(currentQuakeReport.getTimeInMilliseconds());
        dateTextView.setText(formattedDate);

        //// time textview
        TextView timeTextView = (TextView) listItemView.findViewById(R.id.time_view);
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        String formattedTime = timeFormat.format(currentQuakeReport.getTimeInMilliseconds());
        timeTextView.setText(formattedTime);

        return listItemView;
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeFloor = (int) Math.floor(magnitude);

        int magnitudeColor1 = ContextCompat.getColor(getContext(), R.color.magnitude1);
        int magnitudeColor2 = ContextCompat.getColor(getContext(), R.color.magnitude2);
        int magnitudeColor3 = ContextCompat.getColor(getContext(), R.color.magnitude3);
        int magnitudeColor4 = ContextCompat.getColor(getContext(), R.color.magnitude4);
        int magnitudeColor5 = ContextCompat.getColor(getContext(), R.color.magnitude5);
        int magnitudeColor6 = ContextCompat.getColor(getContext(), R.color.magnitude6);
        int magnitudeColor7 = ContextCompat.getColor(getContext(), R.color.magnitude7);
        int magnitudeColor8 = ContextCompat.getColor(getContext(), R.color.magnitude8);
        int magnitudeColor9 = ContextCompat.getColor(getContext(), R.color.magnitude9);
        int magnitudeColor10 = ContextCompat.getColor(getContext(), R.color.magnitude10plus);

        switch (magnitudeFloor){
            case 0:
            case 1:
                return magnitudeColor1;
            case 2:
                return magnitudeColor2;
            case 3:
                return magnitudeColor3;
            case 4:
                return magnitudeColor4;
            case 5:
                return magnitudeColor5;
            case 6:
                return magnitudeColor6;
            case 7:
                return magnitudeColor7;
            case 8:
                return magnitudeColor8;
            case 9:
                return magnitudeColor9;
            default:
                return magnitudeColor10;
        }
    }
}
