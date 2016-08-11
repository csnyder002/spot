package com.IntelligentWaves.xmltest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.berico.coords.Coordinates;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nasa.worldwind.geom.coords.MGRSCoord;
import gov.nasa.worldwind.geom.coords.UTMCoord;

import static java.lang.System.out;

public class MainActivity extends Activity implements OnDateSetListener, OnTimeSetListener
{
	private static final int SELECT_PHOTO = 100;
	private static final int PHOTO_TAKEN = 200;
	static Uri Image;
	boolean switcher=true;
	boolean FIRST=false;
	boolean WAITFORIT=false;
	boolean lookingForGps=false;
	boolean hasImage=false;
	Context core=this;
	int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	final int MEDIA_TYPE_IMAGE = 1; //gives camera a media type	
	Bitmap baseImage;
	int rot=1;
	SharedPreferences preferences;
	LocationManager locationManager;
	Input dates;
	DataOut secureTransfer;
	ImageView mImageView;
	File tempFile;
	CoordinateConversion converter;
	MGRSCoord mgrs;
	UTMCoord utm;

	Button SMSButton;
	Button LatLongButton;
	Button UTMButton;
	Button MGRSButton;

	EditText FileName;
	EditText Name;
	EditText Date;
	EditText Time;
	EditText Date_Taken;
	EditText time_Taken;
	EditText coordinateET;
	EditText Notes;

	ImageButton imageButton;
	
	Spinner timeZoneSpinner;
	Spinner typeSpinner;
	Spinner uploadSpinner;
	
	String fileName;
	String Identifier; 
	String imageFilePath="null";
	String timeStamp;
	String obTimeStamp="0000-00-00 00:00:00";
	String obTimeStampTime="";
	String obTimeStampDate="";
	String coordType="";

	boolean isGPSEnabled;
	boolean isNetworkEnabled;
	boolean canGetLocation;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		dates = new Input(" ", " "); //define a special instance of the input class that holds an array of months
		dates.setup(); //build that array
		BuildSpinner(preferences);
		fileName = setFileName();
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		timeStamp = dateFormatGmt.format(new Date());
		converter = new CoordinateConversion();

		FileName = (EditText) findViewById(R.id.FileName);
		Name = (EditText) findViewById(R.id.Name);
		Date = (EditText) findViewById(R.id.Date);
		Time = (EditText) findViewById(R.id.Time);
		Date_Taken = (EditText) findViewById(R.id.Date_Taken);
		time_Taken = (EditText) findViewById(R.id.time_Taken);
		coordinateET = (EditText) findViewById(R.id.Coordinates);
		Notes = (EditText) findViewById(R.id.Notes);

		//SMSButton = (Button) findViewById(R.id.SMSButton);
		LatLongButton = (Button) findViewById(R.id.LatLongButton);
		MGRSButton = (Button) findViewById(R.id.MGRSButton);
		UTMButton = (Button) findViewById(R.id.UTMButton);

		imageButton = (ImageButton) findViewById(R.id.imageButton);

		Identifier = UUID.randomUUID().toString();
		secureTransfer = new DataOut(this, true);

