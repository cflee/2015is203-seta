
package net.cflee.seta.entity;

public class OveruseResult {
    private double dailyUsageDuration;
    private double gameUsageDuration;
    private double accessFrequency;
    private int overallIndex;

    public OveruseResult(double dailyUsageDuration, double gameUsageDuration, double accessFrequency, int overallIndex) {
        this.dailyUsageDuration = dailyUsageDuration;
        this.gameUsageDuration = gameUsageDuration;
        this.accessFrequency = accessFrequency;
        this.overallIndex = overallIndex;
    }

    public double getDailyUsageDuration() {
        return dailyUsageDuration;
    }

    public double getGameUsageDuration() {
        return gameUsageDuration;
    }

    public double getAccessFrequency() {
        return accessFrequency;
    }

    public int getOverallIndex() {
        return overallIndex;
    }

}
