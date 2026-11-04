package com.daa.search;
import com.daa.metrics.Metrics;
import java.util.ArrayList;
import java.util.List;

public class NaiveSearch implements StringSearch {

    private final Metrics metrics;

    public NaiveSearch(Metrics metrics) {
        this.metrics = metrics;
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

        for (int i = 0; i <= n - m; i++) {

            metrics.alignmentsTried++;

            int j = 0;

            while (j < m) {

                metrics.textComparisons++;
                metrics.charComparisons++;

                if (text.charAt(i + j) != pattern.charAt(j)) {
                    break;
                }

                j++;
            }

            if (j == m) {
                matches.add(i);
            }
        }

        return matches;
    }
}
