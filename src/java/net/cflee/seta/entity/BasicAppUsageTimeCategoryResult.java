package net.cflee.seta.entity;

public class BasicAppUsageTimeCategoryResult {

    private int mild;
    private int normal;
    private int intense;

    public BasicAppUsageTimeCategoryResult() {
    }

    public void addAppUsageTime(double seconds) {
        if (seconds < 1 * 60 * 60) {
            // less than 1 hour
            mild++;
        } else if (seconds < 5 * 60 * 60) {
            // less than 5 hours
            normal++;
        } else {
            intense++;
        }
    }

    public int getMild() {
        return mild;
    }

    public int getNormal() {
        return normal;
    }

    public int getIntense() {
        return intense;
    }

    public int getTotal() {
        return mild + normal + intense;
    }

}
