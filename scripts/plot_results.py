import csv
from pathlib import Path
from collections import defaultdict

import matplotlib.pyplot as plt


# Project paths
BASE_DIR = Path(__file__).resolve().parent.parent

CSV_PATH = BASE_DIR / "results" / "results.csv"
PLOTS_DIR = BASE_DIR / "results" / "plots"

PLOTS_DIR.mkdir(parents=True, exist_ok=True)

print("Project directory:", BASE_DIR)
print("CSV file:", CSV_PATH)

PLOTS_DIR.mkdir(parents=True, exist_ok=True)


# Read CSV data
with CSV_PATH.open("r", newline="", encoding="utf-8") as file:
    rows = list(csv.DictReader(file))


# Keep one representative row for each experiment.
# Counters are deterministic, and median_time_ms is already calculated.
experiments = {}

for row in rows:
    key = (
        row["algorithm"],
        row["input"],
        int(row["n"]),
        int(row["m"])
    )

    if key not in experiments:
        experiments[key] = row


def get_experiments(algorithm, input_type):
    result = []

    for row in experiments.values():
        if row["algorithm"] == algorithm and row["input"] == input_type:
            result.append(row)

    return sorted(result, key=lambda item: int(item["n"]))


def save_plot(filename):
    plt.tight_layout()
    plt.savefig(PLOTS_DIR / filename, dpi=300)
    plt.close()


# -------------------------------------------------
# PLOT 1: EDIT DISTANCE EXECUTION TIME
# -------------------------------------------------

plt.figure(figsize=(9, 5))

for algorithm in ["Table", "TwoRows"]:

    data = get_experiments(algorithm, "different")

    sizes = [int(row["n"]) for row in data]
    times = [float(row["median_time_ms"]) for row in data]

    plt.plot(sizes, times, marker="o", label=algorithm)

plt.title("Edit Distance: Execution Time")
plt.xlabel("String length (characters)")
plt.ylabel("Median execution time (ms)")
plt.legend()
plt.grid(True)

save_plot("edit_distance_time.png")


# -------------------------------------------------
# PLOT 2: EDIT DISTANCE MEMORY USAGE
# -------------------------------------------------

plt.figure(figsize=(9, 5))

for algorithm in ["Table", "TwoRows"]:

    data = get_experiments(algorithm, "different")

    sizes = [int(row["n"]) for row in data]
    memory = [int(row["memory_cells"]) for row in data]

    plt.plot(sizes, memory, marker="o", label=algorithm)

plt.title("Edit Distance: Memory Usage")
plt.xlabel("String length (characters)")
plt.ylabel("Memory cells")
plt.legend()
plt.grid(True)

save_plot("edit_distance_memory.png")


# -------------------------------------------------
# PLOT 3: STRING SEARCH COMPARISONS
# -------------------------------------------------

plt.figure(figsize=(9, 5))

for algorithm in ["Naive", "KMP"]:

    data = get_experiments(algorithm, "worst_case")

    sizes = [int(row["n"]) for row in data]

    comparisons = [
        int(row["text_comparisons"]) + int(row["lps_comparisons"])
        for row in data
    ]

    plt.plot(sizes, comparisons, marker="o", label=algorithm)

plt.title("String Search: Character Comparisons (Worst Case)")
plt.xlabel("Text length (characters)")
plt.ylabel("Total character comparisons")
plt.legend()
plt.grid(True)

save_plot("string_search_comparisons.png")


# -------------------------------------------------
# PLOT 4: NAIVE / KMP COMPARISON RATIO
# -------------------------------------------------

naive_data = get_experiments("Naive", "worst_case")
kmp_data = get_experiments("KMP", "worst_case")

naive_by_size = {
    int(row["n"]):
        int(row["text_comparisons"]) + int(row["lps_comparisons"])
    for row in naive_data
}

kmp_by_size = {
    int(row["n"]):
        int(row["text_comparisons"]) + int(row["lps_comparisons"])
    for row in kmp_data
}

sizes = sorted(set(naive_by_size) & set(kmp_by_size))

ratios = [
    naive_by_size[size] / kmp_by_size[size]
    if kmp_by_size[size] != 0 else 0
    for size in sizes
]

plt.figure(figsize=(9, 5))

plt.plot(sizes, ratios, marker="o", label="Naive / KMP")

plt.title("String Search: Comparison Ratio (Worst Case)")
plt.xlabel("Text length (characters)")
plt.ylabel("Naive comparisons / KMP comparisons")
plt.yscale("log")
plt.legend()
plt.grid(True, which="both")

save_plot("string_search_ratio.png")

# -------------------------------------------------
# PLOT 5: RECURSIVE EDIT DISTANCE EXECUTION TIME
# -------------------------------------------------

plt.figure(figsize=(9, 5))

for input_type in ["identical", "different"]:

    data = get_experiments("Recursive", input_type)

    sizes = [int(row["n"]) for row in data]
    times = [float(row["median_time_ms"]) for row in data]

    plt.plot(
        sizes,
        times,
        marker="o",
        label=input_type
    )

plt.title("Recursive Edit Distance: Execution Time")
plt.xlabel("String length (characters)")
plt.ylabel("Median execution time (ms)")
plt.yscale("log")
plt.legend()
plt.grid(True, which="both")

save_plot("recursive_edit_distance_time.png")

print("All plots were generated successfully!")

for path in sorted(PLOTS_DIR.glob("*.png")):
    print(path)
