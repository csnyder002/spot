package com.IntelligentWaves.xmltest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.util.ArrayList;

/**
 * Created by Cody.Snyder on 8/31/2016.
 */
public class RegisterHTTPS extends AsyncTask<Void, Void, Void> {

    private Exception exception;
    Context c;
    private String url;
    private String host;
    private ArrayList<NameValuePair> params;
    private String coordPref;
    private String phone;
    SharedPreferences preferences;
    String encryptType;
    String encryptKey;

    public RegisterHTTPS(Context c, String host, String encryptType, String encryptKey, ArrayList<NameValuePair> params) {
        preferences = PreferenceManager.getDefaultSharedPreferences(c);
        this.c              = c;
        this.host           = host;
        this.encryptType    = encryptType;
        this.encryptKey     = encryptKey;
        this.url            = "https://" + host + "/registerUser.php";
        this.params         = params;

    }

    protected Void doInBackground(Void... args) {
        try {

            DefaultHttpClient httpclient = new MyHttpsClient(c);
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(params));

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 2000);
            HttpConnectionParams.setSoTimeout(httpParameters, 3000);
            httpclient.setParams(httpParameters);

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();


        } catch (Exception e) {
            System.out.println("!!! " + e.toString() + " !!!");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        preferences.edit().putString("user",params.get(0).getValue()).apply();
        preferences.edit().putString("name",params.get(1).getValue()).apply();
        preferences.edit().putString("password",params.get(2).getValue()).apply();
        preferences.edit().putString("host",params.get(3).getValue()).apply();
        preferences.edit().putString("phone",params.get(4).getValue()).apply();
        preferences.edit().putString("coord_pref",params.get(5).getValue()).apply();
        preferences.edit().putString("encryptType",encryptType).apply();
        preferences.edit().putString("encryptKey",encryptKey).apply();
        Toast.makeText(c, "You have been successfully registered.", Toast.LENGTH_SHORT).show();
        preferences.edit().putString("setupCompleted","true").apply();

        FetchReports helper = new FetchReports(c);
        helper.execute();
    }
}