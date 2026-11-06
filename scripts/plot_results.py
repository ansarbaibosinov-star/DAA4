import csv
from pathlib import Path

import matplotlib.pyplot as plt


# -------------------------------------------------
# PROJECT PATHS
# -------------------------------------------------

BASE_DIR = Path(__file__).resolve().parent.parent

CSV_PATH = BASE_DIR / "results" / "results.csv"
PLOTS_DIR = BASE_DIR / "results" / "plots"

PLOTS_DIR.mkdir(parents=True, exist_ok=True)

print("Project directory:", BASE_DIR)
print("CSV file:", CSV_PATH)


# -------------------------------------------------
# READ CSV
# -------------------------------------------------

if not CSV_PATH.exists():
    raise FileNotFoundError(
        f"Cannot find results.csv: {CSV_PATH}. "
        "Run the Java Benchmark first."
    )

with CSV_PATH.open("r", newline="", encoding="utf-8") as file:
    rows = list(csv.DictReader(file))


# Keep one row for each experiment.
# Median time is identical in all five run rows.
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
        if (
                row["algorithm"] == algorithm
                and row["input"] == input_type
        ):
            result.append(row)

    return sorted(result, key=lambda item: int(item["n"]))


# -------------------------------------------------
# AXIS CONFIGURATION
# -------------------------------------------------

def configure_axes(ax, sizes):
    # Input sizes grow geometrically where applicable.
    ax.set_xscale("log", base=2)

    # Logarithmic Y-axis makes differences across
    # several orders of magnitude easier to compare.
    ax.set_yscale("log")

    # Show the actual input sizes as tick labels.
    ax.set_xticks(sizes)
    ax.set_xticklabels([str(size) for size in sizes])

    ax.grid(True, which="both", linestyle="--", alpha=0.6)


def save_plot(filename):
    plt.tight_layout()
    output = PLOTS_DIR / filename
    plt.savefig(output, dpi=300)
    plt.close()

    print("Saved:", output)


# -------------------------------------------------
# PLOT 1: EDIT DISTANCE EXECUTION TIME
# -------------------------------------------------

plt.figure(figsize=(9, 5))
ax = plt.gca()

all_sizes = []

for algorithm in ["Table", "TwoRows"]:
    data = get_experiments(algorithm, "different")

    sizes = [int(row["n"]) for row in data]
    times = [float(row["median_time_ms"]) for row in data]

    all_sizes.extend(sizes)

    ax.plot(
        sizes,
        times,
        marker="o",
        linewidth=2,
        label=algorithm
    )

ax.set_title("Edit Distance: Execution Time")
ax.set_xlabel("String length (characters)")
ax.set_ylabel("Median execution time (ms)")

configure_axes(ax, sorted(set(all_sizes)))
ax.legend()

save_plot("edit_distance_time.png")


# -------------------------------------------------
# PLOT 2: EDIT DISTANCE MEMORY USAGE
# -------------------------------------------------

plt.figure(figsize=(9, 5))
ax = plt.gca()

all_sizes = []

for algorithm in ["Table", "TwoRows"]:
    data = get_experiments(algorithm, "different")

    sizes = [int(row["n"]) for row in data]
    memory = [int(row["memory_cells"]) for row in data]

    all_sizes.extend(sizes)

    ax.plot(
        sizes,
        memory,
        marker="o",
        linewidth=2,
        label=algorithm
    )

ax.set_title("Edit Distance: Additional Memory")
ax.set_xlabel("String length (characters)")
ax.set_ylabel("Additional memory (cells)")

configure_axes(ax, sorted(set(all_sizes)))
ax.legend()

save_plot("edit_distance_memory.png")


# -------------------------------------------------
# PLOT 3: STRING SEARCH COMPARISONS
# -------------------------------------------------

plt.figure(figsize=(9, 5))
ax = plt.gca()

all_sizes = []

