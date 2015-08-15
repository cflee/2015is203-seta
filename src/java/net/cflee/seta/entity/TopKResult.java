package net.cflee.seta.entity;

/**
 * Container to store the rank #, semantic place name, and count of people for
 * Top-k use cases.
 *
 * These include Top-k Popular Places, Top-k Next Places, and their respective
 * grouped variants.
 */
public class TopKResult implements Comparable<TopKResult> {

    private int rank;
    private String semanticPlace;
    private int count;

    /**
     * Constructs a TopKResult to store a single 'row' of data, with default
     * rank.
     *
     * The default rank is 0. This should be used when the rank number is to be
     * determined later.
     *
     * @param semanticPlace name of semantic place
     * @param count number of people in this semantic place
     */
    public TopKResult(String semanticPlace, int count) {
        this.rank = 0;
        this.semanticPlace = semanticPlace;
        this.count = count;
    }

    /**
     * Retrieve the rank of the topKResult
     *
     * @return rank
     */
    public int getRank() {
        return rank;
    }

    /**
     * Retrieve the semanticPlace of the topKResult
     *
     * @return semanticPlace
     */
    public String getSemanticPlace() {
        return semanticPlace;
    }

    /**
     * Retrieve the total number of counts of the topKResult
     *
     * @return count
     */
    public int getCount() {
        return count;
    }

    /**
     * Set the total number of counts of the topKResult
     *
     * @param count
     */
    public void addCount(int count) {
        this.count += count;
    }

    /**
     * Set the rank of the topKResult
     *
     * @param rank
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * Natural order: email ascending, and mac address ascending
     *
     * @param that LocationUpdate to be compared against
     * @return -1 if this object should be before that, +1 if this object should
     * be after that, and 0 when they are tied
     */
    @Override
    public int compareTo(TopKResult topKResult) {
        if (this.count == topKResult.getCount()) {
            return this.semanticPlace.compareTo(topKResult.getSemanticPlace());
        }
        return topKResult.getCount() - this.count;
    }
}
