package com.IntelligentWaves.xmltest;

import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class update_report extends ActionBarActivity implements View.OnClickListener {
    SpotReportObject spotReport;
    String[] spotInfo;

    Toolbar toolbar;

    TextView name_textView;
    TextView to_textView;
    TextView tor_textView;
    TextView coords_textView;
    TextView synopsis_textView;
    TextView fullReport_textView;

    boolean tor_changed = false;
    boolean to_changed = false;
    boolean coords_changed = false;
    boolean notes_changed = false;

    LocationManager locationManager;
    CoordinateConversion converter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_report);
        spotReport = (SpotReportObject) getIntent().getSerializableExtra("spotReport");
        converter = new CoordinateConversion();
        setupViews();

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
        }
    }

    private void setupViews()
    {
        setupToolbar();

        name_textView       = (TextView) findViewById(R.id.name_textView);
        to_textView         = (TextView) findViewById(R.id.to_textView);
        tor_textView        = (TextView) findViewById(R.id.tor_textView);
        coords_textView     = (TextView) findViewById(R.id.coords_textView);
        synopsis_textView   = (TextView) findViewById(R.id.synopsis_textView);
        fullReport_textView = (TextView) findViewById(R.id.fullReport_textView);

        autoFill();
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.titleTextColor));
        setSupportActionBar(toolbar);
    }

    private void autoFill()
    {
        spotInfo = spotReport.toString().split("\\|");

        name_textView.setText(spotInfo[1]);
        to_textView.setText(spotInfo[4]);
        tor_textView.setText(spotInfo[3]);
        coords_textView.setText(spotInfo[2]);
        synopsis_textView.setText(spotInfo[6]);
        fullReport_textView.setText(spotInfo[7]);
    }

    /*public void dateTimeDialog(View view) // displays a dialog allowing user to input both date and time
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.date_time_dialog);

        Button saveTime = (Button) dialog.findViewById(R.id.saveTime);

        if (view.getId() == R.id.editTOR_IB){
            dialog.setTitle("Set Time of Report:");
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
                    if (!topString.equals(spotReport.getLat()+","+spotReport.getLon())){
                        tor_textView.setTextColor(getColor(R.color.changedText));
                    } else {
                        tor_textView.setTextColor(getColor(R.color.textColor));
                    }
                    tor_textView.setText(topString);
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
                    if (!topString.equals(spotReport.getLat()+","+spotReport.getLon())){
                        to_textView.setTextColor(getColor(R.color.changedText));
                    } else {
                        to_textView.setTextColor(getColor(R.color.textColor));
                    }
                    to_textView.setText(topString);
                    dialog.dismiss();
                }
            });
        }

        dialog.show();
    }

    public void displayCoordsDialog(View view) // displays a dialog allowing user to input both date and time
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.gps_dialog);

        final EditText coords_EditText = (EditText) dialog.findViewById(R.id.coords_editText);
        ImageButton gps_IB = (ImageButton) dialog.findViewById(R.id.gps_IB);
        Button LatLongButton = (Button) dialog.findViewById(R.id.LatLongButton);
        Button MGRSButton = (Button) dialog.findViewById(R.id.MGRSButton);
        Button UTMButton = (Button) dialog.findViewById(R.id.UTMButton);
        Button submit_change = (Button) dialog.findViewById(R.id.submit_change);

        dialog.setTitle("Edit Coordinates:");

        gps_IB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coords_EditText.setText(getLocation());
            }
        });

        LatLongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coords_EditText.setText(convertCoords(v, coords_EditText.getText().toString()));
            }
        });

        MGRSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coords_EditText.setText(convertCoords(v, coords_EditText.getText().toString()));
            }
        });

        UTMButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coords_EditText.setText(convertCoords(v, coords_EditText.getText().toString()));
            }
        });

        submit_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!coords_EditText.getText().toString().equals(spotInfo[11]+","+spotInfo[12])) {
                    coords_textView.setTextColor(getColor(R.color.changedText));
                } else {
                    coords_textView.setTextColor(getColor(R.color.textColor));
                }
                coords_textView.setText(coords_EditText.getText());
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    public void editTextDialog(View view) // displays a dialog allowing user to change text field
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.edit_text_dialog);

        TextView temp = null;
        final EditText editText = (EditText) dialog.findViewById(R.id.editText);
        Button submitChange = (Button) dialog.findViewById(R.id.submitChange);

        switch(view.getId()) {
            case R.id.editSynopsis_IB:
                temp = synopsis_textView;
                editText.setText(synopsis_textView.getText());
                break;
            case R.id.editFullReport_IB:
                temp = fullReport_textView;
                editText.setText(fullReport_textView.getText());
                break;
        }

        final TextView outText = temp;

        submitChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newText = editText.getText().toString();

                outText.setText(newText);
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    public void submitChanges(View view)
    {

    }


    // convert
    public String convertCoords(View view, String coords) // converts coords on the fly for user
    {

        switch(view.getId()) { // find which button was clicked
            case R.id.LatLongButton: // convert to lat/long

                double[] latLongDoubles = convertToLatLon(coords);
                if (latLongDoubles!=null)
                    return latLongDoubles[0]+","+latLongDoubles[1];
                break;

            case R.id.MGRSButton: // convert to MGRS

                String mgrsCoords = convertToMgrs(coords);
                if (mgrsCoords != null)
                    return mgrsCoords;
                break;

            case R.id.UTMButton: // convert to UTM

                String utmCoords = convertToUtm(coords);
                if (utmCoords != null)
                    return utmCoords;
                break;

        }
        return null;
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

    //location
    public String getLocation() // get's user's gps coordinates
    {
        locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        double lat,lon;

        try {
            // try to get
            lat = location.getLatitude ();
            lon = location.getLongitude ();

            //double[] answer = {lat,lon};

            return lat+","+lon;
        }
        catch (NullPointerException e) {
            // if the phone hasn't already cached user's location, get it
            if (bestProvider != null) {
                Location loc = getLastKnownLocation();
                return loc.getLatitude()+","+loc.getLongitude();
            } else
            {
                Toast.makeText(this, "Your GPS is turned off or doesn't exist", Toast.LENGTH_SHORT).show();
                return "";
            }
        }
    }

    private Location getLastKnownLocation()
    {
        locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;



        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }*/

}
