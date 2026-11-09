
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

    private static final double RECURSIVE_TIME_LIMIT_MS = 2000.0;

    private static final int[] DP_SIZES = {
            100, 200, 400, 800
    };

    private static final int[] RECURSIVE_SIZES = {
            2, 4, 6, 8, 10, 12, 14
    };

    private static final int[] TEXT_SIZES = {
            2000, 4000, 8000, 16000
    };

    private static final int[] PATTERN_SIZES = {
            20, 40, 80, 160
    };

    private static final int FIXED_PATTERN_SIZE = 40;
    private static final int FIXED_TEXT_SIZE = 16000;

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
        System.out.println("Total CSV data rows: " + csvRows.size());
    }

    // -------------------------------------------------
    // EDIT DISTANCE EXPERIMENTS
    // -------------------------------------------------

    private void runEditDistanceExperiments() {

        System.out.println("Starting Edit Distance experiments...");

        // Recursive algorithm uses progressively larger inputs.
        // Stop when the different-input median reaches 2 seconds.
        for (int size : RECURSIVE_SIZES) {

            runEditCase(
                    "Recursive",
                    "identical",
                    generator.identicalString(size),
                    generator.identicalString(size),
                    (metrics, a, b) ->
                            new RecursiveEditDistance(metrics).distance(a, b)
            );

            double differentMedian = runEditCase(
                    "Recursive",
                    "different",
                    generator.differentString(size),
                    generator.identicalString(size),
                    (metrics, a, b) ->
                            new RecursiveEditDistance(metrics).distance(a, b)
            );

            if (differentMedian >= RECURSIVE_TIME_LIMIT_MS) {
                System.out.printf(
                        "Recursive benchmark stopped at n=%d: "
                                + "median reached %.2f ms.%n",
                        size,
                        differentMedian
                );
                break;
            }
        }

        // Table and Two Rows use larger input sizes.
        for (int size : DP_SIZES) {

            String identical = generator.identicalString(size);

            String differentA = generator.differentString(size);
            String differentB = generator.identicalString(size);

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
                        differentA,
                        differentB,
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

    private double runEditCase(
            String algorithm,
            String input,
            String a,
            String b,
            EditRunner runner
    ) {

        // Warm-up run.
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
                "Edit Distance: %-10s %-12s n=%d median=%.4f ms%n",
                algorithm,
                input,
                a.length(),
                median
        );

        return median;
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

        // Experiment A:
        // Original best, worst and random input types.
        // Both n and m grow in this series.
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

                SearchRunner runner = createSearchRunner(algorithm);

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

        // Experiment B:
        // Fixed pattern size, increasing text size.
        // Exactly one occurrence is inserted into the text.
        System.out.println();
        System.out.println("Fixed pattern experiment: m = "
                + FIXED_PATTERN_SIZE);

        String fixedPattern =
                generator.randomString(FIXED_PATTERN_SIZE);

        for (int textSize : TEXT_SIZES) {

            String text = generator.textWithMatch(
                    textSize,
                    fixedPattern
            );

            for (String algorithm : algorithms) {

                runSearchCase(
                        algorithm,
                        "fixed_m_single_match",
                        text,
                        fixedPattern,
                        createSearchRunner(algorithm)
                );
            }
        }

        // Experiment C:
        // Fixed text size, increasing pattern size.
        // Two occurrences are inserted into the text.
        System.out.println();
        System.out.println("Fixed text experiment: n = "
                + FIXED_TEXT_SIZE);

        for (int patternSize : PATTERN_SIZES) {

            String pattern = generator.randomString(patternSize);

            String text = generator.textWithMultipleMatches(
                    FIXED_TEXT_SIZE,
                    pattern
            );

            for (String algorithm : algorithms) {

                runSearchCase(
                        algorithm,
                        "fixed_n_two_matches",
                        text,
                        pattern,
                        createSearchRunner(algorithm)
                );
            }
        }

        // Experiment D:
        // Fixed pattern size and text size.
        // Every possible position is a match, including overlaps.
        System.out.println();
        System.out.println("Overlapping matches experiment...");

        for (int textSize : TEXT_SIZES) {

            String text = generator.repeatedString(textSize, 'A');
            String pattern = generator.repeatedString(
                    FIXED_PATTERN_SIZE,
                    'A'
            );

            for (String algorithm : algorithms) {

                runSearchCase(
                        algorithm,
                        "overlapping_matches",
                        text,
                        pattern,
                        createSearchRunner(algorithm)
                );
            }
        }
    }

    private SearchRunner createSearchRunner(String algorithm) {

        if (algorithm.equals("Naive")) {

            return (metrics, text, pattern) ->
                    new NaiveSearch(metrics)
                            .search(text, pattern).size();
        }

        if (algorithm.equals("KMP")) {

            return (metrics, text, pattern) ->
                    new KMP(metrics)
                            .search(text, pattern).size();
        }

        throw new IllegalArgumentException(
                "Unknown search algorithm: " + algorithm
        );
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
                "String Search: %-10s %-24s n=%d m=%d "
                        + "result=%d median=%.4f ms%n",
                algorithm,
                input,
                text.length(),
                pattern.length(),
                runs.get(0).result(),
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
