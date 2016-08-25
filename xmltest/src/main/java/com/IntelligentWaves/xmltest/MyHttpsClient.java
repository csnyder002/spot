package com.IntelligentWaves.xmltest;

import android.content.Context;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import java.io.InputStream;
import java.security.KeyStore;

/**
 * Created by Cody.Snyder on 8/17/2016.
 */
public class MyHttpsClient extends DefaultHttpClient {

    private static Context context;

    public MyHttpsClient(Context context) {
        MyHttpsClient.context = context;
    }

    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", newSslSocketFactory(), 443));
        return new SingleClientConnManager(getParams(), registry);
    }

    private SSLSocketFactory newSslSocketFactory() {
        try {
            KeyStore trusted = KeyStore.getInstance("BKS");
            InputStream in = MyHttpsClient.context.getResources().openRawResource(R.raw.client); //name of your keystore file here
            try {
                trusted.load(in, "P@$$w0rd".toCharArray());
            } finally {
                in.close();
            }
            SSLSocketFactory sf = new SSLSocketFactory(trusted);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return sf;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}