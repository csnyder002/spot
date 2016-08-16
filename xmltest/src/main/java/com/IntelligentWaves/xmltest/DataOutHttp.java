package com.IntelligentWaves.xmltest;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by Cody.Snyder on 8/15/2016.
 */
public class DataOutHttp extends AsyncTask<Void, Void, Void> {

    private Exception exception;
    private Context c;
    private String url;
    private ArrayList<NameValuePair> params;

    public DataOutHttp (Context c, String url, ArrayList<NameValuePair> params) {
        this.c = c;
        this.url = url;
        this.params = params;
    }

    protected Void doInBackground(Void... args) {
        try {
            System.out.println("!!! Begin !!!");
            // Load CAs from an InputStream
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new BufferedInputStream(new FileInputStream("storage/emulated/0/alice.crt"));
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            System.out.println("!!! 1 !!!");

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            System.out.println("!!! 2 !!!");

            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            System.out.println("!!! 3 !!!");

            // Tell the URLConnection to use a SocketFactory from our SSLContext
            URL workingURL = new URL(url);
            HttpsURLConnection urlConnection = (HttpsURLConnection)workingURL.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            InputStream in = urlConnection.getInputStream();

            System.out.println("!!! Check the logs !!!");
            //copyInputStreamToOutputStream(in, System.out);




            /*KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream("storage/emulated/0/mcert.p12");
            keyStore.load(fis, "P@$$w0rd".toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
            kmf.init(keyStore, "P@$$w0rd".toCharArray());
            KeyManager[] keyManagers = kmf.getKeyManagers();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, null, null);

            String result = null;
            HttpURLConnection urlConnection = null;

            System.out.println("!!! working !!!");

            try {
                URL requestedUrl = new URL(url);
                urlConnection = (HttpURLConnection) requestedUrl.openConnection();
                if(urlConnection instanceof HttpsURLConnection) {
                    ((HttpsURLConnection)urlConnection)
                            .setSSLSocketFactory(sslContext.getSocketFactory());
                }
                urlConnection.setRequestMethod("POST");
                urlConnection.setConnectTimeout(1500);
                urlConnection.setReadTimeout(1500);
                int lastResponseCode = urlConnection.getResponseCode();
                result = IOUtil.readFully(urlConnection.getInputStream());
                lastContentType = urlConnection.getContentType();
            } catch(Exception ex) {
                result = ex.toString();
            } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
            }*/

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