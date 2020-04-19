package com.example.OnlineRescueSystem.Model;

public class LocationInfo {

    private String lat1;
    private String log1;

    public LocationInfo(String lat, String log) {
        this.lat1 = lat;
        this.log1= log;
    }

    public LocationInfo() {
    }

    public String getLat() {
        return lat1;
    }

    public void setLat(String lat) {
        this.lat1 = lat;
    }

    public String getLog() {
        return log1;
    }

    public void setLog(String log) {
        this.log1 = log;
    }

}
