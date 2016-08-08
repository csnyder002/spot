package com.IntelligentWaves.xmltest;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class SMSActivity extends Activity {

    Button smsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        //smsButton = (Button) findViewById(R.id.smsButton);
    }



    public String getSMSBody(){
        return null;
    }

}
