# DAA Assignment 4 — Experimental Report
## Dynamic Programming and String Algorithms

## 1. Experiment Design Card

**Project scenario:** City Search & Spell-Check

**Objective:** Compare different algorithms for string similarity and pattern searching. The goal is to understand how their execution time, operation counts, and memory requirements change as input sizes increase.

### Experiment A — Edit Distance

**Research question:** How do recursive and dynamic programming implementations of Edit Distance differ in execution time and memory usage?

**Algorithms:**
- Recursive Edit Distance
- Table Edit Distance
- Two Rows Edit Distance

**Input types:**
- `identical` — two identical strings.
- `different` — strings containing different repeated characters.
- `random` — randomly generated strings.

**Input sizes:**
- Recursive: 2, 4, 6, 8, 10.
- Table and Two Rows: 100, 200, 400, 800.

**Metrics:**
- Execution time in milliseconds.
- Recursive calls.
- Dynamic programming cells.
- Character comparisons.
- Maximum recursion depth.
- Memory cells.

**Prediction:** The recursive implementation should become significantly slower as input length increases because it repeatedly solves overlapping subproblems. Table and Two Rows should have polynomial time complexity. Two Rows should use substantially less additional memory than the full table.

### Experiment B — String Searching

**Research question:** How does KMP compare with Naive Search when searching for patterns in different types of text?

**Algorithms:**
- Naive Search
- Knuth-Morris-Pratt (KMP)

**Input types:**
- `best_case` — text consists of B characters and the pattern consists of A characters.
- `worst_case` — text consists of A characters, while the pattern contains mostly A characters and ends with B.
- `random` — randomly generated text and patterns.

**Text and pattern sizes:**

| Text size | Pattern size |
|---:|---:|
| 2,000 | 20 |
| 4,000 | 40 |
| 8,000 | 80 |
| 16,000 | 160 |

**Metrics:**
- Execution time in milliseconds.
- Character comparisons.
- Text comparisons.
- LPS comparisons.
- Naive alignments tried.
- KMP fallback operations.

**Prediction:** Naive Search should perform poorly on the worst-case input because it repeatedly compares long prefixes. KMP should avoid many repeated comparisons by using the LPS array.

### Experimental procedure

- A fixed random seed (`42`) is used for reproducibility.
- Each experiment includes a warm-up run.
- Each measured configuration is executed five times.
- Median execution time is used to reduce the influence of occasional timing fluctuations.
- Operation counters are collected during algorithm execution.
- Results are saved in `results/results.csv`.

---

## 2. Algorithms and Complexity

### Edit Distance

Edit Distance calculates the minimum number of insertions, deletions, and substitutions needed to transform one string into another.

| Algorithm | Time complexity | Additional space |
|---|---|---|
| Recursive | Exponential in the worst case | O(n + m) recursion stack |
| Table | O(n × m) | O(n × m) |
| Two Rows | O(n × m) | O(m), when the shorter dimension is used for the row |

The recursive implementation directly explores possible operations without memoization. The Table implementation stores intermediate results in a two-dimensional array. Two Rows keeps only the current and previous DP rows.

### String Searching

| Algorithm | Preprocessing | Searching |
|---|---|---|
| Naive Search | O(1) | O(n × m) worst case |
| KMP | O(m) | O(n) |

Here, `n` is the text length and `m` is the pattern length.

KMP constructs an LPS (Longest Prefix Suffix) array. This array allows the algorithm to reuse information from previous comparisons instead of restarting from the beginning of the pattern.

---

## 3. Experimental Results

The benchmark data is stored in `results/results.csv`. The generated charts are stored in `results/plots/`.

### 3.1 Edit Distance Execution Time

![Edit Distance Execution Time](results/plots/edit_distance_time.png)

**Observation:** The chart compares the execution time of the Table and Two Rows implementations for different input sizes. Both implementations have O(n × m) time complexity. Their measured times may differ because of memory access patterns and runtime overhead.

**Takeaway:** Two Rows reduces memory usage while preserving the same asymptotic time complexity.

### 3.2 Edit Distance Memory Usage

![Edit Distance Memory](results/plots/edit_distance_memory.png)

**Observation:** Table stores the complete DP matrix, while Two Rows stores only two rows.

For two strings of length 800, the full table contains:

- Table: (800 + 1) × (800 + 1) = 641,601 cells.
- Two Rows: 2 × (800 + 1) = 1,602 cells.

