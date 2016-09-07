package com.IntelligentWaves.xmltest;

import android.telephony.SmsManager;

/**
 * Created by Cody.Snyder on 9/6/2016.
 */
public class SmsUpload {

    public static void uploadSms(String phone, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, message, null, null);
    }
}
