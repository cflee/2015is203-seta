
package net.cflee.seta.entity;

import java.util.HashMap;

public class SocialActivenessResult {

    private int socialDuration;
    private HashMap<AppUpdateRecord, Integer> socialAppDurations;
    private int timeInGroups;
    private int timeAlone;

    public SocialActivenessResult(int socialDuration, HashMap<AppUpdateRecord, Integer> socialAppDurations,
            int timeInGroups, int timeAlone) {
        this.socialDuration = socialDuration;
        this.socialAppDurations = socialAppDurations;
        this.timeInGroups = timeInGroups;
        this.timeAlone = timeAlone;
    }

    public int getSocialDuration() {
        return socialDuration;
    }

    public HashMap<AppUpdateRecord, Integer> getSocialAppDurations() {
        return socialAppDurations;
    }

    public int getTimeInGroups() {
        return timeInGroups;
    }

    public int getTimeAlone() {
        return timeAlone;
    }

}
