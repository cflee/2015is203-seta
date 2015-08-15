package net.cflee.seta.entity;

import java.util.Date;

/**
 * An entity class of a user's app update
 */
public class AppUpdate implements Comparable<AppUpdate> {

    private String macAddress;
    private Date timestamp;
    private int appId;
    private int rowNo;

    /**
     * Construct a appUpdate object with macAddress, timestamp, appId and rowNo = 0
     *
     * @param macAddress
     * @param timestamp
     * @param appId
     */
    public AppUpdate(String macAddress, Date timestamp, int appId) {
        this(macAddress, timestamp, appId, 0);
    }

    /**
     * Construct a appUpdate object with macAddress, timestamp, appId, email and rowNo
     *
     * @param macAddress
     * @param timestamp
     * @param appId
     * @param email
     * @param rowNo
     */
    public AppUpdate(String macAddress, Date timestamp, int appId, int rowNo) {
        this.macAddress = macAddress;
        this.timestamp = timestamp;
        this.appId = appId;
        this.rowNo = rowNo;
    }

    /**
     * Retrieve the macAddress from app update object
     *
     * @return macAddress
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * Set the macAddress from app update object
     *
     * @param macAddress
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    /**
     * Retrieve the timestamp from app update object
     *
     * @return timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Set the timestamp from app update object
     *
     * @param timestamp
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Retrieve the appId from app update object
     *
     * @return appId
     */
    public int getAppId() {
        return appId;
    }

    /**
     * Set the appId from app update object
     *
     * @param appId
     */
    public void setAppId(int appId) {
        this.appId = appId;
    }

    public int getRowNo() {
        return rowNo;
    }

    /**
     * Natural order: mac address ascending
     *
     * @param that LocationUpdate to be compared against
     * @return -1 if this object should be before that, +1 if this object should be after that, and 0 when they are tied
     */
    @Override
    public int compareTo(AppUpdate that) {
        return macAddress.compareTo(that.macAddress);
    }
}
