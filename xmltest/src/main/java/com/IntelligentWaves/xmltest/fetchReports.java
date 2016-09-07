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
        this.query = "SELECT * FROM reports WHERE user_uuid='" + preferences.getString("user", "") + "';";
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
                System.out.println("!! " + line + " !!");
            }
            is.close();

            // build list of spot report objects
            String[] array = sb.toString().split("\n");
            objectArray = new ArrayList<SpotReportObject>(array.length);
            for (int i=0; i<array.length; i++) {
                // Decrypt string from server and create SpotReportObject with the information
                MCrypt mcrypt = new MCrypt();
                String[] decrypted = new String(mcrypt.decrypt(array[i].toString())).split("\\|");
                System.out.println("!! " + decrypted[0] + " !!");

                SpotReportObject tempObj = new SpotReportObject(
                        decrypted[0],   // uuid
                        decrypted[1],   // user_uuid
                        decrypted[2],   // coordinates
                        decrypted[3],   // time_of_report
                        decrypted[4],   // time_observed
                        decrypted[5],   // timezone
                        decrypted[6],   // synopsis
                        decrypted[7],   // full_report
                        decrypted[8],   // lat
                        decrypted[9],   // lng
                        decrypted[10],  // image_file_path
                        decrypted[11]); // image_file

                objectArray.add(i, tempObj);
            }

        } catch (Exception e) {
            System.out.println("! " + e.toString() + " !!!");
            objectArray = new ArrayList<SpotReportObject>(1);
            SpotReportObject tempObj = new SpotReportObject(
                    "",   // uuid
                    "",   // user_uuid
                    "",   // coordinates
                    "",   // time_of_report
                    "",   // time_observed
                    "",   // timezone
                    "",   // synopsis
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