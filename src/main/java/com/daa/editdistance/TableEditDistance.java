package com.daa.editdistance;
import com.daa.metrics.Metrics;
public class TableEditDistance implements EditDistance {

    private final Metrics metrics;

    public TableEditDistance(Metrics metrics) {
        this.metrics = metrics;
    }

    @Override
    public int distance(String a, String b) {
        metrics.reset();

        int n = a.length();
        int m = b.length();

        int[][] dp = new int[n + 1][m + 1];

        metrics.recordMemory((long) (n + 1) * (m + 1));

        // Base cases: converting a string to an empty string.
        for (int i = 0; i <= n; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= m; j++) {
            dp[0][j] = j;
        }

        /*
         * DP Design:
         * State: dp[i][j] is the edit distance between prefixes
         * a[0..i-1] and b[0..j-1].
         *
         * Transition: min(delete, insert, replace), with cost 0
         * when the current characters are equal and 1 otherwise.
         *
         * Base case: dp[i][0] = i and dp[0][j] = j.
         */
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {

                metrics.dpCells++;
                metrics.charComparisons++;

                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    int delete = dp[i - 1][j] + 1;
                    int insert = dp[i][j - 1] + 1;
                    int replace = dp[i - 1][j - 1] + 1;

                    dp[i][j] = Math.min(
                            delete,
                            Math.min(insert, replace)
                    );
                }
            }
        }

        return dp[n][m];
    }
}
