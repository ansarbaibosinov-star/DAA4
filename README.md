# DAA Assignment 4 — Dynamic Programming and String Algorithms

## Overview

This project was developed for the Design and Analysis of Algorithms course.

The project implements and compares dynamic programming and string-searching algorithms for a City Search & Spell-Check system.

The main goals are:
- Implement multiple Edit Distance algorithms.
- Compare Naive Search and Knuth-Morris-Pratt (KMP).
- Measure execution time, operation counts, and memory usage.
- Run reproducible experiments.
- Analyze the results using CSV data and charts.

## Technologies

- Java 17+
- Maven
- JUnit 5
- Python 3
- Matplotlib

## Implemented Algorithms

### 1. Edit Distance

The project contains three implementations:

| Class | Description | Time complexity |
|---|---|---|
| `RecursiveEditDistance` | Direct recursion without memoization | Exponential worst case |
| `TableEditDistance` | Bottom-up dynamic programming using a 2D table | O(n × m) |
| `TwoRowsEditDistance` | Dynamic programming using two rows | O(n × m) |

The Two Rows implementation reduces additional memory usage from O(n × m) to O(m).

### 2. String Searching

| Class | Description | Worst-case search complexity |
|---|---|---|
| `NaiveSearch` | Checks possible pattern positions one by one | O(n × m) |
| `KMP` | Uses the LPS array to avoid repeated comparisons | O(n + m), including preprocessing |

Both algorithms return all matching starting positions in increasing order and support overlapping matches.

An empty pattern is rejected with `IllegalArgumentException`.

## Project Structure

```text
DAA4/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── daa/
│   │               ├── editdistance/
│   │               │   ├── EditDistance.java
│   │               │   ├── RecursiveEditDistance.java
│   │               │   ├── TableEditDistance.java
│   │               │   └── TwoRowsEditDistance.java
│   │               ├── search/
│   │               │   ├── StringSearch.java
│   │               │   ├── NaiveSearch.java
│   │               │   └── KMP.java
│   │               ├── metrics/
│   │               │   └── Metrics.java
│   │               ├── experiment/
│   │               │   ├── Benchmark.java
│   │               │   └── DataGenerator.java
│   │               └── Main.java
│   └── test/
│       └── java/
│           └── com/
│               └── daa/
│
├── scripts/
│   └── plot_results.py
│
├── results/
│   ├── results.csv
│   └── plots/
│       ├── edit_distance_time.png
│       ├── edit_distance_memory.png
│       ├── string_search_comparisons.png
│       ├── string_search_ratio.png
│       ├── recursive_edit_distance_time.png
│       └── recursive_edit_distance_calls.png
│
├── pom.xml
├── README.md
└── REPORT.md
```

## Experimental Design

The experiments use a fixed random seed (`42`) to make generated input data reproducible.

Each experiment includes:
- One warm-up run.
- Five measured runs.
- Median execution time.
- Algorithm-specific operation counters.
- Multiple input sizes.
- Different input types, including best-case and worst-case scenarios.

### Edit Distance input sizes

| Implementation | Input sizes |
|---|---|
| Recursive | 2, 4, 6, 8, 10 |
| Table | 100, 200, 400, 800 |
| Two Rows | 100, 200, 400, 800 |

Input types: `identical`, `different`, and `random` where applicable.

### String Searching input sizes

| Text size | Pattern size |
|---:|---:|
| 2,000 | 20 |
| 4,000 | 40 |
| 8,000 | 80 |
| 16,000 | 160 |

Input types:
- `best_case`
- `worst_case`
- `random`

## Collected Metrics

### Edit Distance
- `recursiveCalls`
- `dpCells`
- `charComparisons`
- `maxDepth`
- `memoryCells`

### String Searching
- `charComparisons`
- `textComparisons`
- `lpsComparisons`
- `alignmentsTried`
- `fallbacks`

Execution time is measured in milliseconds.

## How to Run

### Requirements

Install:
- JDK 17 or newer.
- Maven.
- Python 3.
- Matplotlib.

### 1. Run unit tests

From the project root:

```bash
mvn clean test
```

### 2. Run benchmarks

Run `com.daa.Main` from the project root directory.

The benchmark results will be saved to:

```text
results/results.csv
```

### 3. Generate charts

Install Matplotlib if it is not already installed:

```bash
python -m pip install matplotlib
```

Run the plotting script:

```bash
python scripts/plot_results.py
```

The generated charts will be saved to:

```text
results/plots/
```

## Results and Report

- `results/results.csv` contains the benchmark measurements.
- `results/plots/` contains generated charts.
- `REPORT.md` contains the experimental design, observations, analysis, and conclusions.

## Testing

JUnit tests cover:
- Reference Edit Distance values.
- Empty-string cases.
- Agreement between the three Edit Distance implementations on random short strings.
- Reference LPS arrays.
- String-search reference cases.
- Overlapping matches.
- Agreement between Naive Search and KMP on random inputs.
- Empty-pattern exception handling.
- Patterns longer than the text.
