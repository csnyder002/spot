package com.IntelligentWaves.xmltest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
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

/**
 * Created by Cody.Snyder on 8/12/2016.
 */
public class Tab1 extends Fragment implements View.OnClickListener{

    private static final int SELECT_PHOTO = 100;
    private static final int PHOTO_TAKEN = 200;
    static Uri Image;
    boolean switcher=true;
    boolean WAITFORIT=false;
    boolean lookingForGps=false;
    boolean hasImage=false;
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

    Button LatLongButton;
    Button UTMButton;
    Button MGRSButton;
    Button UploadSelected;
    Button save;

    ImageButton imageButton;
    ImageButton gps;
    ImageButton TimeOfReportIB;
    ImageButton TimeObservedIB;

    EditText FileName;
    EditText Name;
    EditText TimeOfReportET;
    EditText TimeObservedET;
    EditText coordinateET;
    EditText Notes;

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

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        dates = new Input(" ", " "); //define a special instance of the input class that holds an array of months
        dates.setup(); //build that array

        fileName = setFileName();
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        timeStamp = dateFormatGmt.format(new Date());
        converter = new CoordinateConversion();

        Identifier = UUID.randomUUID().toString();

    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v =inflater.inflate(R.layout.fragment_main,container,false);

        timeZoneSpinner = (Spinner) v.findViewById(R.id.TimeZoneSpinner);
        typeSpinner = (Spinner) v.findViewById(R.id.TypeSpinner);
        uploadSpinner = (Spinner) v.findViewById(R.id.UploadSpinner);

        FileName = (EditText) v.findViewById(R.id.FileName);
        Name = (EditText) v.findViewById(R.id.Name);
        TimeOfReportET = (EditText) v.findViewById(R.id.TimeOfReportET);
        TimeObservedET = (EditText) v.findViewById(R.id.TimeObservedET);
        coordinateET = (EditText) v.findViewById(R.id.Coordinates);
        Notes = (EditText) v.findViewById(R.id.Notes);

        LatLongButton = (Button) v.findViewById(R.id.LatLongButton);
        UTMButton = (Button) v.findViewById(R.id.UTMButton);
        MGRSButton = (Button) v.findViewById(R.id.MGRSButton);
        UploadSelected = (Button) v.findViewById(R.id.UploadSelected);
        save = (Button) v.findViewById(R.id.save);

        gps = (ImageButton) v.findViewById(R.id.gps);
        imageButton = (ImageButton) v.findViewById(R.id.imageButton);
        TimeOfReportIB = (ImageButton) v.findViewById(R.id.TimeOfReportIB);
        TimeObservedIB = (ImageButton) v.findViewById(R.id.TimeObservedIB);

        LatLongButton.setOnClickListener(this);
        UTMButton.setOnClickListener(this);
        MGRSButton.setOnClickListener(this);
        UploadSelected.setOnClickListener(this);
        save.setOnClickListener(this);
        gps.setOnClickListener(this);
        imageButton.setOnClickListener(this);
        TimeOfReportIB.setOnClickListener(this);
        TimeObservedIB.setOnClickListener(this);
        TimeOfReportET.setFocusable(false);
        TimeObservedET.setFocusable(false);

        BuildSpinner();

