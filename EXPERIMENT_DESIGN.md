
# Experiment Design Card

## 1. Edit Distance

### Research Question
How do Recursive, Table, and Two Rows implementations
differ in execution time, number of operations, and memory usage?

### Input Sizes
- Recursive: 2, 4, 6, 8, 10
- Table: 100, 200, 400, 800
- Two Rows: 100, 200, 400, 800

### Input Types
- Identical: both strings contain the same characters.
- Different: strings contain different repeated characters.
- Random: strings contain randomly generated uppercase letters.

### Metrics
- Execution time (milliseconds)
- Recursive calls
- DP cells calculated
- Character comparisons
- Maximum recursion depth
- Memory cells used

### Prediction
Recursive implementation is expected to become significantly
slower as input size increases because it does not use memoization.

Table and Two Rows should have similar execution time.
Two Rows is expected to use less memory because it stores
only two rows instead of the complete DP table.

## 2. String Search

### Research Question
How do Naive and KMP search algorithms behave on different
text inputs and pattern sizes?

### Input Sizes
- Text: 2,000, 4,000, 8,000, 16,000
- Pattern: 20, 40, 80, 160

### Input Types
- Best Case: the first character of the pattern does not match.
- Worst Case: repeated A characters in the text and a pattern
  ending with B.
- Random: randomly generated uppercase letters.

### Metrics
- Execution time (milliseconds)
- Character comparisons
- Text comparisons
- LPS comparisons
- Alignments tried
- Fallbacks

### Prediction
Naive is expected to perform poorly on the worst-case input
because it repeats many character comparisons.

KMP is expected to perform better on this input because it
uses the LPS array to avoid unnecessary comparisons.

## 3. Experiment Procedure

- Use a fixed random seed: 42.
- Perform one warm-up run before measurements.
- Perform 5 measured runs for each experiment.
- Calculate the median execution time.
- Save every measured run in results/results.csv.

## 4. Planned Plots

1. Edit Distance: operation counts by input size.
2. Edit Distance: memory usage comparison.
3. String Search: character comparisons by text size.
4. String Search: Naive-to-KMP comparison ratio.

At least one plot will use a logarithmic axis or a ratio.

## 5. Reproducibility

All random input is generated using Java Random with seed 42.
The same input sizes, input types, and algorithms are used
for each experiment.
