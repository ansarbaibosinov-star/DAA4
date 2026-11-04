package com.daa.search;
import com.daa.metrics.Metrics;

import java.util.ArrayList;
import java.util.List;

public class KMP implements StringSearch {

    private final Metrics metrics;

    public KMP(Metrics metrics) {
        this.metrics = metrics;
    }

    public int[] lps(String pattern) {

        int[] lps = new int[pattern.length()];

        int length = 0;
        int i = 1;

        while (i < pattern.length()) {

            metrics.lpsComparisons++;
            metrics.charComparisons++;

            if (pattern.charAt(i) == pattern.charAt(length)) {

                length++;
                lps[i] = length;
                i++;

            } else {

                if (length != 0) {
                    length = lps[length - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }

    @Override
    public List<Integer> search(String text, String pattern) {

        metrics.reset();

        if (pattern.isEmpty()) {
            throw new IllegalArgumentException(
                    "Pattern cannot be empty"
            );
        }

        List<Integer> matches = new ArrayList<>();

        int n = text.length();
        int m = pattern.length();

        if (m > n) {
            return matches;
        }

        // Build the LPS array before searching.
        int[] lps = lps(pattern);

        int i = 0;
        int j = 0;

        while (i < n) {

            metrics.textComparisons++;
            metrics.charComparisons++;

            if (text.charAt(i) == pattern.charAt(j)) {

                i++;
                j++;

                if (j == m) {

                    matches.add(i - j);

                    // Continue searching for overlapping matches.
                    j = lps[j - 1];
                }

            } else {

                if (j != 0) {

                    metrics.fallbacks++;
                    j = lps[j - 1];

                } else {

                    i++;
                }
            }
        }

        return matches;
    }
}