**Takeaway:** Two Rows significantly reduces additional memory requirements.

### 3.3 String Search Character Comparisons

![String Search Comparisons](results/plots/string_search_comparisons.png)

**Observation:** This chart compares Naive Search and KMP on the worst-case search input using the recorded character comparison counter.

Naive Search can repeatedly compare a long prefix before discovering a mismatch. KMP uses LPS information to avoid unnecessary repeated comparisons.

**Takeaway:** KMP is expected to perform better on repetitive worst-case inputs.

### 3.4 String Search Comparison Ratio

![String Search Ratio](results/plots/string_search_ratio.png)

**Observation:** The ratio compares the number of character comparisons performed by Naive Search with the number performed by KMP.

A ratio greater than 1 means Naive Search performed more character comparisons.

**Takeaway:** The ratio makes the difference between the two algorithms easier to evaluate as input size increases.

### 3.5 Recursive Edit Distance Execution Time

![Recursive Edit Distance Time](results/plots/recursive_edit_distance_time.png)

**Observation:** The recursive implementation becomes increasingly expensive on different strings as input size grows. In the recorded experiment, the execution time for the different input of size 10 was approximately 43.8168 ms.

For identical strings, the implementation can finish much faster because matching characters do not require exploring all three edit operations.

**Takeaway:** Direct recursion without memoization is unsuitable for large Edit Distance inputs.

### 3.6 Recursive Edit Distance Calls

![Recursive Edit Distance Calls](results/plots/recursive_edit_distance_calls.png)

**Observation:** The number of recursive calls increases rapidly for different strings. This demonstrates the repeated exploration of overlapping subproblems.

**Takeaway:** The recursive call counter helps explain why the direct recursive implementation becomes slow.

---

## 4. Analysis and Discussion

### 4.1 Which string-search input was slowest, and why?

The most challenging input for Naive Search is the `worst_case` input. The text contains repeated A characters, and the pattern contains mostly A characters followed by B.

At many starting positions, the algorithm compares several characters successfully before finding a mismatch at the end of the pattern. It then moves to the next starting position and repeats many of the same comparisons.

KMP is designed to reduce this repeated work by using the LPS array.

### 4.2 What was the maximum input size handled by Recursive Edit Distance, and what stopped it?

The largest recursive input size used in the experiment was 10.

For different strings of length 10, the recorded execution time was approximately 43.8168 ms. The main limitation is the exponential growth of recursive calls without memoization.

The experiment used small inputs for Recursive Edit Distance because larger inputs can require a very large number of calls.

### 4.3 What did Two Rows gain and lose compared with the full Table?

Two Rows preserves O(n × m) time complexity but reduces additional memory from O(n × m) to O(m), assuming the shorter string is used for the row dimension.

For strings of length 800, the full table requires 641,601 cells, while Two Rows requires 1,602 cells.

The main disadvantage is that Two Rows does not preserve the entire DP matrix. Therefore, it is not suitable when the application needs to reconstruct the complete sequence of edit operations directly from the table.

### 4.4 Which search algorithm should a city search platform use?

KMP is a suitable choice for searching patterns in long text because it provides O(n + m) time complexity and avoids repeated comparisons.

This is useful when users search for street names or other text in long logs. For example, a platform may need to find a street name even when the text contains repetitive character sequences.

For a production spell-checking system, Edit Distance can additionally help identify similar names and possible spelling mistakes.

### 4.5 Why is execution time not the only evidence?

Execution time can be affected by JVM warm-up, garbage collection, operating system activity, and hardware differences.

Therefore, this project also records operation counters and memory usage. These metrics help explain why an algorithm performs differently, rather than showing only the final execution time.

Using a fixed random seed, warm-up, five measured runs, and median time makes the experiment more reproducible and reduces the influence of individual timing fluctuations.

---

## 5. Conclusion

This assignment demonstrates the practical differences between recursive and dynamic programming solutions, as well as between Naive Search and KMP.

The recursive Edit Distance implementation is useful for understanding the recurrence relation, but its exponential growth makes it unsuitable for larger inputs. The Table implementation provides a complete DP matrix, while Two Rows reduces memory usage without changing the asymptotic running time.

For string searching, KMP is more appropriate for long and repetitive text because it uses preprocessing information to avoid repeated comparisons.

The experiments combine execution time, operation counters, and memory measurements to provide a more complete algorithm comparison.