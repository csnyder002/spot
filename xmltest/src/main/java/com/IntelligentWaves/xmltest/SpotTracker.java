package com.IntelligentWaves.xmltest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;

public class SpotTracker extends ActionBarActivity implements View.OnClickListener {

    Boolean running = false; // flag for updating breadcrumb toggle switch
    int interval = 30000;
    int picked = 0; // holds user interval choice
    LocationManager locationManager;
    SharedPreferences preferences;
    Button spot_tracker_button;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_tracker);
        spot_tracker_button = (Button) findViewById(R.id.spot_tracker_button);
        spot_tracker_button.setOnClickListener(this);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.titleTextColor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.spot_tracker_button:
                toggleSpotTracker(v);
        }
    }

    public void onRadioButtonClicked(View v)
    {
        switch(v.getId()) {
            case R.id.choice1:
                interval = 30000;
                break;
            case R.id.choice2:
                interval = 60000;
                break;
            case R.id.choice3:
                interval = 300000;
                break;
            case R.id.choice4:
                interval = 900000;
                break;
            case R.id.choice5:
                interval = 1800000;
                break;
            case R.id.choice6:
                interval = 3600000;
                break;
        }
    }

    public void toggleSpotTracker(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (running) // if Spot Tracker is running
        {
            builder.setTitle("Disable Spot Tracker?");
            builder.setMessage("Would you like to disable Spot Tracker?");
            builder.setPositiveButton("Disable Spot Tracker", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    running = false;
                    locationManager.removeUpdates(locationListener);
                    spot_tracker_button.setText("Enable Spot Tracker");
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        }
        else // if spot tracker isn't running
        {
            builder.setTitle("Enable Spot Tracker?");
            builder.setMessage("Are you sure you want to enable Spot Tracker?");
            builder.setPositiveButton("Enable Spot Tracker", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    SpotTracker.this.running = true;
                    getLocation(interval);
                    spot_tracker_button.setText("Disable Spot Tracker");
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        }
        AlertDialog alert = builder.create();
        alert.show();
    }



    public void getLocation(int interval) // get's user's gps coordinates
    {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, interval, 0, locationListener); // will call startRunner(locaion)
        Toast.makeText(this, "Spot Tracker will begin once GPS location is acquired.", Toast.LENGTH_SHORT).show();
    }

    private final LocationListener locationListener = new LocationListener() //waits to hear from the gps
    {
        public void onLocationChanged(Location location)
        {
            updateWithNewLocation(location);
        }
        public void onProviderDisabled(String provider){}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    private void updateWithNewLocation(Location location) //takes a location and breaks it into long lat to fill in forms
    {
        String message = buildString(location.getLatitude(),location.getLatitude());
        SmsUpload.uploadSms(preferences.getString("phone", ""), message, preferences.getString("encryptType",""), preferences.getString("encryptKey",""), this);
    }

    public String buildString(double lat, double lng) {
        String output = preferences.getString("user","")+"|"+lat+"|"+lng+"|"+getTimeStamp();
        System.out.println("!!! "+output+" !!!");
        return output;
    }

    public String getTimeStamp() {
        Calendar c = Calendar.getInstance();

        String year = padNumber(c.get(Calendar.YEAR));
        String month = padNumber(c.get(Calendar.MONTH));
        String day = padNumber(c.get(Calendar.DAY_OF_MONTH));

        String hour = padNumber(c.get(Calendar.HOUR));
        String minute = padNumber(c.get(Calendar.MINUTE));
        String second = padNumber(c.get(Calendar.SECOND));

        String timeStamp = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
        return timeStamp;
    }

    private String padNumber(int number) {
        if (number < 10) {
            return "0" + number;
        } else {
            return number + "";
        }
    }

}
