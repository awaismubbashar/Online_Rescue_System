package com.example.OnlineRescueSystem.Model;

public class Callinfo {

    private String lat;
    private String log;
    private String callType;
    private String hour;
    private String minute;
    private String second;

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public Callinfo() {
    }

    public Callinfo(String lat, String log, String callType, String hour, String minute, String second) {
        this.lat = lat;
        this.log = log;
        this.callType = callType;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public Callinfo(String lat, String log, String callType) {
        this.lat = lat;
        this.log = log;
        this.callType = callType;
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


}