for algorithm in ["Naive", "KMP"]:
    data = get_experiments(algorithm, "worst_case")

    sizes = [int(row["n"]) for row in data]

    # char_comparisons includes:
    # - text comparisons
    # - LPS comparisons for KMP
    comparisons = [
        int(row["char_comparisons"])
        for row in data
    ]

    all_sizes.extend(sizes)

    ax.plot(
        sizes,
        comparisons,
        marker="o",
        linewidth=2,
        label=algorithm
    )

ax.set_title("String Search: Total Character Comparisons")
ax.set_xlabel("Text length (characters)")
ax.set_ylabel("Total character comparisons")

configure_axes(ax, sorted(set(all_sizes)))
ax.legend()

save_plot("string_search_comparisons.png")


# -------------------------------------------------
# PLOT 4: NAIVE / KMP COMPARISON RATIO
# -------------------------------------------------

naive_data = get_experiments("Naive", "worst_case")
kmp_data = get_experiments("KMP", "worst_case")

naive_by_size = {
    int(row["n"]): int(row["char_comparisons"])
    for row in naive_data
}

kmp_by_size = {
    int(row["n"]): int(row["char_comparisons"])
    for row in kmp_data
}

sizes = sorted(set(naive_by_size) & set(kmp_by_size))

ratios = [
    naive_by_size[size] / kmp_by_size[size]
    for size in sizes
    if kmp_by_size[size] > 0
]

ratio_sizes = [
    size for size in sizes
    if kmp_by_size[size] > 0
]

plt.figure(figsize=(9, 5))
ax = plt.gca()

ax.plot(
    ratio_sizes,
    ratios,
    marker="o",
    linewidth=2,
    label="Naive / KMP"
)

ax.set_title("String Search: Comparison Ratio (Worst Case)")
ax.set_xlabel("Text length (characters)")
ax.set_ylabel("Naive / KMP comparison ratio")

configure_axes(ax, ratio_sizes)
ax.legend()

save_plot("string_search_ratio.png")


# -------------------------------------------------
# PLOT 5: RECURSIVE EDIT DISTANCE EXECUTION TIME
# -------------------------------------------------

plt.figure(figsize=(9, 5))
ax = plt.gca()

all_sizes = []

for input_type in ["identical", "different"]:
    data = get_experiments("Recursive", input_type)

    sizes = [int(row["n"]) for row in data]
    times = [float(row["median_time_ms"]) for row in data]

    all_sizes.extend(sizes)

    ax.plot(
        sizes,
        times,
        marker="o",
        linewidth=2,
        label=input_type
    )

ax.set_title("Recursive Edit Distance: Execution Time")
ax.set_xlabel("String length (characters)")
ax.set_ylabel("Median execution time (ms)")

configure_axes(ax, sorted(set(all_sizes)))
ax.legend()

save_plot("recursive_edit_distance_time.png")


# -------------------------------------------------
# PLOT 6: RECURSIVE CALLS
# -------------------------------------------------

plt.figure(figsize=(9, 5))
ax = plt.gca()

all_sizes = []

for input_type in ["identical", "different"]:
    data = get_experiments("Recursive", input_type)

    sizes = [int(row["n"]) for row in data]

    calls = [
        int(row["recursive_calls"])
        for row in data
    ]

    all_sizes.extend(sizes)

    ax.plot(
        sizes,
        calls,
        marker="o",
        linewidth=2,
        label=input_type
    )

ax.set_title("Recursive Edit Distance: Recursive Calls")
ax.set_xlabel("String length (characters)")
ax.set_ylabel("Number of recursive calls")

configure_axes(ax, sorted(set(all_sizes)))
ax.legend()

save_plot("recursive_edit_distance_calls.png")


# -------------------------------------------------
# FINISHED
# -------------------------------------------------

print()
print("All plots were generated successfully!")
print("Total experiments:", len(experiments))
print("Generated plots:")

for path in sorted(PLOTS_DIR.glob("*.png")):
    print(path.name)
