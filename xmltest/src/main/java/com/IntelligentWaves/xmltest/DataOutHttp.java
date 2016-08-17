package com.IntelligentWaves.xmltest;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

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
public class DataOutHttp extends AsyncTask<Void, Void, Void> {

    private Exception exception;
    Context c;
    private String url;
    private ArrayList<NameValuePair> params;

    public DataOutHttp (Context c, String url, ArrayList<NameValuePair> params) {
        this.c = c;
        this.url = url;
        this.params = params;
    }

    protected Void doInBackground(Void... args) {
        try {

            DefaultHttpClient httpclient = new MyHttpClient(c);
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            Intent splashIntent = new Intent(c, SlidingActivity.class);
            c.startActivity(splashIntent);

        } catch (Exception e) {
            System.out.println("!!!" + e.toString() + "!!!");
        }
        return null;
    }

    protected void onPostExecute() {
        Intent splashScreen = new Intent(c, SplashActivity.class); //go back to the menu
        c.startActivity(splashScreen);
    }
}