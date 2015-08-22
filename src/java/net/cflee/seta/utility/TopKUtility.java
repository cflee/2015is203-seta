
package net.cflee.seta.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import net.cflee.seta.entity.TopKResult;

public class TopKUtility {

    public static void sortRankFilter(ArrayList<TopKResult> results, int k) {
        // sort by mac address ascending, timestamp ascending
        Collections.sort(results, new Comparator<TopKResult>() {
            @Override
            public int compare(TopKResult o1, TopKResult o2) {
                // duration descending
                int compare = Integer.compare(o2.getDuration(), o1.getDuration());
                if (compare != 0) {
                    return compare;
                }

                // break ties with name ascending
                return o1.getName().compareTo(o2.getName());
            }
        });

        // assign ranks
        int rank = 1;
        for (int i = 0; i < results.size(); i++) {
            TopKResult result = results.get(i);

            if (i == 0) {
                // special case for first item since no prev to compare against
                result.setRank(1);
            } else {
                TopKResult prevResult = results.get(i - 1);
                if (result.getDuration() < prevResult.getDuration()) {
                    // next rank
                    rank++;
                }
                result.setRank(rank);
            }
        }

        // retain only the top-k
        for (int i = 0; i < results.size(); i++) {
            TopKResult result = results.get(i);
            if (result.getRank() > k) {
                // remove remaining items and stop
                results.subList(i, results.size()).clear();
                break;
            }
        }
    }

}
