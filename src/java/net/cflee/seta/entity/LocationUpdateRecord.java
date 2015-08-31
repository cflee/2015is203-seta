
package net.cflee.seta.entity;

import java.util.Date;

public class LocationUpdateRecord {

    private Date timestamp;
    private String macAddress;
    private String semanticPlace;
    private String email;
    private int duration;

    public LocationUpdateRecord(Date timestamp, String macAddress, String semanticPlace, String email) {
        this.timestamp = timestamp;
        this.macAddress = macAddress;
        this.semanticPlace = semanticPlace;
        this.email = email;
        this.duration = 300; // seconds
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getSemanticPlace() {
        return semanticPlace;
    }

    public String getEmail() {
        return email;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

}
