
package com.daa.experiment;

import com.daa.editdistance.RecursiveEditDistance;
import com.daa.editdistance.TableEditDistance;
import com.daa.editdistance.TwoRowsEditDistance;
import com.daa.metrics.Metrics;
import com.daa.search.KMP;
import com.daa.search.NaiveSearch;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class Benchmark {

    private static final int REPEATS = 5;

    private static final int[] DP_SIZES = {
            100, 200, 400, 800
    };

    private static final int[] RECURSIVE_SIZES = {
            2, 4, 6, 8, 10
    };

    private static final int[] TEXT_SIZES = {
            2000, 4000, 8000, 16000
    };

    private static final int[] PATTERN_SIZES = {
            20, 40, 80, 160
    };

    private final DataGenerator generator = new DataGenerator();

    private final List<String> csvRows = new ArrayList<>();

    private record RunResult(
            int run,
            double timeMs,
            long recursiveCalls,
            long dpCells,
            long charComparisons,
            int maxDepth,
            long memoryCells,
            long textComparisons,
            long lpsComparisons,
            long alignmentsTried,
            long fallbacks,
            long result
    ) {}

    @FunctionalInterface
    private interface EditRunner {
        int run(Metrics metrics, String a, String b);
    }

    @FunctionalInterface
    private interface SearchRunner {
        int run(Metrics metrics, String text, String pattern);
    }

    public void runAll() throws IOException {

        csvRows.clear();

        runEditDistanceExperiments();
        runStringSearchExperiments();

        saveResults();

        System.out.println();
        System.out.println("Benchmark completed!");
        System.out.println("Results saved to results/results.csv");
    }

    // -------------------------------------------------
    // EDIT DISTANCE EXPERIMENTS
    // -------------------------------------------------

    private void runEditDistanceExperiments() {

        System.out.println("Starting Edit Distance experiments...");

        // Recursive algorithm uses smaller input sizes.
        for (int size : RECURSIVE_SIZES) {

            runEditCase(
                    "Recursive",
                    "identical",
                    generator.identicalString(size),
                    generator.identicalString(size),
                    (metrics, a, b) ->
                            new RecursiveEditDistance(metrics).distance(a, b)
            );

            runEditCase(
                    "Recursive",
                    "different",
                    generator.differentString(size),
                    generator.identicalString(size),
                    (metrics, a, b) ->
                            new RecursiveEditDistance(metrics).distance(a, b)
            );
        }

        // Table and Two Rows use larger input sizes.
        for (int size : DP_SIZES) {

            String identical = generator.identicalString(size);
            String different = generator.differentString(size);

            String randomA = generator.randomString(size);
            String randomB = generator.randomString(size);

            EditRunner[] runners = {
                    (metrics, a, b) ->
                            new TableEditDistance(metrics).distance(a, b),

                    (metrics, a, b) ->
                            new TwoRowsEditDistance(metrics).distance(a, b)
            };

            String[] algorithms = {
                    "Table",
                    "TwoRows"
            };

            for (int i = 0; i < runners.length; i++) {

                runEditCase(
                        algorithms[i],
                        "identical",
                        identical,
                        identical,
                        runners[i]
                );

                runEditCase(
                        algorithms[i],
                        "different",
                        different,
                        different,
                        runners[i]
                );

                runEditCase(
                        algorithms[i],
                        "random",
                        randomA,
                        randomB,
                        runners[i]
                );
            }
        }
    }

    private void runEditCase(
            String algorithm,
            String input,
            String a,
            String b,
            EditRunner runner
    ) {

        // Warm-up run. Its result is not included in measurements.
        runner.run(new Metrics(), a, b);

        List<RunResult> runs = new ArrayList<>();

        for (int run = 1; run <= REPEATS; run++) {

            Metrics metrics = new Metrics();

            long start = System.nanoTime();

            int result = runner.run(metrics, a, b);

            long end = System.nanoTime();

            double timeMs = (end - start) / 1_000_000.0;

            runs.add(new RunResult(
                    run,
                    timeMs,
                    metrics.recursiveCalls,
                    metrics.dpCells,
                    metrics.charComparisons,
                    metrics.maxDepth,
                    metrics.memoryCells,
                    0,
                    0,
                    0,
                    0,
                    result
            ));
        }

        double median = calculateMedian(
                runs.stream()
                        .map(RunResult::timeMs)
                        .toList()
        );

        addCsvRows(
                algorithm,
                input,
                a.length(),
                b.length(),
                runs,
                median
        );

        System.out.printf(
                "Edit Distance: %-10s %-10s n=%d median=%.4f ms%n",
                algorithm,
                input,
                a.length(),
                median
        );
    }

    // -------------------------------------------------
    // STRING SEARCH EXPERIMENTS
    // -------------------------------------------------

    private void runStringSearchExperiments() {

        System.out.println();
        System.out.println("Starting String Search experiments...");

        String[] algorithms = {
                "Naive",
                "KMP"
        };

        for (int i = 0; i < TEXT_SIZES.length; i++) {

            int textSize = TEXT_SIZES[i];
            int patternSize = PATTERN_SIZES[i];

            String bestText = generator.bestCaseText(textSize);
            String bestPattern = generator.bestCasePattern(patternSize);

            String worstText = generator.worstCaseText(textSize);
            String worstPattern = generator.worstCasePattern(patternSize);

            String randomText = generator.randomString(textSize);
            String randomPattern = generator.randomString(patternSize);

            for (String algorithm : algorithms) {

                SearchRunner runner;

                if (algorithm.equals("Naive")) {

                    runner = (metrics, text, pattern) ->
                            new NaiveSearch(metrics)
                                    .search(text, pattern).size();

                } else {

                    runner = (metrics, text, pattern) ->
                            new KMP(metrics)
                                    .search(text, pattern).size();
                }

                runSearchCase(
                        algorithm,
                        "best_case",
                        bestText,
                        bestPattern,
                        runner
                );

                runSearchCase(
                        algorithm,
                        "worst_case",
                        worstText,
                        worstPattern,
                        runner
                );

                runSearchCase(
                        algorithm,
                        "random",
                        randomText,
                        randomPattern,
                        runner
                );
            }
        }
    }

    private void runSearchCase(
            String algorithm,
            String input,
            String text,
            String pattern,
            SearchRunner runner
    ) {

        // Warm-up run.
        runner.run(new Metrics(), text, pattern);

        List<RunResult> runs = new ArrayList<>();

        for (int run = 1; run <= REPEATS; run++) {

            Metrics metrics = new Metrics();

            long start = System.nanoTime();

            int result = runner.run(metrics, text, pattern);

            long end = System.nanoTime();

            double timeMs = (end - start) / 1_000_000.0;

            runs.add(new RunResult(
                    run,
                    timeMs,
                    0,
                    0,
                    metrics.charComparisons,
                    0,
                    0,
                    metrics.textComparisons,
                    metrics.lpsComparisons,
                    metrics.alignmentsTried,
                    metrics.fallbacks,
                    result
            ));
        }

        double median = calculateMedian(
                runs.stream()
                        .map(RunResult::timeMs)
                        .toList()
        );

        addCsvRows(
                algorithm,
                input,
                text.length(),
                pattern.length(),
                runs,
                median
        );

        System.out.printf(
                "String Search: %-10s %-10s n=%d m=%d median=%.4f ms%n",
                algorithm,
                input,
                text.length(),
                pattern.length(),
                median
        );
    }

    // -------------------------------------------------
    // MEDIAN
    // -------------------------------------------------

    private double calculateMedian(List<Double> values) {

        List<Double> sorted = new ArrayList<>(values);

        sorted.sort(Comparator.naturalOrder());

        int middle = sorted.size() / 2;

        return sorted.get(middle);
    }

    // -------------------------------------------------
    // CSV
    // -------------------------------------------------

    private void addCsvRows(
            String algorithm,
            String input,
            int n,
            int m,
            List<RunResult> runs,
            double median
    ) {

        for (RunResult run : runs) {

            String row = String.join(",",
                    algorithm,
                    input,
                    String.valueOf(n),
                    String.valueOf(m),
                    String.valueOf(run.run()),
                    String.format(Locale.US, "%.6f", run.timeMs()),
                    String.format(Locale.US, "%.6f", median),
                    String.valueOf(run.recursiveCalls()),
                    String.valueOf(run.dpCells()),
                    String.valueOf(run.charComparisons()),
                    String.valueOf(run.maxDepth()),
                    String.valueOf(run.memoryCells()),
                    String.valueOf(run.textComparisons()),
                    String.valueOf(run.lpsComparisons()),
                    String.valueOf(run.alignmentsTried()),
                    String.valueOf(run.fallbacks()),
                    String.valueOf(run.result())
            );

            csvRows.add(row);
        }
    }

    private void saveResults() throws IOException {

        Path output = Path.of("results", "results.csv");

        Files.createDirectories(output.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(output)) {

            writer.write(
                    "algorithm,input,n,m,run,time_ms,median_time_ms,"
                            + "recursive_calls,dp_cells,char_comparisons,"
                            + "max_depth,memory_cells,text_comparisons,"
                            + "lps_comparisons,alignments_tried,fallbacks,result"
            );

            writer.newLine();

            for (String row : csvRows) {
                writer.write(row);
                writer.newLine();
            }
        }
    }
}
