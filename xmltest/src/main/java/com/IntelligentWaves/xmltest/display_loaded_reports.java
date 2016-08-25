package com.IntelligentWaves.xmltest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class display_loaded_reports extends ActionBarActivity implements View.OnClickListener {

    Toolbar toolbar;
    ArrayList<SpotReportObject> objectArray;
    ListView listView;
    Button map_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_loaded_reports);

        // setup toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.titleTextColor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        objectArray = (ArrayList<SpotReportObject>) getIntent().getSerializableExtra("objectArray");
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new CustomAdapter(this, R.layout.spot_report_list_item_layout, objectArray));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), update_report.class);
                intent.putExtra("spotReport", objectArray.get(position));
                startActivity(intent);
            }
        });

        map_button = (Button) findViewById(R.id.map_button);
        map_button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.map_button:
                Intent intent = new Intent(this, Map_Activity.class);
                intent.putExtra("objectArray", objectArray);
                startActivity(intent);
                break;
        }
    }
}
