package com.example.OnlineRescueSystem.Model;

public class Callinfo {

    private String lat;
    private String log;
    private String callType;
    private String date;


    public Callinfo() {
    }

    public Callinfo(String lat, String log, String callType, String date) {
        this.lat = lat;
        this.log = log;
        this.callType = callType;
        this.date = date;
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

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