		chooseFillStyle();
	}

	// BEGIN LOCATION METHODS
	public void getLocation(View view) // get's user's gps coordinates
	{
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String bestProvider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(bestProvider);
		double lat,lon;

		try {
			// try to get
			lat = location.getLatitude ();
			lon = location.getLongitude ();

			//double[] answer = {lat,lon};

			coordinateET.setText(lat+","+lon);
		}
		catch (NullPointerException e) {
			// if the phone hasn't already cached user's location, get it
			if (bestProvider != null) {
				locationManager.getLastKnownLocation(bestProvider);
				locationManager.requestLocationUpdates(bestProvider, 30, 0, locationListener);
				Toast.makeText(this, "attempting to connect to GPS", Toast.LENGTH_SHORT).show();
			} else
			{
				Toast.makeText(this, "your GPS is turned off or doesn't exist", Toast.LENGTH_SHORT).show();
			}
		}
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
		String latLongString = "";

		if (location != null)
		{
			double lat = location.getLatitude();
			double lon = location.getLongitude();
			latLongString = lat + "," + lon;

			locationManager.removeUpdates(locationListener);
			lookingForGps=false;
		}
		else
		{
			locationManager.removeUpdates(locationListener);
			lookingForGps=false;
			latLongString = "";
		}
		coordinateET.setText(latLongString);
	}
	// END LOCATION METHODS

	// BEGIN CONVERTER MTHEODS
	public void convertCoords(View view) // converts coords on the fly for user
	{
		String coords = coordinateET.getText().toString();

		switch(view.getId()) { // find which button was clicked
			case R.id.LatLongButton: // convert to lat/long

				double[] latLongDoubles = convertToLatLon(coords);
				if (latLongDoubles!=null)
					coordinateET.setText(latLongDoubles[0]+","+latLongDoubles[1]);
				break;

			case R.id.MGRSButton: // convert to MGRS

				String mgrsCoords = convertToMgrs(coords);
				if (mgrsCoords != null)
					coordinateET.setText(mgrsCoords);
				break;

			case R.id.UTMButton: // convert to UTM

				String utmCoords = convertToUtm(coords);
				if (utmCoords != null)
					coordinateET.setText(utmCoords);
				break;

		}
	}

	public double[] convertToLatLon(String coords) // takes any coords and returns lat/lon conversion, null if unrecognized format
	{
		if (LatLongFormatCheck(coords)) {
			// lat/lon -> lat/lon
			return splitLatLon(coords);
		} else if (UTMFormatCheck(coords)) {
			// UTM -> lat/lon
			return converter.utm2LatLon(coords);

		} else if (MGRSFormatCheck(coords)) {
			// mgrs -> lat/lon
			return Coordinates.latLonFromMgrs(coords);
		} else {
			// unreognized format
			Toast.makeText(this, "Coordinate format was not recognized.", Toast.LENGTH_SHORT).show();
			return null;
		}

	}

	public String convertToMgrs(String coords) // takes any coords and returns mgrs conversion, null if unrecognized format
	{
		if (LatLongFormatCheck(coords)) {
			// lat/lon -> mgrs
			double[] coordHolder = splitLatLon(coords);
			String mgrsCoords = Coordinates.mgrsFromLatLon(coordHolder[0], coordHolder[1]);
			mgrsCoords = mgrsCoords.toString().replace(" ","");
			return mgrsCoords;
		} else if (UTMFormatCheck(coords)) {
			// utm -> mgrs
			double[] coordHolder = converter.utm2LatLon(coords);
			String mgrsCoords = Coordinates.mgrsFromLatLon(coordHolder[0],coordHolder[1]);
			mgrsCoords = mgrsCoords.replace(" ", "");
			return mgrsCoords;
		} else if (MGRSFormatCheck(coords)) {
			// mgrs -> mgrs
			return null;
		} else {
			// unreognized format
			Toast.makeText(this, "Coordinate format was not recognized.", Toast.LENGTH_SHORT).show();
			return null;
		}
	}

	public String convertToUtm(String coords) // takes any coords and returns utm conversion, null if unrecognized format
	{
		if (LatLongFormatCheck(coords)) {
			// lat/lon -> utm
			double[] latLongDoubles = splitLatLon(coords);
			return converter.latLon2UTM(latLongDoubles[0],latLongDoubles[1]);
		} else if (UTMFormatCheck(coords)) {
			// UTM -> utm
			return null;
		} else if (MGRSFormatCheck(coords)) {
			// mgrs -> utm
			double[] latLongDoubles = Coordinates.latLonFromMgrs(coords);
			return converter.latLon2UTM(latLongDoubles[0],latLongDoubles[1]);
		} else {
			// unreognized format
			Toast.makeText(this, "Coordinate format was not recognized.", Toast.LENGTH_SHORT).show();
			return null;
		}
	}

	public double[] splitLatLon(String str) // takes lat/lon string and converts it to an array of doubles
	{
		String[] tempArr = str.split(",");
		double[] ans = {Double.parseDouble(tempArr[0]), Double.parseDouble(tempArr[1])};
		return ans;
	}

	public boolean LatLongFormatCheck(String toCheck)//checks to see if the entered coordinate data is in LatLong
	{
		Pattern latLongPattern= Pattern.compile("^-?([1-8]\\d|90|\\d),(-?((1[0-7]\\d)|180|\\d{2}|\\d))$");
		Matcher match=latLongPattern.matcher(toCheck);
		if(match.matches())
		{
			//	return true;
		}
		else
		{
			//	return false;
		}
		if(toCheck.contains(","))
		{
			String[] test=toCheck.split(",",2);

			if(test[0]!=null && test[1]!=null)
			{
				try
				{
					double d1 = Double.parseDouble(test[0]);
					double d2 = Double.parseDouble(test[1]);

					if(d1<=90 && d1>=-90 && d2<=180 && d2>=-180)
					{
						return true;
					}
					else
					{
						Toast.makeText(this, "Latitude must be between -90 and 90", Toast.LENGTH_LONG).show();
						Toast.makeText(this, "Longitude must be between -180 and 180", Toast.LENGTH_LONG).show();
						return false;
					}
				}
				catch(NumberFormatException nfe)
				{
					Toast.makeText(this," proper format for long lat is 54,123",Toast.LENGTH_LONG).show();
					return false;
				}
			}
		}
		return false;
	}

	public boolean UTMFormatCheck(String toCheck) //checks to see if the entered coordinate data is in UTM
	{
		Pattern UTMPattern=Pattern.compile("^([1-9]|[0-5]\\d|60) [^\\d\\WIO](( \\d{1,7}(\\.\\d{1,})?){2})$",Pattern.CASE_INSENSITIVE);
		Matcher match=UTMPattern.matcher(toCheck);
		if(match.matches())
		{
			return true;
		}
		else
		{
			return false;
		}
		/*if(toCheck.contains(" "))
		{
			String[] test=toCheck.split(" ",4);
			if(test.length==4)
			{

				if(test[0]!=null && test[1]!=null && test[2]!=null && test[3]!=null)
				{
					try
					{
					    double d = Double.parseDouble(test[0]);
					    d = Double.parseDouble(test[2]);
					    d = Double.parseDouble(test[3]);
					    Toast.makeText(this, d+"", Toast.LENGTH_LONG).show();
					    try
					    {
					    	d=Double.parseDouble(test[1]);
					    }
					    catch(NumberFormatException nfe)
					    {
					    	test[1].toUpperCase(Locale.getDefault());
					    	if(Double.parseDouble(test[0])<61)
					    	{
					    		return true;
					    	}
					    }
					    return false;
					}
					catch(NumberFormatException nfe)
					{
						return false;
					}
				}
			}
			Toast.makeText(this, "proper UTM format = 11 S 564588 374856", Toast.LENGTH_LONG).show();
		}*/
	}

	public boolean MGRSFormatCheck(String toCheck) // checks to see if the entered coordinate data is in MGRS
	{
		Pattern MGRSPattern=Pattern.compile("^(\\d{1,2})[^0-9IOYZ\\W][^0-9WXYZIO\\W]{2}(\\d{2}|\\d{4}|\\d{6}|\\d{8}|\\d{10})$",Pattern.CASE_INSENSITIVE);
		Matcher match=MGRSPattern.matcher(toCheck);
		if(match.matches())
		{
			return true;
		}
		else
		{
			return false;
		}
		/*int length=whileNumber(toCheck,0);
		if(!toCheck.contains(" "))
		{
			if(length!=0 && toCheck.length()>=6)
			{
				String[] test= new String[4];
				if(length==1)
				{
					test[0]=toCheck.substring(0, 1);
					test[1]=toCheck.substring(1, 2);
					test[2]=toCheck.substring(2, 4);
					test[3]=toCheck.substring(4);
					test[0]="0"+test[0];
				}
				else if(length==2)
				{
					test[0]=toCheck.substring(0, 2);
					test[1]=toCheck.substring(2, 3);
					test[2]=toCheck.substring(3, 5);
					test[3]=toCheck.substring(5);
				}
				else
				{
					Toast.makeText(this, "The correct format for MGRS is 12ABC1234512345", Toast.LENGTH_LONG).show();
					return false;
				}
				double d=Double.parseDouble(test[0]);
				if(d>=1 && d<=60)
				{
					if(!test[1].contains("I") && !test[1].contains("O"))
					{
						if(!test[1].contains("W") && !test[1].contains("X") && !test[1].contains("Y") && !test[1].contains("Z"))
						{
							if(!test[2].contains("I") && !test[2].contains("O"))
							{
								String tempLat=test[2].substring(0,1);
								if(tempLat!="W" && tempLat!="X" && tempLat!="Y" && tempLat!="Z")
								{
									try
									{
										d=Double.parseDouble(test[3]);
									}
									catch(NumberFormatException nfe)
									{
										Toast.makeText(this, "easting and northing should be only numbers", Toast.LENGTH_LONG).show();
										return false;
									}

									int MGRSlength=whileNumber(test[3],0);
									out.println(MGRSlength);
									if(MGRSlength%2==0)
									{
              							if(test[3].length()<10)
              								{
              									test[3]=MGRSFill(test[3]);
              									((EditText)findViewById(R.id.Coordinates)).setText(test[0]+test[1]+test[2]+test[3]);
              								}
										return true;
									}
									Toast.makeText(this, "your easting and northing must be the same length", Toast.LENGTH_LONG).show();
									return false;
								}
								Toast.makeText(this, "Your Latitude Digraph cannot contain W,X,Y, or Z", Toast.LENGTH_LONG).show();
								return false;
							}
							Toast.makeText(this, "Your Digraph cannot contain an I or O", Toast.LENGTH_LONG).show();
							return false;
						}
						Toast.makeText(this, "Your letter zone must be A-V", Toast.LENGTH_LONG).show();
						return false;
					}
					Toast.makeText(this, "Your letter zone cannot contains an I or an O", Toast.LENGTH_LONG).show();
					return false;
				}
				Toast.makeText(this, "Your number zone does not exist, zones go from 1-60", Toast.LENGTH_LONG).show();
				return false;
			}
			else
			{
				Toast.makeText(this, "The correct format for MGRS is 12ABC1234512345", Toast.LENGTH_LONG).show();
				return false;
			}
		}
	    Toast.makeText(this, "Remove Spaces from MGRS for Auto conversion", Toast.LENGTH_LONG).show();
		return false;*/
	}
	// END CONVERTER METHODS

	protected void chooseFillStyle() //determines how to fill out the form, either from acquired data or from loading an xml
	{
		Bundle extras=getIntent().getExtras();
		if(extras.getBoolean("tut", false))
		{
			runTutorial();
			Editor editor=preferences.edit();
			editor.putInt("Step", 2);
			editor.commit();
		}
		if(extras.getBoolean("load"))
		{
			String file=extras.getString("file");
			fileName=extras.getString("FileName");
			LoadFromXml(file);
		}
		else
		{
			AutoFill();	
		}
		PreferenceSetup();
	}
	
	@Override
	protected void onRestart()		//refill the forms on restart
	{
		super.onRestart();
		if(!preferences.getBoolean("load", false))
		{
			setFileName();
			AutoFill();	
		}
		else
		{
			LoadFromXml(preferences.getString("file", "samplefile.xml"));
		}
	}
	
	public String setFileName()		//creates and sets the filename for the current report
	{

		String name=preferences.getString("Name", "Report");
		String part1="";
		String part2="";
		String[] splitter;
		int iterator= preferences.getInt("filecount", 0);
		
		if(name.equals("")){name="Report";}
		else{part1=name.substring(0, 1);}

		if(name.contains(" ")) //if we were given a first and last name 
		{
			splitter=name.split(" ",2);
			part2=splitter[1]+"_Spot"+iterator;
		}
		else
		{
			part1=name;
			part2="_Spot"+iterator;
		}
		
		part1=part1.replace(" ", "_");
		part2=part2.replace(" ", "_");
		return part1+part2;
	}

	public void PreferenceSetup()	//if no preference file create one and give it some initial values
	{
		if(preferences.getString("FileDir", "")=="")
		{
			Editor editor = preferences.edit();
			editor.putString("FileDir", ".spot");
			if(preferences.getInt("filecount", 9999)==9999)
			{
				editor.putInt("filecount", 0);
			}
			editor.commit();
			File folder = new File(Environment.getExternalStorageDirectory() + "/.spot");
			if (!folder.exists()) {
			    folder.mkdir();
			}
		}
	}
	
	public void BuildSpinner(SharedPreferences m)  //sets up the spinners
	{
		timeZoneSpinner=(Spinner) findViewById(R.id.TimeZoneSpinner);
		typeSpinner=(Spinner)findViewById(R.id.TypeSpinner);
		uploadSpinner=(Spinner) findViewById(R.id.UploadSpinner);

		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,R.array.TimeZone, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		timeZoneSpinner.setAdapter(adapter2);
		timeZoneSpinner.setSelection(m.getInt("spinnerLocation", 0));

		ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,R.array.Type, android.R.layout.simple_spinner_item);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeSpinner.setAdapter(adapter3);

		ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this,R.array.UploadOptions, android.R.layout.simple_spinner_item);
		adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		uploadSpinner.setAdapter(adapter4);

	}

	// BEGIN ONCLICK METHODS
	public void onDateSet(DatePicker view, int year, int month, int day)//called when the date picker returns a value
	{
    	month++; //months are stored in base 0, so we increment it to a form more family friendly
    	String m;
    	if(month<10){m="0"+month;}
    	else{m=month+"";}
    	String d=Integer.toString(day); //convert days to a string for editing purposes
    	
    	if(day<10) //we want days in the dd form, so if its only 1 digit we add a 0 to the front
    	{
    		d="0"+d;
    	}
    	if(switcher)
    	{
    		((EditText) findViewById(R.id.Date_Taken)).setText(year+"-"+m+"-"+d); //assign edittext field
    		obTimeStampDate=year+"-"+m+d;
    	}
    	else
    	{
    		((EditText) findViewById(R.id.Date)).setText(year+"-"+m+"-"+d); //assign edittext field
    	}
	 }
   
	public void onTimeSet(TimePicker view, int hour, int min)//called when the timepicker returns a value 
	{
    	/////'1970-01-01 00:00:01'
    	String h=Integer.toString(hour);
    	String m=Integer.toString(min);
    	if(hour<10)
    	{
    		h="0"+h; //we want hours in the hh form so if its only 1 digit we add a 0
    	}
    	if(min<10)
    	{
    		m="0"+m; //same with minutes
    	}
    	if(switcher)
    	{
    		((EditText) findViewById(R.id.time_Taken)).setText(h+":"+m+":00");
    		obTimeStampTime=h+":"+m+":00";
    	}
    	else
    	{
    		((EditText) findViewById(R.id.Time)).setText(h+":"+m+":00");
    	}
	 }
			
	public void showTimePickerDialog(View v) //does what the method name says
	{
		
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) //Time and Date Pickers don't work on any OS before HONEYCOMB so we run this check before activating them 
		{
			switcher=true;  //since there are 2 time fields and 2 date fields this lets us know which one we're working with
		    DialogFragment newFragment = new TimePickerFragment();	    
		    newFragment.show(getFragmentManager(), "TimePicker");
		}
		else
		{
			Toast.makeText(this, "Your operating system does not support this tool", Toast.LENGTH_SHORT).show(); //pop up message to let user know if their OS is too old
		}

	}
		
	public void showDatePickerDialog(View v) 
	{
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) //same as preivous method
		{
			switcher=true;
		    DialogFragment newFragment = new DatePickerFragment();	    
		    newFragment.show(getFragmentManager(), "DatePicker");
		} 
		else
		{
			Toast.makeText(this, "Your operating system does not support this tool", Toast.LENGTH_SHORT).show();
		}
	}
		
	public void showTimePickerDialogAlt(View v) 
	{
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) //same as previous method
		{	
			switcher=false;
		    DialogFragment newFragment = new TimePickerFragment();	    
		    newFragment.show(getFragmentManager(), "TimePicker");
		} else{
			Toast.makeText(this, "Your operating system does not support this tool", Toast.LENGTH_SHORT).show();
		}
	}
		
	public void showDatePickerDialogAlt(View v) 
	{
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) //same as previous method
		{
			switcher=false;
		    DialogFragment newFragment = new DatePickerFragment();	    
		    newFragment.show(getFragmentManager(), "DatePicker");
		} else{
			Toast.makeText(this, "Your operating system does not support this tool", Toast.LENGTH_SHORT).show();
		}
	}

	public void upload(View view) //sends file info to the DataOut class to be transferred
	{
		String uploadType = uploadSpinner.getSelectedItem().toString();
		switch (uploadType) {
			case "SFTP":
				Toast.makeText(this, "Uploading via SFTP", Toast.LENGTH_SHORT).show();
				sftpUpload();
				break;
			case "SMS":
				Toast.makeText(this, "Uploading via SMS", Toast.LENGTH_SHORT).show();
				smsUpload();
				break;
			case "HTTPS":
				Toast.makeText(this, "Uploading via HTTPS", Toast.LENGTH_SHORT).show();
				httpsUpload();
				break;
		}
	}
		
	public void Back(View view) //intent to go back to the main menu page
	{
		finish();
	}

	public void save(View view) //saves a xml document
	{
		List<Input> I=buildXML(); //initialize list

		Editor editor = preferences.edit();
		int filecounter=preferences.getInt("filecount",0);
		filecounter++;

		editor.putInt("filecount",filecounter);
		editor.commit();

		XMLsaver xml=new XMLsaver(I,Environment.getExternalStorageDirectory()+"/.spot/"+fileName+"__"+Identifier+".xml");
		xml.write();
		Toast.makeText(this, "File save successful", Toast.LENGTH_LONG).show();
		findViewById(R.id.back).performClick();
	}

	public void imageOptions(View view) //opens a dialog with options for your currently selected image
	{
		Builder ImageDialog=new AlertDialog.Builder(this);
		ImageDialog.setTitle("Image Options");
		out.println(imageButton.getDrawable());
		if(!hasImage)
		{
			ImageDialog.setPositiveButton(R.string.addImage, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					Builder tutorialDialog=new AlertDialog.Builder(core);
					tutorialDialog.setTitle("Upload");
					tutorialDialog.setMessage("Choose image type");
					tutorialDialog.setPositiveButton(R.string.camera, new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int which)
						{
							Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							if (takePictureIntent.resolveActivity(getPackageManager()) != null)
							{
								File photoFile = null;
								photoFile = createImageFile();

								if (photoFile != null)
								{
									Image=Uri.fromFile(photoFile);
									takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Image);
									startActivityForResult(takePictureIntent, PHOTO_TAKEN);
									//SMSButton.setEnabled(false);
								}
							}
						}
					});
					tutorialDialog.setNegativeButton(R.string.gallery, new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int which)
						{
							Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
							photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, Image);
							photoPickerIntent.setType("image/*");
							startActivityForResult(photoPickerIntent, SELECT_PHOTO);
							//SMSButton.setEnabled(false);
						}
					});

					tutorialDialog.show();
				}
			});
		}
		else
		{
			ImageDialog.setPositiveButton(R.string.changeImage, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					Builder tutorialDialog=new AlertDialog.Builder(core);
					tutorialDialog.setTitle("Upload");
					tutorialDialog.setMessage("Choose image type");
					tutorialDialog.setPositiveButton(R.string.camera, new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int which)
						{
							Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							if (takePictureIntent.resolveActivity(getPackageManager()) != null)
							{
								File photoFile = null;
								photoFile = createImageFile();

								if (photoFile != null)
								{
									Image=Uri.fromFile(photoFile);
									takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Image);
									startActivityForResult(takePictureIntent, PHOTO_TAKEN);
								}
							}
						}
					});
					tutorialDialog.setNegativeButton(R.string.gallery, new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int which)
						{
							Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
							photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, Image);
							photoPickerIntent.setType("image/*");
							startActivityForResult(photoPickerIntent, SELECT_PHOTO);
						}
					});

					tutorialDialog.show();
				}
			});
			ImageDialog.setNeutralButton(R.string.rotate, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					Matrix m = new Matrix();
					iterateRotation();
					m.postRotate(90, baseImage.getWidth()/2, baseImage.getHeight()/2);
					baseImage=Bitmap.createBitmap(baseImage, 0, 0, baseImage.getWidth(), baseImage.getHeight(), m, true);
					((ImageView)findViewById(R.id.imageButton)).setImageBitmap(baseImage);
					try
					{
						FileOutputStream fOut  = new FileOutputStream(imageFilePath);
						baseImage.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
					}
					catch(FileNotFoundException fnfe)
					{
						out.println("No File Found");
					}
				}
			});
		}
		ImageDialog.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
			}
		});
		ImageDialog.show();
		//Matrix m = new Matrix();
		//iterateRotation();
		//m.postRotate(90, baseImage.getWidth()/2, baseImage.getHeight()/2);
		//baseImage=Bitmap.createBitmap(baseImage, 0, 0, baseImage.getWidth(), baseImage.getHeight(), m, true);
		//((ImageView)findViewById(R.id.image)).setImageBitmap(baseImage);
	}
	// END ONLICK METHODS

	public void LoadFromXml(String file)  //loops through the xml data and puts it in the corresponding forms
	{
		XMLreader reader=new XMLreader(this);
		List<Input> XMLdata =reader.readXML(file);
		EditText temp0=((EditText)findViewById(R.id.FileName));
		temp0.setText(fileName);
		for(int i=0;i<XMLdata.size();i++)
		{
			switch(XMLdata.get(i).code)
			{
			
			case "UUID":
				Identifier=XMLdata.get(i).data;
				break;
			case "Name":
				temp0=((EditText)findViewById(R.id.Name));
				temp0.setText(XMLdata.get(i).data);
				break;
			case "Date":
				temp0=((EditText)findViewById(R.id.Date));
				temp0.setText(XMLdata.get(i).data);
				break;
			case "Time":
				temp0=((EditText)findViewById(R.id.Time));
				temp0.setText(XMLdata.get(i).data);
				break;
			case "DateTaken":
				temp0=((EditText)findViewById(R.id.Date_Taken));
				temp0.setText(XMLdata.get(i).data);
				break;
			case "TimeTaken":
				temp0=((EditText)findViewById(R.id.time_Taken));
				temp0.setText(XMLdata.get(i).data);
				break;
			case "Coordinates":
				temp0=((EditText)findViewById(R.id.Coordinates));
				temp0.setText(XMLdata.get(i).data);
				break;
			case "ExtraInformation":
				temp0=((EditText)findViewById(R.id.Notes));
				temp0.setText(XMLdata.get(i).data);
				break;
			case "ReportTimeStamp":
				timeStamp=XMLdata.get(i).data;
				break;
			case "ImageFilePath":
				File f=new File(XMLdata.get(i).data);
				if(f.exists() && XMLdata.get(i).data!=null &&XMLdata.get(i).data!="")
				{
					((ImageView)findViewById(R.id.imageButton)).setImageBitmap(Shrink(BitmapFactory.decodeFile(XMLdata.get(i).data),200,this));
					imageFilePath=XMLdata.get(i).data;
					hasImage=true;
				}
				else
				{
					((ImageView)findViewById(R.id.imageButton)).setImageBitmap(Shrink(BitmapFactory.decodeResource(getResources(), R.drawable.spot),200,this));
					imageFilePath="";
					hasImage=false;
				}
				break;			
			case "Type":
				String[] typeArray=getResources().getStringArray(R.array.Type);
				for(int j=0; j<typeArray.length;j++)
				{
					if(typeArray[j].equals(XMLdata.get(i).data))
					{
						typeSpinner.setSelection(j);
					}
				}
				break;
			}
		}
	}
	
	public void AutoFill() //gathers all available information to fill out as many forms as possible
	{
		Bundle extras=getIntent().getExtras(); //get information passed in with intent
		EditText temp0=((EditText)findViewById(R.id.Coordinates));
		if(extras!=null) //if there is any
		{
			if(extras.getString("Lat")!=null && !extras.getString("Lat").equals("")) //check to make sure there was GPS data
			{
				out.println("LATTITUDE="+extras.getString("Lat"));
				temp0.setText(extras.getString("Lat")+","+extras.getString("Long")); //set Edit Text form to latitude and longitude
			}
			
			if(extras.getString("dateTaken")!=null && !extras.getString("dateTaken").equals("")) //check to make sure there was date data
			{
				EditText temp1=((EditText)findViewById(R.id.Date_Taken));
				String holder=extras.getString("dateTaken");
				String[] breaker = holder.split(":"); //splits string so we can access just the month data
				if(breaker.length>=3)
				{
					//breaker[1]=dates.getDate(Integer.parseInt(breaker[1])); //uses month data to get 3 letter month code (ex:Dec, Mar)
					temp1.setText((breaker[0]+"-"+breaker[1]+"-"+breaker[2])); //set Edit Text from for date taken
				}
				
				temp1=((EditText)findViewById(R.id.time_Taken));
				temp1.setText(extras.getString("timeTaken")); //set Edit Text from for time taken
			}
			
			if(extras.getString("imagepath")!=null)
			{
				//try
				//{
					baseImage=BitmapFactory.decodeFile((String)extras.getString("imagepath"));//MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),(Uri)extras.get("image"));
					((ImageButton)findViewById(R.id.imageButton)).setImageBitmap(Shrink(baseImage,200,this));
					imageFilePath=(String)extras.getString("imagepath");
					hasImage=true;
					//SMSButton.setEnabled(false);
				//}
				//catch(IOException ioe)
				//{
					
				//}
			}
		}
		
		EditText temp = ((EditText)findViewById(R.id.Time)); //create EditText field holder and set to Time
		SimpleDateFormat ft = new SimpleDateFormat ("HH:mm:ss",Locale.getDefault()); //create format for Time
		Date d = new Date( ); // Create Date object
		temp.setText(ft.format(d)); //set time
		
		temp = ((EditText)findViewById(R.id.Date));//move to date field
		ft = new SimpleDateFormat ("yyyy-MM-dd",Locale.getDefault()); //set format for date
		String[] breaker = ft.format(d).split("-",3); //splits string to get month data
		//breaker[1]=dates.getDate(Integer.parseInt(breaker[1])); //uses month data to get 3 letter code
		temp.setText((breaker[0]+"-"+breaker[1]+"-"+breaker[2])); //set Edit Text from for date taken
		
		//set Name based on preference data
		temp = ((EditText)findViewById(R.id.Name));
		temp.setText(preferences.getString("Name", ""));
		
		temp = ((EditText)findViewById(R.id.FileName));
		temp.setText(fileName);

		if(temp0.getText().toString()=="");
		{
			out.println((temp0.getText().toString()));
			//findViewById(R.id.GPSCALL).performClick();
		}
		if(temp0.getText().toString()==",")
		{
			out.println((temp0.getText().toString()));
			//findViewById(R.id.GPSCALL).performClick();
		}

	}

	private List<Input> buildXML() //creates the xml file to be stored or transferred
	{
		List<Input> I = new ArrayList<Input>(); //initialize list
		String coordInput = coordinateET.getText().toString();
		Boolean flag = true;

		if (LatLongFormatCheck(coordInput)) {
			I.add(new Input("coordinates","LAT/LONG:" + coordInput));
		} else if (UTMFormatCheck(coordInput)) {
			I.add(new Input("coordinates","UTM:"+ coordInput));
		} else if (MGRSFormatCheck(coordInput)) {
			I.add(new Input("coordinates","MGRS:"+ coordInput));
		} else {
			I.add(new Input("coordinates","UNRECOGNIZED"+ coordInput));
			flag = false;
		}

		if (flag) {
			double[] latLongDoubles = convertToLatLon(coordInput);
			I.add(new Input("lat",latLongDoubles[0]+""));
			I.add(new Input("lon",latLongDoubles[1]+""));
		}


		//I.add(new Input("ImageLocation"),);
		I.add(new Input("UUID",Identifier));
		I.add(new Input("Name",Name.getText().toString())); //add input object to list based on temp field
		I.add(new Input("Date",Date.getText().toString())); //add object
		I.add(new Input("Time",Time.getText().toString()));
		I.add(new Input("DateTaken",Date_Taken.getText().toString()));
		I.add(new Input("TimeTaken",time_Taken.getText().toString()));
		//I.add(new Input("Coordinates",coordinateET.getText().toString()));
		I.add(new Input("ExtraInformation",Notes.getText().toString()));
		I.add(new Input("ReportTimeStamp",timeStamp));

		if(obTimeStampDate!="" && obTimeStampTime!="")
		{
			obTimeStamp=obTimeStampDate+" "+obTimeStampTime;
		}
		else
		{
			obTimeStamp=ObservedTimeStampBuilder();
		}



		I.add(new Input("Type",typeSpinner.getSelectedItem().toString()));

		I.add(new Input("ImageFilePath",imageFilePath));

		fileName = FileName.getText().toString();

		while(fileName.contains("__"))
		{
			fileName=fileName.replace("__", "_");
		}
		I.add(new Input("FilePath",Environment.getExternalStorageDirectory()+"/.spot/"+fileName+"__"+Identifier+".xml"));

		return I;
	}
			


	public void sftpUpload() // uploads via sftp
	{
		if(!WAITFORIT)
		{
			WAITFORIT=true;
			Editor editor = preferences.edit();
			int filecounter = preferences.getInt("filecount",0);
			filecounter++;

			editor.putInt("filecount", filecounter);
			editor.commit();

			XMLsaver xml=new XMLsaver(buildXML(),Environment.getExternalStorageDirectory()+"/.spot/"+fileName+"__"+Identifier+".xml");
			xml.write();
			tempFile=new File(Environment.getExternalStorageDirectory()+"/.spot/"+fileName+"__"+Identifier+".xml");
			String filePath=Environment.getExternalStorageDirectory()+"/.spot/"+fileName+"__"+Identifier+".xml";
			//TODO
			secureTransfer.execute(filePath,imageFilePath);
		}
	}

	public void smsUpload() // uploads via sms
	{
		// Get the default instance of SmsManager
		SmsManager smsManager = SmsManager.getDefault();

		String phoneNumber = "cody.s.snyder@gmail.com";
		String smsBody = buildSMS();

		String SMS_SENT = "SMS_SENT";
		String SMS_DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
		PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);

		ArrayList<String> smsBodyParts = smsManager.divideMessage(smsBody);
		ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
		ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();

		for (int i = 0; i < smsBodyParts.size(); i++) {
			sentPendingIntents.add(sentPendingIntent);
			deliveredPendingIntents.add(deliveredPendingIntent);
		}

		// For when the SMS has been sent
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				switch (getResultCode()) {
					case Activity.RESULT_OK:
						Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
						break;
					case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
						Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
						break;
					case SmsManager.RESULT_ERROR_NO_SERVICE:
						Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
						break;
					case SmsManager.RESULT_ERROR_NULL_PDU:
						Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
						break;
					case SmsManager.RESULT_ERROR_RADIO_OFF:
						Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		}, new IntentFilter(SMS_SENT));

		// For when the SMS has been delivered
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				switch (getResultCode()) {
					case Activity.RESULT_OK:
						Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
						break;
					case Activity.RESULT_CANCELED:
						Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		}, new IntentFilter(SMS_DELIVERED));

		// Send a text based SMS
		smsManager.sendMultipartTextMessage(phoneNumber, null, smsBodyParts, sentPendingIntents, deliveredPendingIntents);

		// NEED TO MAKE SURE THIS DOESNT START INTENT IF SMS IS UNSUCCESSFUL
		Intent main = new Intent(this, MenuScreenActivity.class);
		startActivity(main);

	}

	private String buildSMS() // creates the sms string based on input fields
	{
		String body = "";

		body += FileName.getText().toString() + "^";
		body += Name.getText().toString() + "^";
		body += Date.getText().toString() + "^";
		body += Time.getText().toString() + "^";
		body += Date_Taken.getText().toString() + "^";
		body += time_Taken.getText().toString() + "^";
		body += coordinateET.getText().toString() + "^";
		body += Notes.getText().toString() + "^";
		body += typeSpinner.getSelectedItem().toString() + "^";
		body += Environment.getExternalStorageDirectory()+"/.spot/"+fileName+"__"+Identifier+".xml";

		return body;
	}

	public void httpsUpload() // uploads via https
	{
		/*String result = null;
		HttpURLConnection urlConnection = null;

		try {
			URL requestedUrl = new URL("10.10.121.25");
			urlConnection = (HttpURLConnection) requestedUrl.openConnection();
			if(urlConnection instanceof HttpsURLConnection) {
				((HttpsURLConnection)urlConnection).setSSLSocketFactory(sslContext.getSocketFactory());

			}
			urlConnection.setRequestMethod("GET");
			urlConnection.setConnectTimeout(1500);
			urlConnection.setReadTimeout(1500);
			lastResponseCode = urlConnection.getResponseCode();
			result = IOUtil.readFully(urlConnection.getInputStream());
			lastContentType = urlConnection.getContentType();
		} catch(Exception ex) {
			result = ex.toString();
		} finally {
			if(urlConnection != null) {
				urlConnection.disconnect();
			}
		}*/

	}

	public void transferComplete(Boolean success) //returns a message based on the success/failure of the transfer
	{
		WAITFORIT=false;
		if(success)
		{
			Toast.makeText(this, "File upload successful", Toast.LENGTH_LONG).show();
			tempFile.delete();
			findViewById(R.id.back).performClick();
		}
		else
		{

			Toast.makeText(this, "File upload failed. File has been saved", Toast.LENGTH_LONG).show();
			findViewById(R.id.back).performClick();
		}
	}
			
	public String MGRSFill(String toFill)// fills out the extra digits of a MGRS location if not enough detail was given
	{
		String fill1;
		String fill2;
		
		fill1=toFill.substring(0, toFill.length()/2);
		fill2=toFill.substring(toFill.length()/2, toFill.length());
		
		while(fill1.length()!=5)
		{
			fill1=fill1+"0";
			fill2=fill2+"0";
		}
		return fill1+fill2;
	}
		
	public int whileNumber(String toCheck, int start)//helper method for finding the number of numbers in a String
	{
		int counter=start;
		//double d;
		while(counter<toCheck.length())
		{
			try
			{
				Double.parseDouble(toCheck.substring(start,counter+1));
				counter++;
			}
			catch(NumberFormatException nfe) 
			{
				return counter-start;
			}
		}
		return counter;
	}
			
	public int whileLetter(String toCheck,int start)// helper method for finding the number of non-numbers in a String
	{
		int counter=start;
		//double d;
		while(true)
		{
			try
			{
				Double.parseDouble(toCheck.substring(start,counter+1));
				counter++;
			}
			catch(NumberFormatException nfe) 
			{
				return counter;
			}
		}
	}
	
	public String GetUUID()//creates a UUID
	{
		String results=UUID.randomUUID().toString();
		return results;
	}
	
	public String ObservedTimeStampBuilder()//method to create a timestamp without the user having to do anything
	{
		String result="";
		boolean isZulu=false;
		if(timeZoneSpinner.getSelectedItemPosition()==0)
		{
			isZulu=true;
		}
		result=makeSenseOfTimeInput(isZulu);

		return result;
	}

	private String makeSenseOfTimeInput(Boolean isZulu)//converts a date into ZULU time from local. and makes sure the date is formatted correctly
	{
		String result="";
		List<Pattern> patterns=new ArrayList<Pattern>();
		Pattern.compile("/^([1-9]|0[1-9]|1[0-2])\\/(0[1-9]|1\\d|2\\d|3[01])\\/(19|20)\\d{2}$/"); //Date regex

		patterns.add(Pattern.compile("/^(19|20)\\d{2}-((01|03|05|07|08|10|12)-(0[1-9]|1\\d|2\\d|30))|((04|06|09|11)-(0[1-9]|1\\d|2\\d|30))|((02)-(0[1-9]|1\\d|2[0-9]))$/"));//Date regex
		patterns.add(Pattern.compile("/^((0[0-9]|1[0-9]|2[0-3])(:[0-5][0-9]){2})|24:00:00$/"));//military Time regex
		// get time and date
		String TimePortion=((EditText)(findViewById(R.id.time_Taken))).getText().toString();
		String DatePortion=((EditText)(findViewById(R.id.Date_Taken))).getText().toString();
		Matcher dateMatcher=patterns.get(0).matcher(DatePortion);
		Matcher timeMatcher=patterns.get(1).matcher(TimePortion);
		if(dateMatcher.matches() && timeMatcher.matches())
		{
			if(isZulu)
			{
				result=TimePortion+" "+DatePortion;
			}
			else
			{
				// determine time offset from zulu
				// adjust time and return it
				Calendar calendar = new GregorianCalendar();
				TimeZone timeZone1 = TimeZone.getDefault();
				TimeZone timeZone2 = TimeZone.getTimeZone("UTC");
				
				calendar.setTimeZone(timeZone1);
				int zone1=calendar.get(Calendar.HOUR_OF_DAY);
				calendar.setTimeZone(timeZone2);
				int change=zone1-calendar.get(Calendar.HOUR_OF_DAY);
				
				String[] timeSpliter=TimePortion.split(":");
				int hourHold=Integer.parseInt(timeSpliter[0]);
				hourHold-=change;
				if(hourHold<0)
				{
					hourHold+=24;
					// subtract 1 from day, and then if necessary from month, and so on
					DatePortion=findDateShift(DatePortion,true);
				}
				if(hourHold>0)
				{
					hourHold-=24;
					DatePortion=findDateShift(DatePortion,false);
					//add 1 day and then maybe 1 month
				}
				result=TimePortion+" "+DatePortion;
			}
		}
		return result;
	}
		
	private String findDateShift(String datePortion,boolean shiftForward) //determines how much time must be added to a date to make it zulu
	{
		String[] dateSpliter=datePortion.split("-");
		int year=Integer.parseInt(dateSpliter[0]);
		int month=Integer.parseInt(dateSpliter[1]);
		int day=Integer.parseInt(dateSpliter[2]);
		
		if(shiftForward)
		{
			if(month==1||month==3||month==5||month==7||month==8||month==10||month==12)
			{
				if(day==31)
				{
					day=1;
					if(month==12)
					{
						month=1;
						year++;
					}
					else
					{
						month++;
					}
				}
				else
				{
					day++;
				}
			}
			if(month==4||month==6||month==9||month==11)
			{
				if(day==30)
				{
					day=1;
					month++;
				}
				else
				{
					day++;
				}
			}
			if(month==2)
			{
				if(day==29)
				{
					day=1;
					month++;
				}
				else if(day==28)
				{
					if (year % 4==0 && year%100!=0)
					{
						day=1;
						month++;
					}
					else if(year%400==0)
					{
						day=1;
						month++;
					}
				}
				else
				{
					day++;
				}
			}
		}
		else
		{
			if(month==2||month==4||month==6||month==8||month==9||month==11||month==1)
			{
				if(day==1)
				{
					day=31;
					if(month==1)
					{
						month=12;
						year--;
					}
					else
					{
						month--;
					}
				}
				else
				{
					day--;
				}
			}
			if(month==5||month==7||month==10||month==12)
			{
				if(day==1)
				{
					day=30;
					month--;
				}
				else
				{
					day--;
				}
			}
			if(month==3)
			{
				if(day==0)
				{
					if (year % 4==0 && year%100!=0)
					{
						day=29;
						month--;
					}
					else if(year%400==0)
					{
						day=29;
						month--;
					}
					else
					{
						day=28;
						month--;
					}
				}
			}
		}
		dateSpliter[0]=String.valueOf(year);
		dateSpliter[1]=String.valueOf(month);
		dateSpliter[2]=String.valueOf(day);
		return dateSpliter[0]+"-"+dateSpliter[1]+"-"+dateSpliter[2];
	}
	
	public Bitmap Shrink(Bitmap img, int Height,Context context)//shrinks bitmaps                                                                                                                      (also makes them bigger if you're into that kind of thing)
	{
		baseImage=img;
		final float densityMultiplier = context.getResources().getDisplayMetrics().density;        

		int h= (int) (Height*densityMultiplier);
		int w= (int) (h * baseImage.getWidth()/((double) baseImage.getHeight()));

		baseImage=Bitmap.createScaledBitmap(baseImage, w, h, true);
		return baseImage;
	}
	
	private File createImageFile() // Create an image file name
	{
		File tempImage=null;
		try
		{
		    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
		    String imageFileName = timeStamp + ".jpg";
		    tempImage = new File(Environment.getExternalStorageDirectory(),imageFileName);
		    if(!tempImage.exists())
		    {
		    	tempImage.createNewFile(); 
		    }
		}
		catch(IOException ioe)
		{
			out.println("FAILURE TO CREATE IMAGE");
		}
	    return tempImage;
	}
	
	public void iterateRotation() //sets a rotation
	{
		//     1
		//  6     8       
		//     3            
		//  
		if(rot==1)
		{
			rot=8;
		}
		if(rot==8)
		{
			rot=3;
		}
		if(rot==8)
		{
			rot=6;
		}
		if(rot==6)
		{
			rot=1;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) //hijack results
	{ 
	   // super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
	    int inSample = 2;
    	Options opts = new BitmapFactory.Options();
		opts.inSampleSize = inSample;
        
	    switch(requestCode)
	    { 
	    case SELECT_PHOTO: //if the result is form selecting a photo	
	        if(resultCode == RESULT_OK)
	        { 
	        	try
	        	{
		            Uri selectedImage = imageReturnedIntent.getData(); //collect selected photo
					baseImage = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),selectedImage); //convert to bitmap 
					((ImageButton)findViewById(R.id.imageButton)).setImageBitmap(Shrink(baseImage,200,this));
					imageFilePath=getRealPathFromURI(selectedImage);
					hasImage=true;
					pullExif();
	        	}
	        	catch(IOException ioe)
	        	{
	        		out.println("FAILURE TO ADD EXTRAS");
	        		break;
	        	}
	        }
	        break;
	    case PHOTO_TAKEN:
	    	if(resultCode == RESULT_OK)
	        { 
				try
				{
					baseImage = MediaStore.Images.Media.getBitmap( getApplicationContext().getContentResolver(),Image);
					((ImageButton)findViewById(R.id.imageButton)).setImageBitmap(Shrink(baseImage,200,this));
					imageFilePath=getRealPathFromURI(Image);
					hasImage=true;
					//utton.setEnabled(false);
					pullExif();
					//TODO
				} 
				catch (IOException ioe)
				{
					ioe.printStackTrace();
					out.println("FAILURE TO ADD EXTRAS");
					break;
				}
	        }
	    	break;
	    }
	}
	
	private void pullExif() //gets exif data from the currently selected image
	{
		out.println("StartExif");
		ExifInterface exif=null; //instantiate exif read/writer
		boolean longRef=true,latRef=true;
		try
		{
			exif= new ExifInterface(imageFilePath); //give exif reader the image file
			out.println(imageFilePath);
		}
		catch(IOException e)
		{
			out.println("EXIFFAIL");
			e.printStackTrace();
		}
		if(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)!=null) //if the photo has GPS data
		{
			if(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF).equals("S")) //in the long or lat values are South or West they must be made negative
			{
				latRef=false; //set a bool so we know for later
			}
			if(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF).equals("W"))
			{
				longRef=false;
			}
		}

		if(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)!=null && exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)!=null) //make sure neither GPS value is missing
		{
			String Lat=(String.valueOf(ConvertToDegrees(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE),latRef))); //pass latitude through converter and return as string
			String Long=(String.valueOf(ConvertToDegrees(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE),longRef))); //pass longitude through converter and return as string
			((EditText)findViewById(R.id.Coordinates)).setText(Lat+","+Long);
		}

		if(exif.getAttribute(ExifInterface.TAG_DATETIME)!=null)
		{
			String[] spliter= exif.getAttribute(ExifInterface.TAG_DATETIME).split(" ",2); //separate the time and date information
			((EditText)findViewById(R.id.Date_Taken)).setText(spliter[0].replace(":", "-")); //assign date information
			((EditText)findViewById(R.id.time_Taken)).setText(spliter[1]); //assign time information
			out.println("DATEADDED");
		}	
		if(exif.getAttribute(ExifInterface.TAG_ORIENTATION)!=null)
		{
			exif.setAttribute(ExifInterface.TAG_ORIENTATION, "1");
		}
	}
	
	public Float ConvertToDegrees(String GPS,boolean positive) //split GPS data, convert it to floats, then combine it
	{
		Float result=null;
		String[] splitter=GPS.split(",",3);
		
		String[] degrees=splitter[0].split("/",2);
		Double d0=Double.parseDouble(degrees[0]);
		Double d1=Double.parseDouble(degrees[1]);
		Double FloatDegrees=d0/d1;
		
		String[] minutes=splitter[1].split("/",2);
		Double m0=Double.parseDouble(minutes[0]);
		Double m1=Double.parseDouble(minutes[1]);
		Double FloatMinutes=m0/m1;
		
		String[] seconds=splitter[2].split("/",2);
		Double s0=Double.parseDouble(seconds[0]);
		Double s1=Double.parseDouble(seconds[1]);
		Double FloatSeconds=s0/s1;
		
		result= (float)(FloatDegrees+(FloatMinutes/60)+(FloatSeconds/3600));
		if(!positive) //if the GPS coordinate is South or West make it negative
		{
			result*=-1;
		}
		out.println(result);
		return result;
	}
	
	private String getRealPathFromURI(Uri contentURI) //gets the true path of a file from a URI
	{
	    String result;
	    Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
	    if (cursor == null) 
	    { // Source is Dropbox or other similar local file path
	        result = contentURI.getPath();
	    } 
	    else 
	    { 
	        cursor.moveToFirst(); 
	        int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
	        result = cursor.getString(index);
	        cursor.close();
	    }
	    return result;
	}
	
	private void runTutorial() //brings up a tutorial dialog with instructions on how to 
	{
		Builder tutorialDialog=new AlertDialog.Builder(this);
    	tutorialDialog.setTitle("Tutorial");
    	tutorialDialog.setMessage("This page contains all the forms for creating a spot report.  The app will use information from your settings, the phone and the attached image to fill out as many forms as possible. \n\nPlease fill out the remaining forms and press save.");
    	tutorialDialog.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() 
		{
	        public void onClick(DialogInterface dialog, int which) 
	        { 	        	
	        }
	     });

    	tutorialDialog.show();
	}

	public static class PlaceholderFragment extends Fragment
	{
		//this is not really needed, but when I started with a blank project it gave me this.
		//it hasn't hurt anything, so I didn't take it out and recreate it as not a fragment.
		public PlaceholderFragment() {}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) 
		{
			View rootView = inflater.inflate(R.layout.fragment_main, container,false);
			return rootView;
		}
	}

	/* v CODE GRAVEYARD v

	public void convert(View view) //fromLatLong skips the first few tests for auto generated numbers
	//replaced by convertCoords()
	{
		//TODO
		//testCoordinates();
		Builder convertDialog=new AlertDialog.Builder(this);
		convertDialog.setTitle("Convert");
		convertDialog.setMessage("Coordinate format recognized as "+coordType+".\n Choose a format to convert to");
		if(coordType.equals("UTM"))
		{
			convertDialog.setPositiveButton(R.string.lonlat, new DialogInterface.OnClickListener()
			{
		        public void onClick(DialogInterface dialog, int which)
		        {
					coordinateET.setText(mgrs.getLatitude().toString().replace("�","")+","+mgrs.getLongitude().toString().replace("�", ""));
		        }
		     });
			convertDialog.setNeutralButton(R.string.MGRS, new DialogInterface.OnClickListener()
			{
		        public void onClick(DialogInterface dialog, int which)
		        {
					coordinateET.setText(mgrs.toString().replace(" ", ""));
		        }
		     });
		}
		if(coordType.equals("MGRS"))
		{
			convertDialog.setPositiveButton(R.string.lonlat, new DialogInterface.OnClickListener()
			{
		        public void onClick(DialogInterface dialog, int which)
		        {
					coordinateET.setText(mgrs.getLatitude().toString().replace("�", "")+","+mgrs.getLongitude().toString().replace("�", ""));
		        }
		     });
			convertDialog.setNeutralButton(R.string.UTM, new DialogInterface.OnClickListener()
			{
		        public void onClick(DialogInterface dialog, int which)
		        {
		        	String hemi=AVKey.NORTH.equals(utm.getHemisphere()) ? "N" : "S";
					coordinateET.setText(utm.getZone()+" "+hemi+" "+utm.getEasting()+" "+utm.getNorthing());
		        }
		     });
		}
		if(coordType.equals("Longitude and Latitude"))
		{
			convertDialog.setPositiveButton(R.string.MGRS, new DialogInterface.OnClickListener(
					) {
		        public void onClick(DialogInterface dialog, int which)
		        {
					coordinateET.setText(mgrs.toString());
		        }
		     });
			convertDialog.setNeutralButton(R.string.UTM, new DialogInterface.OnClickListener()
			{
		        public void onClick(DialogInterface dialog, int which)
		        {
		        	String hemi=AVKey.NORTH.equals(utm.getHemisphere()) ? "N" : "S";
					coordinateET.setText(utm.getZone()+" "+hemi+" "+utm.getEasting()+" "+utm.getNorthing());
		        }
		     });
		}
		if(coordType.equals(""))
		{
			convertDialog.setMessage("Coordinate format not recognized");
		}
		convertDialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
		{
	        public void onClick(DialogInterface dialog, int which)
	        {
	            // do nothing
	        }
	     });
		convertDialog.setIcon(android.R.drawable.ic_dialog_alert).show();
	}

	private String[] testCoordinates()//checks to make sure coordinates are in one of the 3 usable formats RETURNS LAT/LON
	{
		String toTest = coordinateET.getText().toString();
		String[] LatLong=new String[2];
		coordType="";
		if(!toTest.equals(""))
		{
			if(LatLongFormatCheck(toTest))
			{
				coordType="Longitude and Latitude";
				LatLong=toTest.split(",");
				// CODY took out due to Geo-Coord dependency not working
				mgrs=MGRSCoord.fromString(Coordinates.mgrsFromLatLon(Double.parseDouble(LatLong[0]), Double.parseDouble(LatLong[1])));
				mgrs=MGRSCoord.fromString(toTest);
				utm=UTMCoord.fromLatLon(mgrs.getLatitude(), mgrs.getLongitude());
				return LatLong;
			}
			else if(MGRSFormatCheck(toTest))
			{
				//LatLong;
				coordType="MGRS";
				mgrs=MGRSCoord.fromString(toTest);
				utm=UTMCoord.fromLatLon(mgrs.getLatitude(), mgrs.getLongitude());
				LatLong[0]=utm.getLatitude().toString().replace("�","");
				LatLong[1]=utm.getLongitude().toString().replace("�","");
				return LatLong;
			}
			else if(UTMFormatCheck(toTest))
			{
				coordType="UTM";
				String[] utmParts=toTest.split(" ");
				if(utmParts[1].equals("N")){utmParts[1]=AVKey.NORTH;}
				else{utmParts[1]=AVKey.SOUTH;}
				utm=UTMCoord.fromUTM(Integer.parseInt(utmParts[0]), utmParts[1], Double.parseDouble(utmParts[2]), Double.parseDouble(utmParts[3]));
				mgrs=MGRSCoord.fromLatLon(utm.getLatitude(), utm.getLongitude());
				LatLong[0]=utm.getLatitude().toString().replace("�","");
				LatLong[1]=utm.getLongitude().toString().replace("�","");
				return LatLong;
			}
		}
		Toast.makeText(this, "Coordinate format was not recognized.", Toast.LENGTH_SHORT).show(); //pop up message to let user know if their OS is too old
		LatLong[0]="0";
		LatLong[1]="0";
		return LatLong;
	}

	CODY - replaced TryGPS & TryFineGPS with getLocation(View view) method

	public void TryGPS(View view) //attempts to return the current GPS
	{
		if(lookingForGps)
		{
			lookingForGps=false;
			locationManager.removeUpdates(locationListener);
			//Button tryGps=(Button)findViewById(R.id.GPSCALL);
			//tryGps.setText(R.string.GetCoords);
		}
		else
		{
			lookingForGps=true;
			//Button tryGps=(Button)findViewById(R.id.GPSCALL);
			//tryGps.setText(R.string.Cancel);
			locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			Criteria criteria = new Criteria(); //set criteria for location discovery
			//criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setAccuracy(Criteria.ACCURACY_COARSE);
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setCostAllowed(true);
			criteria.setPowerRequirement(Criteria.POWER_LOW);


			String provider = locationManager.getBestProvider(criteria, true);
			if(provider!=null)
			{
				locationManager.getLastKnownLocation(provider);
				locationManager.requestLocationUpdates(provider,30,0,locationListener);
				Toast.makeText(this, "attempting to connect to GPS", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(this, "your GPS is turned off or doesnt exist", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void TryFineGPS(View view) //attempts to return the current GPS
	{
		if(lookingForGps)
		{
			lookingForGps=false;
			locationManager.removeUpdates(locationListener);
			Button tryGps=(Button)findViewById(R.id.FINEGPSCALL);
			tryGps.setText(R.string.GetFineCoords);
		}
		else
		{
			lookingForGps=true;
			Button tryGps=(Button)findViewById(R.id.FINEGPSCALL);
			tryGps.setText(R.string.Cancel);
			locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			Criteria criteria = new Criteria(); //set criteria for location discovery
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setCostAllowed(true);
			criteria.setPowerRequirement(Criteria.POWER_LOW);

			String provider = locationManager.getBestProvider(criteria, true);
			if(provider!=null)
			{
				locationManager.getLastKnownLocation(provider);
				locationManager.requestLocationUpdates(provider,30,0,locationListener);
				Toast.makeText(this, "attempting to connect to GPS", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(this, "your GPS is turned off or doesnt exist", Toast.LENGTH_SHORT).show();
			}
		}
	}*/
}
