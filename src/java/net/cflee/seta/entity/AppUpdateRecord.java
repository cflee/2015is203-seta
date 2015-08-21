package net.cflee.seta.entity;

import java.util.Date;

public class AppUpdateRecord {

    private Date timestamp;
    private String macAddress;
    private String userName;
    private char userGender;
    private String userSchool;
    private int userYear;
    private String userEmail;
    private int appId;
    private String appName;
    private String appCategory;
    private int duration; // seconds

    public AppUpdateRecord(Date timestamp, String macAddress, String userName, char userGender, String userSchool,
            int userYear, String userEmail, int appId, String appName, String appCategory) {
        this.timestamp = timestamp;
        this.macAddress = macAddress;
        this.userName = userName;
        this.userGender = userGender;
        this.userSchool = userSchool;
        this.userYear = userYear;
        this.userEmail = userEmail;
        this.appId = appId;
        this.appName = appName;
        this.appCategory = appCategory;
        this.duration = 10;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getUserName() {
        return userName;
    }

    public char getUserGender() {
        return userGender;
    }

    public String getUserSchool() {
        return userSchool;
    }

    public int getUserYear() {
        return userYear;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public int getAppId() {
        return appId;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppCategory() {
        return appCategory;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

}
