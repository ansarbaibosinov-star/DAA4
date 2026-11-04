package com.daa.editdistance;
import com.daa.metrics.Metrics;

public class TwoRowsEditDistance implements EditDistance {

    private final Metrics metrics;

    public TwoRowsEditDistance(Metrics metrics) {
        this.metrics = metrics;
    }

    @Override
    public int distance(String a, String b) {
        metrics.reset();

        int n = a.length();
        int m = b.length();

        // Use the shorter string as columns to minimize memory.
        if (m > n) {
            String temp = a;
            a = b;
            b = temp;

            n = a.length();
            m = b.length();
        }

        int[] previous = new int[m + 1];
        int[] current = new int[m + 1];

        metrics.recordMemory(2L * (m + 1));

        for (int j = 0; j <= m; j++) {
            previous[j] = j;
        }

        /*
         * DP Design:
         * State: previous[j] and current[j] store distances
         * for the previous and current prefixes of a.
         *
         * Transition: use the values above, left and diagonal,
         * then swap the two rows after each iteration.
         *
         * Base case: previous[j] = j and current[0] = i.
         */
        for (int i = 1; i <= n; i++) {

            current[0] = i;

            for (int j = 1; j <= m; j++) {

                metrics.dpCells++;
                metrics.charComparisons++;

                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    current[j] = previous[j - 1];
                } else {
                    int delete = previous[j] + 1;
                    int insert = current[j - 1] + 1;
                    int replace = previous[j - 1] + 1;

                    current[j] = Math.min(
                            delete,
                            Math.min(insert, replace)
                    );
                }
            }

            int[] temp = previous;
            previous = current;
            current = temp;
        }

        return previous[m];
    }
}
