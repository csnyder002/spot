package com.IntelligentWaves.xmltest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;

/**
 * Created by Cody.Snyder on 8/15/2016.
 */
public class SubmitReportHTTPS extends AsyncTask<Void, Void, Void> {

    private Exception exception;
    Context c;
    private String url;
    private ArrayList<NameValuePair> params;
    SharedPreferences preferences;

    public SubmitReportHTTPS(Context c, ArrayList<NameValuePair> params) {
        this.c = c;
        preferences = PreferenceManager.getDefaultSharedPreferences(c);
        this.url = "https://"+preferences.getString("host", "")+"/submitReport.php";
        this.params = params;
    }

    protected Void doInBackground(Void... args) {
        try {

            DefaultHttpClient httpclient = new MyHttpsClient(c);
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

        } catch (Exception e) {
            System.out.println("!!!!! " + e.toString() + " !!!");
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Intent splashScreen = new Intent(c, SplashActivity.class); //go back to the menu
        c.startActivity(splashScreen);
    }

}