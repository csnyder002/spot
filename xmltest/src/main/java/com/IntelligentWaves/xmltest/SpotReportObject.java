package com.IntelligentWaves.xmltest;

import java.io.Serializable;

/**
 * Created by Cody.Snyder on 8/24/2016.
 */
public class SpotReportObject implements Serializable{
    private String uuid;
    private String user_uuid;
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

    public SpotReportObject(String uuid, String user_uuid, String coordinates, String time_of_report, String time_observed, String timezone, String synopsis, String full_report, String lat, String lng, String image_file_path, String image_file) {
        this.uuid               = uuid;
        this.user_uuid          = user_uuid;
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
        return uuid+"|"+user_uuid+"|"+coordinates+"|"+time_of_report+"|"+time_observed+"|"+timezone+"|"+synopsis+"|"+full_report+"|"+lat+"|"+lng+"|"+image_file_path+"|"+image_file;
    }

    public String getUUID(){
        return uuid;
    }

    public String getSynopsis() { return synopsis; }

    public double getLat(){
        return Double.parseDouble(lat);
    }

    public double getLon(){
        return Double.parseDouble(lng);
    }

    public String getTime_observed() {
        return time_observed;
    }
}
