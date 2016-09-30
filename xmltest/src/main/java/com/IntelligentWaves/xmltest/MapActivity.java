package com.IntelligentWaves.xmltest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapActivity extends ActionBarActivity implements OnMapReadyCallback, View.OnClickListener {
    private static final String TAG = "MapActivity.java";
    GoogleMap gMap;
    Toolbar toolbar;
    SharedPreferences preferences;
    FetchReports fetchReports;
    Button runTest;
    ArrayList<SpotReportObject> objectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent i = getIntent();
        objectList = (ArrayList<SpotReportObject>) i.getSerializableExtra("objectArray");

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.titleTextColor));
        setSupportActionBar(toolbar);

        setUpMap();

    }

    private void setUpMap() {
        MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;

        gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                for (int i=0; i<objectList.size(); i++) {
                    if (objectList.get(i).getUUID().equals(marker.getTitle())) {
                        Intent intent = new Intent(getApplicationContext(), update_report.class);
                        intent.putExtra("spotReport", objectList.get(i));
                        startActivity(intent);
                    }
                }
            }
        });

        SpotReportObject spotReport = null;
        if (objectList != null) {
            for (int i = 0; i < objectList.size(); i++) {
                spotReport = objectList.get(i);
                Log.d(TAG, spotReport.toString());
                Log.d(TAG, "ADDED MARKER AT: " + spotReport.getLat() + ", " + spotReport.getLon() + " !!!");
                gMap.addMarker(new MarkerOptions()
                        .position(new LatLng(spotReport.getLon(), spotReport.getLat()))
                        .title(spotReport.getSynopsis())
                        .snippet(spotReport.getFullReport()));
            }
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(spotReport.getLon(), spotReport.getLat()), 15);
            gMap.animateCamera(update);
        } else {
            Toast.makeText(this, "No Spot reports to display", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.runTest:

                break;

            }
    }

}
