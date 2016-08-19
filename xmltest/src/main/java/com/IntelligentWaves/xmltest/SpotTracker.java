package com.IntelligentWaves.xmltest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class SpotTracker extends ActionBarActivity implements View.OnClickListener {

    Boolean running = false; // flag for updating breadcrumb toggle switch
    int interval = 30000;
    int picked = 0; // holds user interval choice
    LocationManager locationManager;
    SharedPreferences manager;
    Button spot_tracker_button;
    Spinner uploadOptionsSpinner;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_tracker);
        uploadOptionsSpinner = (Spinner) findViewById(R.id.uploadOptionsSpinner);
        spot_tracker_button = (Button) findViewById(R.id.spot_tracker_button);
        spot_tracker_button.setOnClickListener(this);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.titleTextColor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.UploadOptions, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        uploadOptionsSpinner.setAdapter(adapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        manager = PreferenceManager.getDefaultSharedPreferences(this);
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

    public void smsUpload(String message) // uploads via sms
    {
        String phoneNumber = "7578690037";

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        System.out.println("Message sent");
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
        String temp = manager.getString("Name","") + ": " + location.getLatitude() + "," + location.getLongitude();
        smsUpload(temp);
    }

}
