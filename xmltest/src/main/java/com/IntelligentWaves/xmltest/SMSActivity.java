package com.IntelligentWaves.xmltest;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;

public class SMSActivity extends Activity {

    Button smsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        smsButton = (Button) findViewById(R.id.smsButton);
    }

    public void sendSMS(View view)
    {
        String phoneNumber = "7578690037";
        String smsBody = "Message from the API";

        // Get the default instance of SmsManager
        SmsManager smsManager = SmsManager.getDefault();
        // Send a text based SMS
        smsManager.sendTextMessage(phoneNumber, null, getSMSBody(), null, null);

        //The following will send binary data without leaving anything in user's sms inbox
        /*
        // Get the default instance of SmsManager
        SmsManager smsManager = SmsManager.getDefault();

        String phoneNumber = "9999999999";
        byte[] smsBody = "Message from the API".getBytes();
        short port = 6734;

        // Send a text based SMS
        smsManager.sendDataMessage(phoneNumber, null, port, smsBody, null, null);
        */

    }

    public String getSMSBody(){
        String smsBody = "This is an SMS!";
        return smsBody;
    }

}
