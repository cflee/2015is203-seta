package net.cflee.seta.entity;

/**
 * Container to store the rank #, name, and duration of things for Top-k use cases.
 */
public class TopKResult implements Comparable<TopKResult> {

    private int rank;
    private String name;
    private int duration;

    /**
     * Constructs a TopKResult to store a single 'row' of data, with default rank.
     *
     * The default rank is 0. This should be used when the rank number is to be determined later.
     *
     * @param name name
     * @param duration amount of time in seconds
     */
    public TopKResult(String name, int duration) {
        this.rank = 0;
        this.name = name;
        this.duration = duration;
    }

    public int getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public void addCount(int count) {
        this.duration += count;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * Natural order: duration ascending, and name ascending
     *
     * @param that
     * @return -1 if this object should be before that, +1 if this object should be after that, and 0 when they are tied
     */
    @Override
    public int compareTo(TopKResult that) {
        int difference = that.getDuration() - this.duration;
        if (difference != 0) {
            return difference;
        }
        return this.name.compareTo(that.getName());
    }
}