        //BuildSpinner(preferences);
        chooseFillStyle();
        return v;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {

            case R.id.imageButton:
                imageOptions(view);
                break;

            case R.id.TimeOfReportIB:
                dateTimeDialog(view);
                break;

            case R.id.TimeObservedIB:
                dateTimeDialog(view);
                break;

            case R.id.gps:
                getLocation(view);
                break;

            case R.id.LatLongButton:
                convertCoords(view);
                break;

            case R.id.MGRSButton:
                convertCoords(view);
                break;

            case R.id.UTMButton:
                convertCoords(view);
                break;

            case R.id.UploadSelected:
                upload(view);
                break;

            case R.id.save:
                save(view);
                break;

            default:
                break;
        }
    }

    // BEGIN LOCATION METHODS
    public void getLocation(View view) // get's user's gps coordinates
    {
        locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
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
                Toast.makeText(getActivity(), "Attempting to connect to GPS", Toast.LENGTH_SHORT).show();
            } else
            {
                Toast.makeText(getActivity(), "Your GPS is turned off or doesn't exist", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), "Coordinate format was not recognized.", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), "Coordinate format was not recognized.", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), "Coordinate format was not recognized.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "Latitude must be between -90 and 90", Toast.LENGTH_LONG).show();
                        Toast.makeText(getActivity(), "Longitude must be between -180 and 180", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
                catch(NumberFormatException nfe)
                {
                    Toast.makeText(getActivity()," proper format for long lat is 54,123",Toast.LENGTH_LONG).show();
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
        if(match.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean MGRSFormatCheck(String toCheck) // checks to see if the entered coordinate data is in MGRS
    {
        Pattern MGRSPattern=Pattern.compile("^(\\d{1,2})[^0-9IOYZ\\W][^0-9WXYZIO\\W]{2}(\\d{2}|\\d{4}|\\d{6}|\\d{8}|\\d{10})$",Pattern.CASE_INSENSITIVE);
        Matcher match=MGRSPattern.matcher(toCheck);
        if(match.matches()) {
            return true;
        } else {
            return false;
        }
    }

    protected void chooseFillStyle() //determines how to fill out the form, either from acquired data or from loading an xml
    {
        /*Bundle extras=getActivity().getIntent().getExtras();
        if(extras.getBoolean("tut", false))
        {
            runTutorial();
            SharedPreferences.Editor editor=preferences.edit();
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
        {*/
            AutoFill();
        //}
        PreferenceSetup();
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
            SharedPreferences.Editor editor = preferences.edit();
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

    public void BuildSpinner()  //sets up the spinners
    {
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.TimeZone, R.layout.spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeZoneSpinner.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getActivity(), R.array.Type, R.layout.spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter3);

        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(getActivity(), R.array.UploadOptions, R.layout.spinner_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        uploadSpinner.setAdapter(adapter4);

    }

    public void dateTimeDialog(View view) // displays a dialog allowing user to input both date and time
    {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.date_time_dialog);

        Button saveTime = (Button) dialog.findViewById(R.id.saveTime);

        if (view.getId() == R.id.TimeOfReportIB){
            dialog.setTitle("Set Time of Report:");
            saveTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Tab1.set
                    DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker1);
                    int year = datePicker.getYear();
                    int monthTemp = (datePicker.getMonth() + 1);
                    String month = Integer.toString(monthTemp);
                    String day = Integer.toString(datePicker.getDayOfMonth());
                    if (month.length()==1) { month = "0" + month; }
                    if (day.length()==1) { day = "0" + day; }

                    TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePicker1);
                    String hour = timePicker.getCurrentHour().toString();
                    String minute = timePicker.getCurrentMinute().toString();

                    if (minute.length()==1) { minute = "0" + minute; }
                    if (hour.length()==1) { hour = "0" + hour; }


                    String topString = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":00";

                    TimeOfReportET.setText(topString);
                    dialog.dismiss();
                }
            });
        } else {
            dialog.setTitle("Set Time of Observation:");
            saveTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker1);
                    int year = datePicker.getYear();
                    int monthTemp = (datePicker.getMonth() + 1);
                    String month = Integer.toString(monthTemp);
                    String day = Integer.toString(datePicker.getDayOfMonth());
                    if (month.length()==1) { month = "0" + month; }
                    if (day.length()==1) { day = "0" + day; }

                    TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePicker1);
                    String hour = timePicker.getCurrentHour().toString();
                    String minute = timePicker.getCurrentMinute().toString();

                    if (minute.length()==1) { minute = "0" + minute; }
                    if (hour.length()==1) { hour = "0" + hour; }

                    String topString = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":00";

                    TimeObservedET.setText(topString);
                    dialog.dismiss();
                }
            });
        }

        dialog.show();
    }

    public void save(View view) //saves a xml document
    {
        List<Input> I=buildXML(); //initialize list

        SharedPreferences.Editor editor = preferences.edit();
        int filecounter=preferences.getInt("filecount",0);
        filecounter++;

        editor.putInt("filecount",filecounter);
        editor.commit();

        XMLsaver xml=new XMLsaver(I,Environment.getExternalStorageDirectory()+"/.spot/"+fileName+"__"+Identifier+".xml");
        xml.write();
        Toast.makeText(getActivity(), "File save successful", Toast.LENGTH_LONG).show();

        // reload the layout
        Intent splashScreen = new Intent(getActivity(), SplashActivity.class);
        getActivity().startActivity(splashScreen);
    }

    public void imageOptions(View view) //opens a dialog with options for your currently selected image
    {
        AlertDialog.Builder ImageDialog = new AlertDialog.Builder(getActivity());
        ImageDialog.setTitle("Image Options");
        out.println(imageButton.getDrawable());
        if(!hasImage)
        {
            ImageDialog.setPositiveButton(R.string.addImage, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    AlertDialog.Builder tutorialDialog=new AlertDialog.Builder(getActivity());
                    tutorialDialog.setTitle("Upload");
                    tutorialDialog.setMessage("Choose image type");
                    tutorialDialog.setPositiveButton(R.string.camera, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null)
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
                    AlertDialog.Builder tutorialDialog=new AlertDialog.Builder(getActivity());
                    tutorialDialog.setTitle("Upload");
                    tutorialDialog.setMessage("Choose image type");
                    tutorialDialog.setPositiveButton(R.string.camera, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null)
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
                    ((ImageView) getView().findViewById(R.id.imageButton)).setImageBitmap(baseImage);
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

    public void upload(View view) //sends file info to the DataOut class to be transferred
    {
        String uploadType = uploadSpinner.getSelectedItem().toString();
        switch (uploadType) {
            case "Automatic":
                autoUpload();
                break;
            case "SFTP":
                sftpUpload();
                break;
            case "SMS":
                smsUpload();
                break;
            case "HTTPS":
                httpsUpload();
                break;
        }
    }

    public void autoUpload() // chooses upload type (sms/https/sftp) based on phone's signal
    {
        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            Toast.makeText(getActivity(), "WIFI detected: Uploading via SFTP.", Toast.LENGTH_SHORT).show();
            sftpUpload();
            return;
        }

        String networkClass = getNetworkClass(getActivity());
        if (networkClass.equals("3G") || networkClass.equals("4G")) {
            Toast.makeText(getActivity(), networkClass + " signal detected: Uploading via HTTPS.", Toast.LENGTH_SHORT).show();
            httpsUpload();
            return;
        }

        // sms upload for all other cases
        Toast.makeText(getActivity(), "Uploading via SMS.", Toast.LENGTH_SHORT).show();
        smsUpload();
    }

    public void sftpUpload() // uploads via sftp
    {
        if(!WAITFORIT)
        {
            WAITFORIT=true;
            secureTransfer = new DataOut(this, true);
            SharedPreferences.Editor editor = preferences.edit();

            int filecounter = preferences.getInt("filecount",0) + 1;
            editor.putInt("filecount", filecounter);
            editor.commit();

            XMLsaver xml = new XMLsaver(buildXML(),Environment.getExternalStorageDirectory()+"/.spot/"+fileName+"__"+Identifier+".xml");
            xml.write();
            tempFile = new File(Environment.getExternalStorageDirectory()+"/.spot/"+fileName+"__"+Identifier+".xml");
            String filePath=Environment.getExternalStorageDirectory()+"/.spot/"+fileName+"__"+Identifier+".xml";

            secureTransfer.execute(filePath,imageFilePath);
        }
    }

    public void httpsUpload() // uploads via https
    {
        Toast.makeText(getActivity(), "Beginning HTTPS upload...", Toast.LENGTH_SHORT).show();
        if(!WAITFORIT)
        {
            WAITFORIT=true;

            SharedPreferences.Editor editor = preferences.edit();
            int filecounter = preferences.getInt("filecount",0);
            filecounter++;
            editor.putInt("filecount", filecounter);
            editor.commit();

            DataOutHttp helper = new DataOutHttp(getActivity(), preferences.getString("Host", ""), getParams());
            helper.execute();
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

        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(SMS_SENT), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(SMS_DELIVERED), 0);

        ArrayList<String> smsBodyParts = smsManager.divideMessage(smsBody);
        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();

        for (int i = 0; i < smsBodyParts.size(); i++) {
            sentPendingIntents.add(sentPendingIntent);
            deliveredPendingIntents.add(deliveredPendingIntent);
        }

        // For when the SMS has been sent
        getActivity().registerReceiver(new BroadcastReceiver() {
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
        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getActivity().getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getActivity().getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_DELIVERED));

        // Send a text based SMS
        smsManager.sendMultipartTextMessage(phoneNumber, null, smsBodyParts, sentPendingIntents, deliveredPendingIntents);

        // NEED TO MAKE SURE THIS DOESNT START INTENT IF SMS IS UNSUCCESSFUL
        Intent main = new Intent(getActivity(), MenuScreenActivity.class);
        startActivity(main);

    }

    private String buildSMS() // creates the sms string based on input fields
    {
        String body = "";

        body += FileName.getText().toString() + "^";
        body += Name.getText().toString() + "^";
        body += TimeOfReportET.getText().toString() + "^";
        body += TimeObservedET.getText().toString() + "^";
        body += coordinateET.getText().toString() + "^";
        body += Notes.getText().toString() + "^";
        body += typeSpinner.getSelectedItem().toString() + "^";
        body += Environment.getExternalStorageDirectory()+"/.spot/"+fileName+"__"+Identifier+".xml";

        return body;
    }

    public String getNetworkClass(Context context) // gets signal type
    {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            default:
                return "Unknown";
        }
    }

    public void LoadFromXml(String file)  //loops through the xml data and puts it in the corresponding forms
    {
        XMLreader reader=new XMLreader(getActivity());
        List<Input> XMLdata =reader.readXML(file);
        FileName.setText(fileName);
        for(int i=0;i<XMLdata.size();i++)
        {
            switch(XMLdata.get(i).code)
            {

                case "UUID":
                    Identifier=XMLdata.get(i).data;
                    break;
                case "Name":
                    Name.setText(XMLdata.get(i).data);
                    break;
                case "Date":
                    TimeOfReportET.setText(XMLdata.get(i).data);
                    break;
                case "Time":
                    TimeOfReportET.setText(TimeOfReportET.getText().toString() + " " + XMLdata.get(i).data);
                    break;
                case "DateTaken":
                    TimeObservedET.setText(XMLdata.get(i).data);
                    break;
                case "TimeTaken":
                    TimeObservedET.setText(TimeObservedET.getText().toString() + " " + XMLdata.get(i).data);
                    break;
                case "Coordinates":
                    coordinateET.setText(XMLdata.get(i).data);
                    break;
                case "ExtraInformation":
                    Notes.setText(XMLdata.get(i).data);
                    break;
                case "ReportTimeStamp":
                    timeStamp=XMLdata.get(i).data;
                    break;
                case "ImageFilePath":
                    File f=new File(XMLdata.get(i).data);
                    if(f.exists() && XMLdata.get(i).data!=null &&XMLdata.get(i).data!="")
                    {
                        imageButton.setImageBitmap(Shrink(BitmapFactory.decodeFile(XMLdata.get(i).data),200,getActivity()));
                        imageFilePath=XMLdata.get(i).data;
                        hasImage=true;
                    }
                    else
                    {
                        imageButton.setImageBitmap(Shrink(BitmapFactory.decodeResource(getResources(), R.drawable.spot),200,getActivity()));
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
        Bundle extras= getActivity().getIntent().getExtras(); //get information passed in with intent
        if(extras!=null) //if there is any
        {
            if(extras.getString("Lat")!=null && !extras.getString("Lat").equals("")) //check to make sure there was GPS data
            {
                out.println("LATTITUDE="+extras.getString("Lat"));
                coordinateET.setText(extras.getString("Lat")+","+extras.getString("Long")); //set Edit Text form to latitude and longitude
            }

            if(extras.getString("dateTaken")!=null && !extras.getString("dateTaken").equals("")) //check to make sure there was date data
            {
                String holder=extras.getString("dateTaken");
                String[] breaker = holder.split(":"); //splits string so we can access just the month data
                if(breaker.length>=3)
                {
                    //breaker[1]=dates.getDate(Integer.parseInt(breaker[1])); //uses month data to get 3 letter month code (ex:Dec, Mar)
                    TimeObservedET.setText((breaker[0]+"-"+breaker[1]+"-"+breaker[2])); //set Edit Text from for date taken
                }
                TimeObservedET.setText(extras.getString("timeTaken") + " " + TimeObservedET.getText().toString()); //set Edit Text from for time taken
            }

            if(extras.getString("imagepath")!=null)
            {
                //try
                //{
                baseImage=BitmapFactory.decodeFile((String)extras.getString("imagepath"));//MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),(Uri)extras.get("image"));
                imageButton.setImageBitmap(Shrink(baseImage,200,getActivity()));
                imageFilePath=(String)extras.getString("imagepath");
                hasImage=true;
                //SMSButton.setEnabled(false);
                //}
                //catch(IOException ioe)
                //{

                //}
            }
        }

        SimpleDateFormat ft = new SimpleDateFormat ("HH:mm:ss", Locale.getDefault()); //create format for Time
        java.util.Date d = new Date( ); // Create Date object
        String dateTime = ft.format(d) + " ";

        ft = new SimpleDateFormat ("yyyy-MM-dd",Locale.getDefault()); //set format for date
        String[] breaker = ft.format(d).split("-",3); //splits string to get month data
        dateTime = (breaker[0]+"-"+breaker[1]+"-"+breaker[2]) + " " + dateTime;
        TimeOfReportET.setText(dateTime); //set Edit Text from for date taken

        //set Name based on preference data
        Name.setText(preferences.getString("Name", ""));

        FileName.setText(fileName);

        if(coordinateET.getText().toString()=="");
        {
            out.println((coordinateET.getText().toString()));
            //findViewById(R.id.GPSCALL).performClick();
        }
        if(coordinateET.getText().toString()==",")
        {
            out.println((coordinateET.getText().toString()));
            //findViewById(R.id.GPSCALL).performClick();
        }

    }

    private List<Input> buildXML() //creates the xml file to be stored or transferred
    {
        List<Input> I = new ArrayList<Input>(); //initialize list
        String coordInput = coordinateET.getText().toString();
        // Get Time of report date/time
        String[] torTime = TimeOfReportET.getText().toString().split(" ");
        String[] toTime = TimeObservedET.getText().toString().split(" ");
        System.out.println("!!! "+TimeObservedET.getText().toString());
        System.out.println("!!! "+ toTime[0]);
        //System.out.println("!!! "+ yo);
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

        if (torTime.length == 1) {
            torTime = new String[2];
            torTime[0] = "";
            torTime[1] = "";
        }
        if (toTime.length == 1) {
            toTime = new String[2];
            toTime[0] = "";
            toTime[1] = "";
        }


        if (flag) {
            double[] latLongDoubles = convertToLatLon(coordInput);
            I.add(new Input("lat",latLongDoubles[0]+""));
            I.add(new Input("lon",latLongDoubles[1]+""));
        }

        I.add(new Input("UUID",Identifier));
        I.add(new Input("Name",Name.getText().toString())); //add input object to list based on temp field
        I.add(new Input("Date",torTime[0])); //add object
        I.add(new Input("Time",torTime[1]));
        I.add(new Input("DateTaken",toTime[0]));
        I.add(new Input("TimeTaken",toTime[1]));
        I.add(new Input("ExtraInformation",Notes.getText().toString()));
        I.add(new Input("ReportTimeStamp",timeStamp));

        if(obTimeStampDate!="" && obTimeStampTime!="") {
            obTimeStamp=obTimeStampDate+" "+obTimeStampTime;
        } else {
            obTimeStamp=ObservedTimeStampBuilder();
        }

        I.add(new Input("Type",typeSpinner.getSelectedItem().toString()));
        I.add(new Input("ImageFilePath",imageFilePath));

        fileName = FileName.getText().toString();

        while(fileName.contains("__")) {
            fileName=fileName.replace("__", "_");
        }

        I.add(new Input("FilePath",Environment.getExternalStorageDirectory()+"/.spot/"+fileName+"__"+Identifier+".xml"));

        return I;
    }

    public ArrayList<NameValuePair> getParams() // params for https post
    {
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("uuid", GetUUID()));
        postParameters.add(new BasicNameValuePair("name", Name.getText().toString()));

        String coordInput = coordinateET.getText().toString();
        String[] torTime = TimeOfReportET.getText().toString().split(" ");
        String[] toTime = TimeObservedET.getText().toString().split(" ");
        Boolean flag = true;

        if (LatLongFormatCheck(coordInput)) {
            postParameters.add(new BasicNameValuePair("coordinates", ("LAT/LONG:" + coordInput)));
        } else if (UTMFormatCheck(coordInput)) {
            postParameters.add(new BasicNameValuePair("coordinates", ("UTM:" + coordInput)));
        } else if (MGRSFormatCheck(coordInput)) {
            postParameters.add(new BasicNameValuePair("coordinates", ("MGRS:" + coordInput)));
        } else {
            postParameters.add(new BasicNameValuePair("coordinates", ("UNRECOGNIZED:" + coordInput)));
            flag = false;
        }

        if (flag) {
            double[] latLongDoubles = convertToLatLon(coordInput);
            postParameters.add(new BasicNameValuePair("lat", (latLongDoubles[0] + "")));
            postParameters.add(new BasicNameValuePair("lon", (latLongDoubles[1] + "")));
        }

        postParameters.add(new BasicNameValuePair("Date", torTime[0]));
        postParameters.add(new BasicNameValuePair("Time", torTime[1]));
        postParameters.add(new BasicNameValuePair("DateTaken", toTime[0]));
        postParameters.add(new BasicNameValuePair("TimeTaken", toTime[1]));
        postParameters.add(new BasicNameValuePair("ExtraInformation", Notes.getText().toString()));
        postParameters.add(new BasicNameValuePair("ReportTimeStamp", timeStamp));

        if (obTimeStampDate != "" && obTimeStampTime != "") {
            obTimeStamp = obTimeStampDate + " " + obTimeStampTime;
        } else {
            obTimeStamp = ObservedTimeStampBuilder();
        }

        postParameters.add(new BasicNameValuePair("Type", typeSpinner.getSelectedItem().toString()));
        postParameters.add(new BasicNameValuePair("ImageFilePath", imageFilePath));

        if (!imageFilePath.equals("null")) {
            Bitmap bitmap = ((BitmapDrawable)imageButton.getDrawable()).getBitmap();
            String encodedImage = encodeToBase64(bitmap);
            System.out.println("!!! " + encodedImage + " !!!");
            postParameters.add(new BasicNameValuePair("ImageFile", encodedImage));
        }

        fileName = FileName.getText().toString();

        while (fileName.contains("__")) {
            fileName = fileName.replace("__", "_");
        }

        postParameters.add(new BasicNameValuePair("FilePath", Environment.getExternalStorageDirectory() + "/.spot/" + fileName + "__" + Identifier + ".xml"));
        postParameters.add(new BasicNameValuePair("localizedPath", fileName + "__" + Identifier + ".xml"));

        return postParameters;
    }

    public static String encodeToBase64(Bitmap image) // encodes image to a string to be transmitted through https post
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public void transferComplete(Boolean success) //returns a message based on the success/failure of the transfer
    {
        WAITFORIT=false;
        if(success)
        {
            Toast.makeText(getActivity(), "File upload successful", Toast.LENGTH_LONG).show();
            tempFile.delete();
            Intent splashScreen = new Intent(getActivity(), SplashActivity.class); //go back to the menu
            startActivity(splashScreen);
        }
        else
        {

            Toast.makeText(getActivity(), "File upload failed. File has been saved", Toast.LENGTH_LONG).show();
            //getView().findViewById(R.id.back).performClick();
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

    public String GetUUID() //creates a UUID
    {
        return UUID.randomUUID().toString();
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
        String[] timeObserved = TimeObservedET.getText().toString().split(" ");
        String TimePortion= timeObserved[0];
        String DatePortion= timeObserved[0];
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

    public Bitmap Shrink(Bitmap img, int Height,Context context)//shrinks bit
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
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) //hijack results
    {
        // super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        int inSample = 2;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = inSample;

        switch(requestCode)
        {
            case SELECT_PHOTO: //if the result is form selecting a photo
                if(resultCode == getActivity().RESULT_OK)
                {
                    try
                    {
                        Uri selectedImage = imageReturnedIntent.getData(); //collect selected photo
                        baseImage = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(),selectedImage); //convert to bitmap
                        imageButton.setImageBitmap(Shrink(baseImage,200,getActivity()));
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
                if(resultCode == getActivity().RESULT_OK)
                {
                    try
                    {
                        baseImage = MediaStore.Images.Media.getBitmap( getActivity().getApplicationContext().getContentResolver(),Image);
                        imageButton.setImageBitmap(Shrink(baseImage,200,getActivity()));
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
            coordinateET.setText(Lat+","+Long);
        }

        if(exif.getAttribute(ExifInterface.TAG_DATETIME)!=null)
        {
            String[] spliter= exif.getAttribute(ExifInterface.TAG_DATETIME).split(" ",2); //separate the time and date information
            String timeObserved = spliter[0].replace(":", "-") + " " + spliter[1];
            TimeObservedET.setText(timeObserved);
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
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
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
}
