package net.cflee.seta.entity;

import java.util.Date;

/**
 * An entity class of a user's location update
 */
public class LocationUpdate implements Comparable<LocationUpdate> {

    private String macAddress;
    private Date timestamp;
    private int locationId;
    private int rowNo;

    /**
     * Construct a locationUpdate object with macAddress, timestamp, locationId and rowNo = 0
     *
     * @param macAddress
     * @param timestamp
     * @param locationId
     */
    public LocationUpdate(String macAddress, Date timestamp, int locationId) {
        this(macAddress, timestamp, locationId, 0);
    }

    /**
     * Construct a locationUpdate object with macAddress, timestamp, locationId, email and rowNo
     *
     * @param macAddress
     * @param timestamp
     * @param locationId
     * @param rowNo
     */
    public LocationUpdate(String macAddress, Date timestamp, int locationId, int rowNo) {
        this.macAddress = macAddress;
        this.timestamp = timestamp;
        this.locationId = locationId;
        this.rowNo = rowNo;
    }

    /**
     * Retrieve the macAddress from location update object
     *
     * @return macAddress
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * Set the macAddress from location update object
     *
     * @param macAddress
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    /**
     * Retrieve the timestamp from location update object
     *
     * @return timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Set the timestamp from location update object
     *
     * @param timestamp
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Retrieve the locationId from location update object
     *
     * @return locationId
     */
    public int getLocationId() {
        return locationId;
    }

    /**
     * Set the locationId from location update object
     *
     * @param locationId
     */
    public void setLocationId(int locationId) {
        this.locationId = locationId;
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
    public int compareTo(LocationUpdate that) {
        return macAddress.compareTo(that.macAddress);
    }
}
