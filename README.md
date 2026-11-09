# DAA Assignment 4 — Dynamic Programming and String Algorithms

## Project Overview

This project was developed for the **Design and Analysis of Algorithms (DAA)** course.

The assignment explores two algorithmic problems related to a City Search & Spell-Check platform:

1. **Edit Distance** — calculating the minimum number of operations required to transform one string into another.
2. **String Search** — finding all occurrences of a pattern inside a text.

The project implements multiple algorithms, compares their performance, measures operation counts and auxiliary storage, and visualizes experimental results.

## Technologies

- Java 17+
- Maven
- JUnit 5
- Python 3
- Matplotlib
- Git

## Implemented Algorithms

### 1. Edit Distance

Three implementations calculate the Levenshtein distance between two strings.

| Algorithm | Approach | Time Complexity | Auxiliary Space |
|---|---|---|---|
| Recursive | Direct recursion without memoization | Exponential | Exponential recursion stack |
| Table | Bottom-up dynamic programming with a full table | O(n × m) | O(n × m) |
| TwoRows | Dynamic programming using two rows | O(n × m) | O(m) |

The implementations use the same method signature:

`int distance(String a, String b)`

All implementations support empty strings and are tested against reference results.

### 2. String Search

Two algorithms find all occurrences of a pattern in a text, including overlapping matches.

| Algorithm | Approach | Time Complexity |
|---|---|---|
| Naive | Checks possible alignments one by one | O(n × m) worst case |
| KMP | Uses the Longest Proper Prefix which is also a Suffix (LPS) array | O(n + m) |

The KMP implementation includes a public `lps(String p)` method for preprocessing the pattern.

Both algorithms return matching starting indices in increasing order.

An empty pattern is rejected with `IllegalArgumentException`.

## Project Structure

```text
DAA4/
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── daa/
│   │               ├── Main.java
│   │               ├── editdistance/
│   │               ├── search/
│   │               ├── metrics/
│   │               └── experiment/
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── daa/
│
├── results/
│   ├── results.csv
│   └── plots/
│       ├── edit_distance_time.png
│       ├── edit_distance_memory.png
│       ├── recursive_edit_distance_time.png
│       ├── recursive_edit_distance_calls.png
│       ├── string_search_comparisons.png
│       ├── string_search_ratio.png
│       ├── fixed_pattern_comparisons.png
│       └── fixed_text_comparisons.png
│
├── scripts/
│   └── plot_results.py
│
├── README.md
├── REPORT.md
└── pom.xml
```

## Experimental Design

The benchmark uses a fixed random seed (`42`) to make generated inputs reproducible.

### Input sizes

| Experiment | Input sizes |
|---|---|
| Recursive Edit Distance | 2, 4, 6, 8, 10, 12 |
| Table and TwoRows | 100, 200, 400, 800 |
| String Search text | 2,000, 4,000, 8,000, 16,000 |
| String Search pattern | 20, 40, 80, 160 |

### Input types

**Edit Distance:**
- Identical strings
- Different strings
- Random strings

**String Search:**
- Best-case input
- Worst-case input
- Random input
- Fixed pattern with growing text
- Fixed text with growing pattern
- Overlapping matches

### Metrics

The benchmark collects:

- Median execution time in milliseconds
- Recursive calls
- Dynamic programming cells
- Character comparisons
- Maximum recursion depth
- Estimated auxiliary storage cells
- Text comparisons
- LPS preprocessing comparisons
- Number of matches returned

Each experiment uses five measured runs. The median is used to reduce the effect of timing fluctuations.

**Note:** Auxiliary storage is estimated in algorithmic cells. It is not a direct measurement of JVM heap memory in bytes.

## Experimental Results

The benchmark generated:

- 84 unique experiment configurations
- 5 measured runs per configuration
- 420 CSV data rows

The CSV file is located at:

`results/results.csv`

The generated plots are located in:

`results/plots/`

The experiments demonstrate that:

- Recursive Edit Distance becomes impractical quickly for different strings.
- TwoRows substantially reduces auxiliary storage compared with the full DP table.
- KMP performs significantly fewer character comparisons than Naive in difficult search cases.
- Naive can remain competitive on simple inputs because it does not require LPS preprocessing.
- Both search algorithms correctly support overlapping matches.

For detailed measurements, charts, predictions, and conclusions, see [REPORT.md](REPORT.md).

## How to Run the Project

### 1. Requirements

Install:

- JDK 17 or newer
- Maven
- Python 3
- Matplotlib

Install Matplotlib:

```bash
python -m pip install matplotlib
```

### 2. Run Unit Tests

From the project root directory:

```bash
mvn clean test
```

This compiles the project and executes the JUnit tests.

### 3. Run the Benchmark

Run the `com.daa.Main` class from IntelliJ IDEA or another Java IDE.

The benchmark executes the experiments and saves the collected data to:

```text
results/results.csv
```

Run the program with the project root as its working directory so that the relative output path is correct.

### 4. Generate Plots

After the benchmark has finished, run:

```bash
python scripts/plot_results.py
```

Alternatively, on Windows:

```bash
py scripts/plot_results.py
```

The script reads `results/results.csv` and generates eight PNG charts in `results/plots/`.

## Testing

JUnit tests cover:

- Reference Edit Distance values
- Empty-string cases
- Agreement between all three Edit Distance implementations
- Random short-string comparisons
- Reference LPS arrays
- Reference String Search results
- Overlapping matches
- Random text comparisons between Naive and KMP
- Empty-pattern exception handling
- Pattern longer than text

## How to Interpret the Results

Execution time alone is not sufficient to compare algorithms because JVM warm-up, garbage collection, CPU activity, and other system processes can influence timing.

Operation counters and auxiliary storage estimates provide additional evidence about algorithmic behavior.

The report discusses both theoretical complexity and measured performance.

## Author

**Course:** Design and Analysis of Algorithms  
**Assignment:** 4 — Dynamic Programming and String Algorithms
**Name:** Angsar Baibossinov
## AI Assistance Acknowledgement

AI assistance was used to support project development, debugging, documentation, and explanation of algorithmic concepts.