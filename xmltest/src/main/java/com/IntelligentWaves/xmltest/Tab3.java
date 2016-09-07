package com.IntelligentWaves.xmltest;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Cody.Snyder on 8/12/2016.
 */
public class Tab3 extends Fragment implements View.OnClickListener{
    Button saveConfig;
    TextView user;
    EditText host;
    EditText coordPrefET;
    EditText uploadET;
    EditText phoneET;
    SharedPreferences manager;

    Boolean running = false; // flag for updating breadcrumb toggle switch
    int picked = 0; // holds user interval choice
    LocationManager locationManager;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle extras = getActivity().getIntent().getExtras();
        manager = PreferenceManager.getDefaultSharedPreferences(getActivity());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v =inflater.inflate(R.layout.tab3_frag,container,false);
        initializeViews(v);
        autofill();
        return v;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.saveConfig:
                Save(view);
                break;
            case R.id.UploadET:
                displayListDialog(view);
                break;
            case R.id.coordPrefET:
                displayListDialog(view);
                break;
        }
    }

    public void displayListDialog(View view) // displays a dialog allowing user to input both date and time
    {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.list_dialog);
        ListView listView = (ListView) dialog.findViewById(R.id.listView);
        String[] vals = null;
        EditText outputView = null;
        switch (view.getId()) {
            case R.id.UploadET:
                dialog.setTitle("Select upload type:");
                vals = getResources().getStringArray(R.array.UploadOptions);
                outputView = uploadET;
                break;
            case R.id.coordPrefET:
                dialog.setTitle("Select your coordinate preference:");
                vals = getResources().getStringArray(R.array.CoordinatePreference);
                outputView = coordPrefET;
                break;
        }

        ArrayAdapter adapter = new CustomDialogAdapter(getActivity(), R.layout.simple_list_item, vals);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view.findViewById(R.id.textview);
                coordPrefET.setText(tv.getText());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void autofill()
    {
        user.setText(manager.getString("name",""));
        host.setText(manager.getString("host", ""));
        uploadET.setText(manager.getString("uploadType", ""));
        coordPrefET.setText(manager.getString("coordPref", ""));
        phoneET.setText(manager.getString("phone",""));
    }

    private void initializeViews(View v)
    {
        user            = (TextView) v.findViewById(R.id.User);
        host            = (EditText) v.findViewById(R.id.ip);
        coordPrefET     = (EditText) v.findViewById(R.id.coordPrefET);
        uploadET        = (EditText) v.findViewById(R.id.UploadET);
        phoneET         = (EditText) v.findViewById(R.id.phoneET);
        saveConfig      = (Button)   v.findViewById(R.id.saveConfig);

        uploadET.setFocusable(false);
        coordPrefET.setFocusable(false);

        uploadET.setOnClickListener(this);
        saveConfig.setOnClickListener(this);
        coordPrefET.setOnClickListener(this);
    }

    public void Save(View view) //saves all data in the forms
    {
        SharedPreferences.Editor editor = manager.edit(); //create the preferences editor
        editor.putString("uploadType", uploadET.getText().toString());
        editor.putString("name", user.getText().toString());
        editor.putString("host", host.getText().toString());
        editor.putString("coordPref", coordPrefET.getText().toString());
        editor.putString("phone", phoneET.getText().toString());
        editor.commit(); //save these changes
        Toast.makeText(getActivity(), "Successfully saved.", Toast.LENGTH_SHORT).show(); //display a popup menu to verify the successful saving
        Intent splashScreen = new Intent(getActivity(), SplashActivity.class); //go back to the menu
        startActivity(splashScreen);

    }

}