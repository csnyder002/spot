package com.IntelligentWaves.xmltest;

import android.util.Log;
import android.view.View;

import com.berico.coords.Coordinates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Cody.Snyder on 9/25/2016.
 */
public class CoordinateCombinator {
    private final static String TAG = "CoordinateCombinator";
    public static String convertCoords(View view, String coords) // converts coords on the fly for user
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

    public static double[] convertToLatLon(String coords) // takes any coords and returns lat/lon conversion, null if unrecognized format
    {
        if (LatLongFormatCheck(coords)) {
            // lat/lon -> lat/lon
            return splitLatLon(coords);
        } else if (UTMFormatCheck(coords)) {
            // UTM -> lat/lon
            CoordinateConversion converter = new CoordinateConversion();
            return converter.utm2LatLon(coords);
        } else if (MGRSFormatCheck(coords)) {
            // mgrs -> lat/lon
            return Coordinates.latLonFromMgrs(coords);
        } else {
            // unreognized format
            Log.d(TAG, "Coordinate format was not recognized.");
            return null;
        }

    }

    public static String convertToMgrs(String coords) // takes any coords and returns mgrs conversion, null if unrecognized format
    {
        if (LatLongFormatCheck(coords)) {
            // lat/lon -> mgrs
            double[] coordHolder = splitLatLon(coords);
            String mgrsCoords = Coordinates.mgrsFromLatLon(coordHolder[0], coordHolder[1]);
            mgrsCoords = mgrsCoords.replace(" ","");
            return mgrsCoords;
        } else if (UTMFormatCheck(coords)) {
            // utm -> mgrs
            CoordinateConversion converter = new CoordinateConversion();
            double[] coordHolder = converter.utm2LatLon(coords);
            String mgrsCoords = Coordinates.mgrsFromLatLon(coordHolder[0],coordHolder[1]);
            mgrsCoords = mgrsCoords.replace(" ", "");
            return mgrsCoords;
        } else if (MGRSFormatCheck(coords)) {
            // mgrs -> mgrs
            return null;
        } else {
            // unreognized format
            Log.d(TAG, "Coordinate format was not recognized.");
            return null;
        }
    }

    public static String convertToUtm(String coords) // takes any coords and returns utm conversion, null if unrecognized format
    {
        if (LatLongFormatCheck(coords)) {
            // lat/lon -> utm
            CoordinateConversion converter = new CoordinateConversion();
            double[] latLongDoubles = splitLatLon(coords);
            return converter.latLon2UTM(latLongDoubles[0],latLongDoubles[1]);
        } else if (UTMFormatCheck(coords)) {
            // UTM -> utm
            return null;
        } else if (MGRSFormatCheck(coords)) {
            // mgrs -> utm
            CoordinateConversion converter = new CoordinateConversion();
            double[] latLongDoubles = Coordinates.latLonFromMgrs(coords);
            return converter.latLon2UTM(latLongDoubles[0],latLongDoubles[1]);
        } else {
            // unreognized format
            Log.d(TAG, "Coordinate format was not recognized.");
            return null;
        }
    }

    public static double[] splitLatLon(String str) // takes lat/lon string and converts it to an array of doubles
    {
        String[] tempArr = str.split(",");
        double[] ans = {Double.parseDouble(tempArr[0]), Double.parseDouble(tempArr[1])};
        return ans;
    }

    public static boolean isValidCoord(String toCheck)
    {
        if (LatLongFormatCheck(toCheck)) {
            return true;
        } else if (UTMFormatCheck(toCheck)) {
            return true;
        } else if (MGRSFormatCheck(toCheck)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean LatLongFormatCheck(String toCheck)//checks to see if the entered coordinate data is in LatLong
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
                        Log.d(TAG, "Latitude must be between -90 and 90");
                        Log.d(TAG, "Longitude must be between -180 and 180");
                        return false;
                    }
                }
                catch(NumberFormatException nfe)
                {
                    Log.d(TAG, " proper format for long lat is 54,123");
                    return false;
                }
            }
        }
        return false;
    }

    public static boolean UTMFormatCheck(String toCheck) //checks to see if the entered coordinate data is in UTM
    {
        Pattern UTMPattern=Pattern.compile("^([1-9]|[0-5]\\d|60) [^\\d\\WIO](( \\d{1,7}(\\.\\d{1,})?){2})$",Pattern.CASE_INSENSITIVE);
        Matcher match=UTMPattern.matcher(toCheck);
        if(match.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean MGRSFormatCheck(String toCheck) // checks to see if the entered coordinate data is in MGRS
    {
        Pattern MGRSPattern=Pattern.compile("^(\\d{1,2})[^0-9IOYZ\\W][^0-9WXYZIO\\W]{2}(\\d{2}|\\d{4}|\\d{6}|\\d{8}|\\d{10})$",Pattern.CASE_INSENSITIVE);
        Matcher match=MGRSPattern.matcher(toCheck);
        if(match.matches()) {
            return true;
        } else {
            return false;
        }
    }
}
