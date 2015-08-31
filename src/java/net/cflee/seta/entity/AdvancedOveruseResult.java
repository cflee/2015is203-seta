
package net.cflee.seta.entity;

public class AdvancedOveruseResult {

    private int classDuration;
    private int groupDuration;
    private int smartphoneUsageDuration;
    private int classAndGroupTime;
    private int distractingClassAndGroupTime;
    private int numOfDays;

    public AdvancedOveruseResult(int classDuration, int groupDuration, int smartphoneUsageDuration,
            int classAndGroupTime, int distractingClassAndGroupTime, int numOfDays) {
        this.classDuration = classDuration;
        this.groupDuration = groupDuration;
        this.smartphoneUsageDuration = smartphoneUsageDuration;
        this.classAndGroupTime = classAndGroupTime;
        this.distractingClassAndGroupTime = distractingClassAndGroupTime;
        this.numOfDays = numOfDays;
    }

    public int getClassDuration() {
        return classDuration;
    }

    public int getGroupDuration() {
        return groupDuration;
    }

    public int getSmartphoneUsageDuration() {
        return smartphoneUsageDuration;
    }

    public int getClassAndGroupTime() {
        return classAndGroupTime;
    }

    public int getDistractingClassAndGroupTime() {
        return distractingClassAndGroupTime;
    }

    public int getNumOfDays() {
        return numOfDays;
    }

    public int getOverallIndex() {
        double index = (double) distractingClassAndGroupTime / classAndGroupTime;
        if (Double.isNaN(index) || index < 0.05) {
            return 1;
        } else if (index < 0.15) {
            return 2;
        } else {
            return 3;
        }
    }

}
