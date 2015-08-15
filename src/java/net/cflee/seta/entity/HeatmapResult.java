package net.cflee.seta.entity;

/**
 * An entity class of HeatmapResult with the semantic place, total number of people in the semantic place, and the crowd
 * density of the semantic place
 *
 */
public class HeatmapResult {

    private String placeName;
    private int numOfPeople;
    private int crowdDensity;

    /**
     * Construct a HeatmapResult object with semantic place and total number of people in the semantic place
     *
     * @param placeName
     * @param numOfPeople
     */
    public HeatmapResult(String placeName, int numOfPeople) {
        this.placeName = placeName;
        this.numOfPeople = numOfPeople;
        crowdDensity = calculateCrowdDensity(numOfPeople);
    }

    /**
     * Retrieve the semantic place
     *
     * @return placeName
     */
    public String getPlaceName() {
        return placeName;
    }

    /**
     * Retrieve the total number of people in the semantic place
     *
     * @return numOfPeople
     */
    public int getNumOfPeople() {
        return numOfPeople;
    }

    /**
     * Retrieve the crowd density of the semantic place
     *
     * @return crowdDensity
     */
    public int getCrowdDensity() {
        return crowdDensity;
    }

    /**
     * Calculate the crowd density based on the total number of people in the semantic place
     *
     * @param numOfPeople
     * @return 0 if there is nobody in the semantic place, 1 if there are more than 1 but less than 3 people, 2 if there
     * are more 2 but less than 6, 3 if there are more 5 but less than 11, 4 if there are more 10 but less than 21, 5 if
     * there are more 20 but less than 31, and 6 otherwise
     */
    private int calculateCrowdDensity(int numOfPeople) {

        if (numOfPeople == 0) {
            return 0;
        } else if (numOfPeople <= 2) {
            return 1;
        } else if (numOfPeople <= 5) {
            return 2;
        } else if (numOfPeople <= 10) {
            return 3;
        } else if (numOfPeople <= 20) {
            return 4;
        } else if (numOfPeople <= 30) {
            return 5;
        } else {
            return 6;
        }
    }
}
