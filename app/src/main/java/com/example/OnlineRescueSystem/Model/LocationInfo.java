package com.example.OnlineRescueSystem.Model;

public class LocationInfo {

    private String lat;
    private String log;
    private String driverType;

    public LocationInfo(String lat, String log) {
        this.lat = lat;
        this.log= log;
    }

    public LocationInfo(String lat, String log, String driverType) {
        this.lat = lat;
        this.log = log;
        this.driverType = driverType;
    }

    public String getDriverType() {
        return driverType;
    }

    public void setDriverType(String driverType) {
        this.driverType = driverType;
    }

    public LocationInfo() {
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

}
