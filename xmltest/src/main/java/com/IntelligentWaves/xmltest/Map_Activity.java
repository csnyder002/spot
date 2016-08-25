package com.IntelligentWaves.xmltest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Map_Activity extends ActionBarActivity implements OnMapReadyCallback, View.OnClickListener {

    MapFragment map;
    GoogleMap gMap;
    Toolbar toolbar;
    SharedPreferences preferences;
    FetchReports fetchReports;
    Button runTest;
    SpotReportObject[] objectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent i = getIntent();
        String query = i.getStringExtra("query");

        // async task to get appropriate list of reports
        String formattedUrl = "https://" + preferences.getString("Host","") + "/getUserReports.php";
        fetchReports = new FetchReports(this, "SELECT * FROM spot_xml_uploads", formattedUrl);
        fetchReports.execute(); // calls setMapReports()

        runTest = (Button) findViewById(R.id.runTest);
        runTest.setOnClickListener(this);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.titleTextColor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;

        if (objectList != null) {
            for (int i = 0; i < objectList.length; i++) {
                System.out.println("!!! ADDED PIN AT: " + objectList[i].getLat() + "," + objectList[i].getLon() + " !!!");
                LatLng temp = new LatLng(objectList[i].getLat(), objectList[i].getLon());
                gMap.addMarker(new MarkerOptions().position(temp).visible(true));
            }
        } else {
            System.out.println("!!!!! Object list is null");
        }

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.runTest:

                break;

            }
    }

    public void setMapReports(SpotReportObject[] objList) {
        if (!objList.equals(null)) {
            this.objectList = objList;
            System.out.println("!!! set ObjectList !!!");
            map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            map.getMapAsync(this);

        } else {
            System.out.println("!!! null object list !!!");
        }
    }
}
