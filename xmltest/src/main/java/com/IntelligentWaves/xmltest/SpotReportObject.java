package com.IntelligentWaves.xmltest;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Cody.Snyder on 8/24/2016.
 */
public class SpotReportObject implements Serializable{
    private String uuid;
    private String phone;
    private String name;
    private String coordinates;
    private String time_of_report;
    private String time_observed;
    private String timezone;
    private String synopsis;
    private String full_report;
    private String lat;
    private String lng;
    private String image_file_path;
    private String image_file;
    private static final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public SpotReportObject(String uuid, String phone, String name, String coordinates, String time_of_report, String time_observed, String timezone, String synopsis, String full_report, String lat, String lng, String image_file_path, String image_file) {
        this.uuid               = uuid;
        this.phone              = phone;
        this.name               = name;
        this.coordinates        = coordinates;
        this.time_of_report     = time_of_report;
        this.time_observed      = time_observed;
        this.timezone           = timezone;
        this.synopsis           = synopsis;
        this.full_report        = full_report;
        this.lat                = lat;
        this.lng                = lng;
        this.image_file_path    = image_file_path;
        this.image_file         = image_file;
    }

    public String toString() {
        return uuid+"|"+phone+"|"+name+"|"+coordinates+"|"+time_of_report+"|"+time_observed+"|"+timezone+"|"+synopsis+"|"+full_report+"|"+lat+"|"+lng+"|"+image_file_path+"|"+image_file;
    }

    public String getUUID(){
        return uuid;
    }

    public String getSynopsis() { return synopsis; }

    public String getFullReport() { return full_report; }

    public double getLat(){
        return Double.parseDouble(lat);
    }

    public double getLon(){
        return Double.parseDouble(lng);
    }

    public String getTime_observed() {
        return time_observed;
    }

    public String getNiceTime() {
        long unixSeconds = Long.parseLong(getTime_observed());
        Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // the format of your date
        String formattedDate = sdf.format(date);
        return formattedDate;
    }
}
