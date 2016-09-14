package com.IntelligentWaves.xmltest;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Base64;

import java.util.ArrayList;

/**
 * Created by Cody.Snyder on 9/6/2016.
 */
public class SmsUpload {

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


    public static String encrypt(String encryptionType, String encryptionKey, String message) {
        System.out.println("!!! to encrypt:" + message + " !!!");
        try {
            byte[] encrypted;
            switch (encryptionType) {
                case "Blowfish":
                    encrypted = BlowfishEncrypt.encrypt(encryptionKey, message);
                    System.out.println("!!! Blowfish encrypted:" + encrypted + " !!!");
                    return base64Encode(encrypted);

                case "AES":
                    encrypted = AesEncryption.aesEncrypt(encryptionKey, message);
                    System.out.println("!!! Blowfish encrypted:" + encrypted + " !!!");
                    return base64Encode(encrypted);
            }
        } catch (Exception e) {
            System.out.println("!!!!! " + e.toString() + " !!!");
            return message;
        }
        System.out.println("!!!!! unrecognized encryption type !!!");
        return message;
    }

    public static String base64Encode(byte[] data) {
        String answer = Base64.encodeToString(data, Base64.DEFAULT);
        System.out.println("!!! Base64 encoded:" + answer + " !!!");
        return answer;
    }
}
