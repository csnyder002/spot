package com.IntelligentWaves.xmltest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Cody.Snyder on 8/12/2016.
 */
public class Tab3 extends Fragment implements View.OnClickListener{
    Button breadcrumbs_button;
    Button saveConfig;
    EditText n;
    EditText user;
    EditText pass;
    EditText host;
    Spinner spinner;
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
        /*if(extras!=null)
        {
            runTutorial();
            ((TextView)findViewById(R.id.NameText)).setText("Fill out the following forms and then press save.");
            SharedPreferences.Editor editor = manager.edit();
            editor.putInt("Step", 1);
            editor.commit();
        }*/


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);



    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.setup_activity,container,false);
        n = ((EditText) v.findViewById(R.id.EditName)); //reference to the name edittext field
        user = ((EditText) v.findViewById(R.id.User)); //reference to the name edittext field
        pass = ((EditText) v.findViewById(R.id.Pass)); //reference to the unit edittext field
        host = ((EditText) v.findViewById(R.id.ip));
        breadcrumbs_button = (Button) v.findViewById(R.id.breadcrumbs_button);
        saveConfig = (Button) v.findViewById(R.id.saveConfig);

        n.setText(manager.getString("Name",""));  // check stored preferences for name, if we have one fill the field out else set it to blank
        user.setText(manager.getString("User",""));  // check stored preferences for name, if we have one fill the field out else set it to blank
        pass.setText(manager.getString("Pass",""));  // do the same for the unit field
        host.setText(manager.getString("Host", ""));
        breadcrumbs_button.setOnClickListener(this);
        saveConfig.setOnClickListener(this);
        //BuildSpinner(manager);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.breadcrumbs_button:
                toggleBreadcrumbs(view);
                break;
            case R.id.saveConfig:
                Save(view);
                break;
        }
    }

    public void toggleBreadcrumbs(View view) {
        String message = "Breadcrumbs is a feature that tracks your gps location through sms upload. ";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (running) // if breadcrumbs is running
        {
            builder.setTitle("Disable Breadcrumbs?");
            message += "Would you like to disable Breadcrumbs?";
            builder.setMessage(message);
            builder.setPositiveButton("Disable Breadcrumbs", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    disableBreadcrumbs();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        }
        else // if breadcrumbs isn't running
        {
            builder.setTitle("Enable Breadcrumbs?");
            message += "Are you sure you want to enable this feature?";
            builder.setMessage(message);
            builder.setPositiveButton("Enable Breadcrumbs", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Tab3.this.running = true;
                    enableBreadcrumbs();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        }
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void enableBreadcrumbs() // display dialog to configure and launch breadcrumbs
    {
        final CharSequence[] intervals = {"30 seconds", "1 minute", "5 minutes"};
        final int[] intervalsInMillis = {30000,60000,300000};


        AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
        builder2.setTitle("Set upload interval:");

        // sets intervals for user to choose from
        builder2.setSingleChoiceItems(intervals,0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Tab3.this.picked = which;
                    }
                });
        // starts breadcrumbs
        builder2.setPositiveButton("Begin Breadcrumbs", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getLocation(intervalsInMillis[picked]);
                breadcrumbs_button.setText("Disable Breadcrumbs");
            }
        });
        builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog alert2 = builder2.create();
        alert2.show();

    }

    public void disableBreadcrumbs()
    {
        running = false;
        locationManager.removeUpdates(locationListener);
        breadcrumbs_button.setText("Enable Breadcrumbs");
    }

    public void smsUpload(String message) // uploads via sms
    {
        String phoneNumber = "7578690037";

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        System.out.println("Message sent");
    }

    public String buildSMS() {
        return "TEST";
    }

    public void getLocation(int interval) // get's user's gps coordinates
    {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, interval, 0, locationListener); // will call startRunner(locaion)
        Toast.makeText(getActivity(), "Breadcrumbs will begin once GPS location is acquired.", Toast.LENGTH_SHORT).show();
    }

    private final LocationListener locationListener = new LocationListener() //waits to hear from the gps
    {
        public void onLocationChanged(Location location)
        {
            updateWithNewLocation(location);
        }
        public void onProviderDisabled(String provider){}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    private void updateWithNewLocation(Location location) //takes a location and breaks it into long lat to fill in forms
    {
        String temp = n.getText().toString() + ": " + location.getLatitude() + "," + location.getLongitude();
        smsUpload(temp);
    }

	/*public void startRunner(final Location location) {
		runnable = new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println(location.getLatitude() + "," + location.getLongitude());
				}
				catch (Exception e) {
					// TODO: handle exception
				}
				finally {
					// call the same runnable to call it at regular interval
					handler.postDelayed(this, 10000);
				}
			}
		};
		// call the same runnable to call it at regular interval
		handler.postDelayed(runnable, 10000);
	}*/

    public void GoToMain(View view)  //Intent call to go the main menu page
    {
        //Intent menuScreen = new Intent(getApplicationContext(), MenuScreenActivity.class);
        //startActivity(menuScreen);
        //finish();
    }

    public void BuildSpinner(SharedPreferences m) //build security spinner
    {
        //spinner=(Spinner) findViewById(R.id.SecuritySpinner);

        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Security_Type, android.R.layout.simple_spinner_item);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        //spinner.setAdapter(adapter);
        //spinner.setOnItemSelectedListener(this);
        //spinner.setSelection(m.getInt("spinnerLocation", 0));
    }

    /*public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)  //adjusts the visibilty of forms based on the chosen security style
    {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        if(pos==0)
        {
            findViewById(R.id.Username).setVisibility(View.VISIBLE);
            findViewById(R.id.User).setVisibility(View.VISIBLE);
            findViewById(R.id.Password).setVisibility(View.VISIBLE);
            findViewById(R.id.Pass).setVisibility(View.VISIBLE);
        }
        if(pos==1)
        {
            findViewById(R.id.Username).setVisibility(View.GONE);
            findViewById(R.id.User).setVisibility(View.GONE);
            findViewById(R.id.Password).setVisibility(View.GONE);
            findViewById(R.id.Pass).setVisibility(View.GONE);
        }
        if(pos==2)
        {
            findViewById(R.id.Username).setVisibility(View.GONE);
            findViewById(R.id.User).setVisibility(View.GONE);
            findViewById(R.id.Password).setVisibility(View.VISIBLE);
            findViewById(R.id.Pass).setVisibility(View.VISIBLE);
        }
    }*/

    public void Save(View view) //saves all data in the forms
    {
        SharedPreferences.Editor editor = manager.edit(); //create the preferences editor
        editor.putString("Name", n.getText().toString());  //set the Name preference to whatever was typed into the Name field
        //editor.putString("security", spinner.getSelectedItem().toString());
        //editor.putInt("spinnerLocation", spinner.getSelectedItemPosition());
        editor.putString("User", user.getText().toString());
        editor.putString("Pass", pass.getText().toString());
        editor.putString("Host", host.getText().toString());
        editor.commit(); //save these changes
        //Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show(); //display a popup menu to verify the successful saving
        Toast.makeText(getActivity(), "Successfully saved.", Toast.LENGTH_SHORT).show(); //display a popup menu to verify the successful saving
        Intent splashScreen = new Intent(getActivity(), SplashActivity.class); //go back to the menu
        startActivity(splashScreen);

    }

    public void runTutorial() //opens a dialog which instructs the user on the purpose of the page
    {
        AlertDialog.Builder tutorialDialog=new AlertDialog.Builder(getActivity());
        tutorialDialog.setTitle("Tutorial");
        tutorialDialog.setMessage("The Setup/Options page allows you to set a name which will be added to each future report.\nIt also is the place where you set your username, password, and the servers IP address. \n\nPlease fill out these forms and press save.");
        tutorialDialog.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });

        tutorialDialog.show();
    }

    public void resetTutorial(View view) //allows the user to redo the tutorial when they return to the main page
    {
        SharedPreferences.Editor editor = manager.edit();
        editor.putBoolean("FirstTime", true);
        editor.putInt("Step", 0);
        editor.commit();
    }
}