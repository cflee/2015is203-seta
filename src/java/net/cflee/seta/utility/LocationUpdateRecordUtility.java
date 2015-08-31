
package net.cflee.seta.utility;

import java.util.ArrayList;
import java.util.Date;
import net.cflee.seta.entity.LocationUpdateRecord;

public class LocationUpdateRecordUtility {

    public static void compressRecords(ArrayList<LocationUpdateRecord> records) {
        for (int i = 0; i < records.size(); i++) {
            LocationUpdateRecord current = records.get(i);

            // obtain the next record if it doesn't overrun
            // if there's no next record, then there's nothing to do
            if (i + 1 < records.size()) {
                LocationUpdateRecord next = records.get(i + 1);

                // are these two records in the same semantic place?
                if (current.getSemanticPlace().equals(next.getSemanticPlace())) {
                    // are these two records contiguous?
                    Date expectedNextTimestamp = DateUtility.addSeconds(current.getTimestamp(), current.getDuration());
                    if (next.getTimestamp().equals(expectedNextTimestamp)) {
                        // yes, contiguous
                        // merge into the current record
                        current.setDuration(current.getDuration() + next.getDuration());
                        // remove the next record
                        records.remove(i + 1);
                        // revisit this current record + new next record in next cycle
                        i--;
                    }
                }
            }
        }
    }

    /**
     *
     * @param records LocationUpdateRecord sorted in mac-address order
     * @return ArrayList of ArrayLists of updates for each user (as identified by mac-address)
     */
    public static ArrayList<ArrayList<LocationUpdateRecord>> groupByUser(ArrayList<LocationUpdateRecord> records) {
        ArrayList<ArrayList<LocationUpdateRecord>> results = new ArrayList<>();
        ArrayList<LocationUpdateRecord> currentUserRecords = new ArrayList<>();
        String previousUser = null;

        for (LocationUpdateRecord record : records) {
            // is this still the same user as previous record
            if (previousUser != null && !previousUser.equals(record.getMacAddress())) {
                // different user
                results.add(currentUserRecords);
                currentUserRecords = new ArrayList<>();
            }

            currentUserRecords.add(record);
            previousUser = record.getMacAddress();
        }

        // final sub-list
        results.add(currentUserRecords);

        return results;
    }

}
