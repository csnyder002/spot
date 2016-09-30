package com.IntelligentWaves.xmltest;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrationActivity extends ActionBarActivity implements View.OnClickListener{
    private static final String TAG = "RegistrationActivity";
    SharedPreferences preferences;
    Toolbar toolbar;
    EditText nameET;
    EditText hostET;
    EditText coordinatePreferenceET;
    EditText phoneET;
    EditText encryptionET;
    EditText encryptionKeyET;

    Button registerB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        initializeViews();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.registerB:
                register();
                break;
            case R.id.coordPreferenceET:
                displayListDialog(v);
                break;
            case R.id.encryptionET:
                displayListDialog(v);
                break;
        }
    }

    private void initializeViews() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.titleTextColor));
        setSupportActionBar(toolbar);

        nameET                  = (EditText) findViewById(R.id.nameET);
        hostET                  = (EditText) findViewById(R.id.hostET);
        coordinatePreferenceET  = (EditText) findViewById(R.id.coordPreferenceET);
        phoneET                 = (EditText) findViewById(R.id.phoneET);
        encryptionET            = (EditText) findViewById(R.id.encryptionET);
        encryptionKeyET         = (EditText) findViewById(R.id.encryptionKeyET);
        registerB               = (Button) findViewById(R.id.registerB);

        encryptionET.setFocusable(false);
        encryptionET.setOnClickListener(this);

        coordinatePreferenceET.setFocusable(false);
        coordinatePreferenceET.setOnClickListener(this);

        registerB.setOnClickListener(this);

    }

    private void register() {

        if (validateForm()) {
            savePreferences();

            if (preferences.getString("setupCompleted","").equals("true")) {
                FetchReports newHelper = new FetchReports(this);
                newHelper.execute();
            }

            else {
                Toast.makeText(this, "Registration failed. Please make sure you entered your configurations correctly.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private boolean validateForm() // returns true if from is filled out correctly, false if not
    {
        if (nameET.getText().toString().equals("")) {
            Toast.makeText(this, "Please input your name.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (hostET.getText().toString().equals("")) {
            Toast.makeText(this, "Please input a host address.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (phoneET.getText().toString().equals("")) {
            Toast.makeText(this, "Please input the Spot Receiver phone number.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (coordinatePreferenceET.getText().toString().equals("")) {
            Toast.makeText(this, "Please input your coordinate preference.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (encryptionET.getText().toString().equals("")) {
            Toast.makeText(this, "Please select encryption preference.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (encryptionKeyET.getText().toString().equals("")) {
            Toast.makeText(this, "Please input encryption key.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void savePreferences() {
        preferences.edit().putString("uploadType", "HTTPS").apply();
        preferences.edit().putString("phoneId", getPhoneNumber()).apply(); // phone for spot receiver companion app
        preferences.edit().putString("name", nameET.getText().toString()).apply();
        preferences.edit().putString("host", hostET.getText().toString()).apply();
        preferences.edit().putString("phone", phoneET.getText().toString()).apply(); // phone for spot receiver companion app
        preferences.edit().putString("coord_pref", coordinatePreferenceET.getText().toString()).apply();
        preferences.edit().putString("encryptType", encryptionET.getText().toString()).apply();
        preferences.edit().putString("encryptKey", encryptionKeyET.getText().toString()).apply();
        preferences.edit().putString("setupCompleted","true").apply();
    }

    public String getPhoneNumber() {
        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        Log.d(TAG, "This phone's number: "+mPhoneNumber);
        return mPhoneNumber;
    }

    public void displayListDialog(View view) // displays a dialog allowing user to input both date and time
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.list_dialog);
        ListView listView = (ListView) dialog.findViewById(R.id.listView);
        String[] vals = null;
        EditText outputView = null;
        switch (view.getId()) {
            case R.id.coordPreferenceET:
                dialog.setTitle("Select your coordinate preference:");
                vals = getResources().getStringArray(R.array.CoordinatePreference);
                outputView = coordinatePreferenceET;
                break;
            case R.id.encryptionET:
                dialog.setTitle("Select your encryption preference:");
                vals = getResources().getStringArray(R.array.Encryption_Type);
                outputView = encryptionET;
                break;
        }
        final EditText outView = outputView;
        ArrayAdapter adapter = new CustomDialogAdapter(this, R.layout.simple_list_item, vals);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view.findViewById(R.id.textview);
                outView.setText(tv.getText());
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
