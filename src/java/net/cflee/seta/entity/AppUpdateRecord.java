package net.cflee.seta.entity;

import java.util.Date;

public class AppUpdateRecord implements Comparable<AppUpdateRecord> {

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

    /**
     * Natural order
     *
     * @param that
     * @return -1 if this object should be before that, +1 if this object should be after that, and 0 when they are tied
     */
    @Override
    public int compareTo(AppUpdateRecord that) {
        // mac address ascending
        int compare = getMacAddress().compareTo(that.getMacAddress());
        if (compare != 0) {
            return compare;
        }

        // break ties with timestamp ascending
        return getTimestamp().compareTo(that.getTimestamp());
    }

}
