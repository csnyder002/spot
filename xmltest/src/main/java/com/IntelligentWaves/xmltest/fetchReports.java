package com.IntelligentWaves.xmltest;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Cody.Snyder on 8/15/2016.
 */
public class FetchReports extends AsyncTask<Void, Void, SpotReportObject[]> {

    private Exception exception;
    Context c;
    String query;
    String url;
    SpotReportObject[] objectArray;

    public FetchReports(Context c, String query, String url) {
        this.c = c;
        this.query = query;
        this.url = url;
    }

    protected SpotReportObject[] doInBackground(Void... args) {
        try {
            DefaultHttpClient httpclient = new MyHttpsClient(c);
            HttpPost httppost = new HttpPost(url);
            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>(1);
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
            }
            is.close();

            // build list of spot report objects
            String[] array = sb.toString().split("\n");
            objectArray = new SpotReportObject[array.length];
            for (int i=0; i<array.length; i++) {
                String[] temp = array[i].toString().split("\\|");
                //SpotReportObject tempObj = new SpotReportObject(uuid,  name,   date,   time,     dateTaken, timeTaken, coordinates, extrainfo, imagefilepath, uploadts, geometry,   lat,      lon,      type);
                SpotReportObject tempObj = new SpotReportObject(temp[0], temp[1], temp[2], temp[3], temp[4],    temp[5],  temp[6],     temp[7],    temp[8],      temp[9],  temp[10], temp[11], temp[12], temp[13]);
                objectArray[i]=tempObj;
            }

        } catch (Exception e) {
            System.out.println("!!! " + e.toString() + " !!!");
        }
        return objectArray;
    }

    @Override
    protected void onPostExecute(SpotReportObject[] spotReportObjects) {
        System.out.println("!!!!!! "+spotReportObjects[0].getUUID());
        super.onPostExecute(spotReportObjects);
        ((Map_Activity)c).setMapReports(spotReportObjects);
    }

    public SpotReportObject[] getObjectArray() {
        return objectArray;
    }

}