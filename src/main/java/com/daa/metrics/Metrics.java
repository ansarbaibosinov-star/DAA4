
package com.daa.metrics;

public class Metrics {

    // Edit Distance counters
    public long recursiveCalls;
    public long dpCells;
    public long charComparisons;
    public int maxDepth;
    public long memoryCells;

    // String Search counters
    public long textComparisons;
    public long lpsComparisons;
    public long alignmentsTried;
    public long fallbacks;

    public Metrics() {
        reset();
    }

    public void reset() {
        recursiveCalls = 0;
        dpCells = 0;
        charComparisons = 0;
        maxDepth = 0;
        memoryCells = 0;

        textComparisons = 0;
        lpsComparisons = 0;
        alignmentsTried = 0;
        fallbacks = 0;
    }

    public void recordDepth(int depth) {
        maxDepth = Math.max(maxDepth, depth);
    }

    public void recordMemory(long cells) {
        memoryCells = Math.max(memoryCells, cells);
    }
}
