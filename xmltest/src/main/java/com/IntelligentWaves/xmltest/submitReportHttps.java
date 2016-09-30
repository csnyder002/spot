package com.IntelligentWaves.xmltest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Cody.Snyder on 8/15/2016.
 */
public class SubmitReportHTTPS extends AsyncTask<Void, Void, Void> {
    private final static String TAG = "SubmitReportHTTPS.java";
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
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 2000);
            HttpConnectionParams.setSoTimeout(httpParameters, 3000);
            httpclient.setParams(httpParameters);
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(params));

            // get response
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
            is.close();

            String result = sb.toString();
            Log.d(TAG, result);

            Intent splashScreen = new Intent(c, SplashActivity.class); //go back to the menu
            c.startActivity(splashScreen);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

}