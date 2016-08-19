package com.IntelligentWaves.xmltest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SetupActivity extends Activity implements OnItemSelectedListener{

	Button spot_tracker_button;
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
        setContentView(R.layout.setup_activity);
        Bundle extras=getIntent().getExtras();
        manager = PreferenceManager.getDefaultSharedPreferences(this);
        if(extras!=null)
        {
        	((TextView)findViewById(R.id.NameText)).setText("Fill out the following forms and then press save.");
        	Editor editor = manager.edit();
            editor.putInt("Step", 1);
            editor.commit();
        }

		BuildSpinner(manager);

		spot_tracker_button = (Button) findViewById(R.id.spot_tracker_button);
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		n=((EditText)findViewById(R.id.EditName)); //reference to the name edittext field
		user=((EditText)findViewById(R.id.User)); //reference to the name edittext field
		pass=((EditText)findViewById(R.id.Pass)); //reference to the unit edittext field
		host=((EditText)findViewById(R.id.ip));

		n.setText(manager.getString("Name",""));  // check stored preferences for name, if we have one fill the field out else set it to blank
		user.setText(manager.getString("User",""));  // check stored preferences for name, if we have one fill the field out else set it to blank
		pass.setText(manager.getString("Pass",""));  // do the same for the unit field
		host.setText(manager.getString("Host", ""));

	}
	
	public void BuildSpinner(SharedPreferences m) //build security spinner
	{
		//spinner=(Spinner) findViewById(R.id.SecuritySpinner);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Security_Type, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		spinner.setSelection(m.getInt("spinnerLocation", 0));
	}
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)  //adjusts the visibilty of forms based on the chosen security style
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
    }
	
	public void Save(View view) //saves all data in the forms
	{
		Editor editor = manager.edit(); //create the preferences editor
        editor.putString("Name", n.getText().toString());  //set the Name preference to whatever was typed into the Name field
        editor.putString("security", spinner.getSelectedItem().toString());
        editor.putInt("spinnerLocation", spinner.getSelectedItemPosition());
        editor.putString("User", user.getText().toString()); 
        editor.putString("Pass", pass.getText().toString()); 
        editor.putString("Host", host.getText().toString());
        editor.commit(); //save these changes
        //Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show(); //display a popup menu to verify the successful saving
        Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show(); //display a popup menu to verify the successful saving
		
		//Intent menuScreen = new Intent(getApplicationContext(), MenuScreenActivity.class); //go back to the menu
		//startActivity(menuScreen);
		finish();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent)
	{
		// TODO Auto-generated method stub
	}
}