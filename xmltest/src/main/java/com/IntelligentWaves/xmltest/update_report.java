package com.IntelligentWaves.xmltest;

import android.os.Bundle;
import android.app.Activity;
import android.widget.Toast;

public class update_report extends Activity {
    SpotReportObject spotReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_report);

        spotReport = (SpotReportObject) getIntent().getSerializableExtra("spotReport");
        Toast.makeText(this, spotReport.getUUID(), Toast.LENGTH_SHORT).show();
    }

}
