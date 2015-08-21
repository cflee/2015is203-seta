package net.cflee.seta.entity;

public class AppUsageTimeResult {

    private String name;
    private int duration;

    public AppUsageTimeResult(String name, int duration) {
        this.name = name;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

}
