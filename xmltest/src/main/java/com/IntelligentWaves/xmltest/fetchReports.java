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
import org.apache.http.message.BasicNameValuePair;
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
public class FetchReports extends AsyncTask<Void, Void, ArrayList<SpotReportObject>> {
    private static final String TAG = "FetchReports.java";
    private Exception exception;
    Context c;
    String query;
    String url;
    String host;
    ArrayList<SpotReportObject> objectArray;
    SharedPreferences preferences;

    public FetchReports(Context c) {
        this.c = c;
        preferences = PreferenceManager.getDefaultSharedPreferences(c);
        this.url = "https://" + preferences.getString("host", "") + "/getUserReports.php";
        this.query = "SELECT * FROM reports WHERE phone='" + preferences.getString("phoneId", "") + "';";
    }

    protected ArrayList<SpotReportObject> doInBackground(Void... args) {
        try {
            DefaultHttpClient httpclient = new MyHttpsClient(c);
            HttpPost httppost = new HttpPost(url);
            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>(2);

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 2000);
            HttpConnectionParams.setSoTimeout(httpParameters, 3000);
            httpclient.setParams(httpParameters);

            postParameters.add(new BasicNameValuePair("userUUID", preferences.getString("user","")));
            postParameters.add(new BasicNameValuePair("query", query));
            httppost.setEntity(new UrlEncodedFormEntity(postParameters));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            // get response
            InputStream is = entity.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = br.readLine()) != null) {
                sb.append(line+"\n");
                Log.d(TAG, line);
            }
            is.close();

            // build list of spot report objects
            String[] array = sb.toString().split("\n");
            objectArray = new ArrayList<SpotReportObject>(array.length);
            for (int i=0; i<array.length; i++) {
                // Decrypt string from server and create SpotReportObject with the information
                MCrypt mcrypt = new MCrypt();
                String[] decryptData = new String(mcrypt.decrypt(array[i].toString())).split("\\|");
                SpotReportObject tempObj = new SpotReportObject(
                        decryptData[0],   // uuid
                        decryptData[1],   // phone
                        decryptData[2],   // name
                        decryptData[3],   // coordinates
                        decryptData[4],   // time_of_report
                        decryptData[5],   // time_observed
                        decryptData[6],   // timezone
                        decryptData[7],   // synopsis
                        decryptData[8],   // full_report
                        decryptData[9],   // lat
                        decryptData[10],   // lng
                        decryptData[11],  // image_file_path
                        decryptData[12]); // image_file
                Log.d(TAG, tempObj.toString());
                objectArray.add(i, tempObj);
            }

        } catch (Exception e) {
            Log.e(TAG, e.toString());
            objectArray = new ArrayList<SpotReportObject>(1);
            SpotReportObject tempObj = new SpotReportObject(
                    "",   // uuid
                    "",   // phone
                    "",   // name
                    "",   // coordinates
                    "",   // time_of_report
                    "",   // time_observed
                    "",   // timezone
                    "Unable to connect to database",   // synopsis
                    "",   // full_report
                    "0",   // lat
                    "0",   // lng
                    "",  // image_file_path
                    ""); // image_file
            objectArray.add(0, tempObj);
            return objectArray;
        }
        return objectArray;
    }

    @Override
    protected void onPostExecute(ArrayList<SpotReportObject> spotReportObjects) {
        super.onPostExecute(spotReportObjects);
        Intent intent = new Intent(c, SlidingActivity.class);
        intent.putExtra("objectArray", spotReportObjects);
        c.startActivity(intent);
    }

    public ArrayList<SpotReportObject> getObjectArray() {
        return objectArray;
    }

}