package com.IntelligentWaves.xmltest;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Cody.Snyder on 9/6/2016.
 */
public class SmsUpload {
    private static String TAG = "SmsUpload.java";
    public static void uploadSms(String phone, String message, String encryptionType, String key, Context c) {

        // Get the default instance of SmsManager
        SmsManager smsManager = SmsManager.getDefault();

        // encrypt message
        String smsBody = encrypt(encryptionType, key, message);

        String SMS_SENT = "SMS_SENT";
        String SMS_DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(c, 0, new Intent(SMS_SENT), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(c, 0, new Intent(SMS_DELIVERED), 0);

        ArrayList<String> smsBodyParts = smsManager.divideMessage(smsBody);
        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();

        for (int i = 0; i < smsBodyParts.size(); i++) {
            sentPendingIntents.add(sentPendingIntent);
            deliveredPendingIntents.add(deliveredPendingIntent);
        }

        smsManager.sendMultipartTextMessage(phone, null, smsBodyParts, sentPendingIntents, deliveredPendingIntents);
    }

    public static void testSms(String phone, String user, String encryptionType, String key, Context c) {

        // Get the default instance of SmsManager
        SmsManager smsManager = SmsManager.getDefault();

        String message = "test|"+user;
        String smsBody = encrypt(encryptionType, key, message);

        PendingIntent sentPendingIntent      = PendingIntent.getBroadcast(c, 0, new Intent("SMS_SENT"), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(c, 0, new Intent("SMS_DELIVERED"), 0);

        ArrayList<String> smsBodyParts = smsManager.divideMessage(smsBody);
        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();

        for (int i = 0; i < smsBodyParts.size(); i++) {
            sentPendingIntents.add(sentPendingIntent);
            deliveredPendingIntents.add(deliveredPendingIntent);
        }

        smsManager.sendMultipartTextMessage(phone, null, smsBodyParts, sentPendingIntents, deliveredPendingIntents);
    }

    public static String encrypt(String encryptionType, String encryptionKey, String message) {
        try {
            switch (encryptionType) {
                case "Blowfish":
                    return BlowfishEncrypt.encryptToString(encryptionKey, message);
                case "AES":
                    return AesEncryption.encryptToString(encryptionKey, message);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return message;
        }
        Log.d(TAG, "Unrecognized encryption type");
        return message;
    }

}
