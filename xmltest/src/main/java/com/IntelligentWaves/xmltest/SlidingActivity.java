package com.IntelligentWaves.xmltest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class SlidingActivity extends ActionBarActivity implements Toolbar.OnMenuItemClickListener {

    private Toolbar toolbar;
    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout tabs;
    private ArrayList<SpotReportObject> objectArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding);

        setupToolbar();
        setupSlidingTabs();

        objectArray = (ArrayList<SpotReportObject>) getIntent().getSerializableExtra("objectArray");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sliding_tab, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.spotTracks:
                Intent trackerIntent = new Intent(this, SpotTrackerActivity.class);
                startActivity(trackerIntent);
                break;
            case R.id.refresh:
                FetchReports helper = new FetchReports(this);
                helper.execute();
                break;
        }
        return true;
    }

    private void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.titleTextColor));
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);
    }

    private void setupSlidingTabs() {
        CharSequence Titles[]={"Submit Report","Your Reports", "Configure"};
        int Numboftabs = 3;

        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.gold);
            }
        });
        tabs.setViewPager(pager);
    }

    @Override
    public void onBackPressed() {

    }

    public ArrayList<SpotReportObject> getObjectArray(){
        return objectArray;
    }
}
