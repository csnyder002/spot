package com.IntelligentWaves.xmltest;

/**
 * Created by Cody.Snyder on 8/24/2016.
 */
public class SpotReportObject {
    private String uuid;
    private String name;
    private String date;
    private String time;
    private String dateTaken;
    private String timeTaken;
    private String coordinates;
    private String extrainfo;
    private String imagefilepath;
    private String uploadts;
    private String geometry;
    private Double lat;
    private Double lon;
    private String type;

    public SpotReportObject(String uuid, String name, String date, String time, String dateTaken, String timeTaken, String coordinates, String extrainfo, String imagefilepath, String uploadts, String geometry, String lat, String lon, String type) {
        this.uuid           = uuid;
        this.name           = name;
        this.date           = date;
        this.time           = time;
        this.dateTaken      = dateTaken;
        this.timeTaken      = timeTaken;
        this.coordinates    = coordinates;
        this.extrainfo      = extrainfo;
        this.imagefilepath  = imagefilepath;
        this.uploadts       = uploadts;
        this.geometry       = geometry;
        this.lat            = Double.parseDouble(lat);
        this.lon            = Double.parseDouble(lon);
        this.type           = type;
    }

    public String toString() {
        return uuid + "|" + name + "|" + date + "|" +time + "|" + dateTaken + "|" + timeTaken + "|" + coordinates + "|" + extrainfo + "|" + imagefilepath + "|" + uploadts + "|" + geometry + "|" + lat + "|" + lon + "|" + type;
    }

    public String getUUID(){
        return uuid;
    }

    public double getLat(){
        return lat;
    }

    public double getLon(){
        return lon;
    }
}
