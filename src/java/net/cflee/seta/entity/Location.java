package net.cflee.seta.entity;

/**
 * An entity class of location with locationId and semantic place
 */
public class Location {

    private int locationId;
    private String semanticPlace;

    /**
     * Contruct a location object with locationId and semantic place
     *
     * @param locationId
     * @param semanticPlace
     */
    public Location(int locationId, String semanticPlace) {
        this.locationId = locationId;
        this.semanticPlace = semanticPlace;
    }

    /**
     * Retrieve the location id of the location
     *
     * @return locationId
     */
    public int getLocationId() {
        return locationId;
    }

    /**
     * Retrieve the semantic place of the location
     *
     * @return semannticPlace
     */
    public String getSemanticPlace() {
        return semanticPlace;
    }
}
