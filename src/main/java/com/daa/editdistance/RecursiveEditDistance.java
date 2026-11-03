package com.daa.editdistance;
import com.daa.metrics.Metrics;
public class RecursiveEditDistance implements EditDistance {

    private final Metrics metrics;

    public RecursiveEditDistance(Metrics metrics) {
        this.metrics = metrics;
    }

    @Override
    public int distance(String a, String b) {
        metrics.reset();
        return recursive(a, b, a.length(), b.length(), 1);
    }

    /*
     * DP Design:
     * State: dp(i, j) is the edit distance between the first i characters
     * of a and the first j characters of b.
     *
     * Transition: If the last characters match, use dp(i-1, j-1).
     * Otherwise, use 1 + min(delete, insert, replace).
     *
     * Base case: If i == 0, return j. If j == 0, return i.
     */
    private int recursive(String a, String b, int i, int j, int depth) {

        metrics.recursiveCalls++;
        metrics.recordDepth(depth);

        if (i == 0) {
            return j;
        }

        if (j == 0) {
            return i;
        }

        metrics.charComparisons++;

        if (a.charAt(i - 1) == b.charAt(j - 1)) {
            return recursive(a, b, i - 1, j - 1, depth + 1);
        }

        int delete = recursive(a, b, i - 1, j, depth + 1);
        int insert = recursive(a, b, i, j - 1, depth + 1);
        int replace = recursive(a, b, i - 1, j - 1, depth + 1);

        return 1 + Math.min(delete, Math.min(insert, replace));
    }
}

