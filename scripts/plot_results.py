
import csv
import math
from pathlib import Path

import matplotlib.pyplot as plt


# --------------------------------------------------
# Paths
# --------------------------------------------------

BASE_DIR = Path(__file__).resolve().parent.parent
CSV_PATH = BASE_DIR / "results" / "results.csv"
PLOTS_DIR = BASE_DIR / "results" / "plots"


# --------------------------------------------------
# Load CSV
# --------------------------------------------------

def load_results():
    if not CSV_PATH.exists():
        raise FileNotFoundError(
            f"CSV file not found: {CSV_PATH}"
        )

    experiments = {}

    with open(CSV_PATH, "r", newline="", encoding="utf-8") as file:
        reader = csv.DictReader(file)

        for row in reader:
            key = (
                row["algorithm"],
                row["input"],
                int(row["n"]),
                int(row["m"])
            )

            # Each experiment has 5 runs.
            # median_time_ms and counters are the same
            # for all rows of one experiment.
            if key not in experiments:
                experiments[key] = {
                    "algorithm": row["algorithm"],
                    "input": row["input"],
                    "n": int(row["n"]),
                    "m": int(row["m"]),
                    "time_ms": float(row["median_time_ms"]),
                    "char_comparisons": int(row["char_comparisons"]),
                    "memory_cells": int(row["memory_cells"]),
                    "recursive_calls": int(row["recursive_calls"]),
                    "result": int(row["result"])
                }

    return list(experiments.values())


# --------------------------------------------------
# Helpers
# --------------------------------------------------

def get_experiments(data, algorithm, input_type, sort_by="n"):
    selected = [
        item for item in data
        if item["algorithm"] == algorithm
           and item["input"] == input_type
    ]

    return sorted(selected, key=lambda item: item[sort_by])


def require_data(items, description):
    if not items:
        raise ValueError(
            f"No data found for: {description}. "
            "Check the algorithm and input names in results.csv."
        )


def save_plot(filename):
    output_path = PLOTS_DIR / filename
    plt.tight_layout()
    plt.savefig(output_path, dpi=200)
    plt.close()
    print(f"Saved: {output_path}")


def prepare_plot(title, x_label, y_label):
    plt.figure(figsize=(9, 5))
    plt.title(title)
    plt.xlabel(x_label)
    plt.ylabel(y_label)
    plt.grid(True, which="both", linestyle="--", alpha=0.5)


# --------------------------------------------------
# Plot 1: Edit Distance execution time
# --------------------------------------------------

def plot_edit_distance_time(data):
    table = get_experiments(data, "Table", "different")
    two_rows = get_experiments(data, "TwoRows", "different")

    require_data(table, "Table / different")
    require_data(two_rows, "TwoRows / different")

    prepare_plot(
        "Edit Distance: Execution Time",
        "String length (n)",
        "Median execution time (ms)"
    )

    plt.plot(
        [x["n"] for x in table],
        [x["time_ms"] for x in table],
        marker="o",
        label="Table"
    )

    plt.plot(
        [x["n"] for x in two_rows],
        [x["time_ms"] for x in two_rows],
        marker="s",
        label="Two Rows"
    )

    plt.xscale("log", base=2)
    plt.yscale("log")
    plt.legend()

    save_plot("edit_distance_time.png")


# --------------------------------------------------
# Plot 2: Edit Distance auxiliary storage
# --------------------------------------------------

def plot_edit_distance_memory(data):
    table = get_experiments(data, "Table", "different")
    two_rows = get_experiments(data, "TwoRows", "different")

    require_data(table, "Table memory")
    require_data(two_rows, "TwoRows memory")

    prepare_plot(
        "Edit Distance: Auxiliary Storage",
        "String length (n)",
        "Estimated auxiliary storage (cells)"
    )

    plt.plot(
        [x["n"] for x in table],
        [x["memory_cells"] for x in table],
        marker="o",
        label="Table"
    )

    plt.plot(
        [x["n"] for x in two_rows],
        [x["memory_cells"] for x in two_rows],
        marker="s",
        label="Two Rows"
    )

    plt.xscale("log", base=2)
    plt.yscale("log")
    plt.legend()

    save_plot("edit_distance_memory.png")


# --------------------------------------------------
# Plot 3: Recursive Edit Distance execution time
# --------------------------------------------------

def plot_recursive_time(data):
    identical = get_experiments(data, "Recursive", "identical")
    different = get_experiments(data, "Recursive", "different")

    require_data(identical, "Recursive / identical")
    require_data(different, "Recursive / different")

    prepare_plot(
        "Recursive Edit Distance: Execution Time",
        "String length (n)",
        "Median execution time (ms)"
    )

    plt.plot(
        [x["n"] for x in identical],
        [x["time_ms"] for x in identical],
        marker="o",
        label="Identical strings"
    )

    plt.plot(
        [x["n"] for x in different],
        [x["time_ms"] for x in different],
        marker="s",
        label="Different strings"
    )

    plt.yscale("log")
    plt.legend()

    save_plot("recursive_edit_distance_time.png")


# --------------------------------------------------
# Plot 4: Recursive calls
# --------------------------------------------------

def plot_recursive_calls(data):
    identical = get_experiments(data, "Recursive", "identical")
    different = get_experiments(data, "Recursive", "different")

    require_data(identical, "Recursive calls / identical")
    require_data(different, "Recursive calls / different")

    prepare_plot(
        "Recursive Edit Distance: Number of Calls",
        "String length (n)",
        "Recursive calls"
    )

    plt.plot(
        [x["n"] for x in identical],
        [x["recursive_calls"] for x in identical],
        marker="o",
        label="Identical strings"
    )

    plt.plot(
        [x["n"] for x in different],
        [x["recursive_calls"] for x in different],
        marker="s",
        label="Different strings"
    )

    plt.yscale("log")
    plt.legend()

    save_plot("recursive_edit_distance_calls.png")


# --------------------------------------------------
# Plot 5: String Search character comparisons
# --------------------------------------------------

def plot_search_comparisons(data):
    naive = get_experiments(data, "Naive", "worst_case")
    kmp = get_experiments(data, "KMP", "worst_case")

    require_data(naive, "Naive / worst_case")
    require_data(kmp, "KMP / worst_case")

    prepare_plot(
        "String Search: Character Comparisons",
        "Text length (n)",
        "Character comparisons"
    )

    plt.plot(
        [x["n"] for x in naive],
        [x["char_comparisons"] for x in naive],
        marker="o",
        label="Naive"
    )

    plt.plot(
        [x["n"] for x in kmp],
        [x["char_comparisons"] for x in kmp],
        marker="s",
        label="KMP"
    )

    plt.xscale("log", base=2)
    plt.yscale("log")
    plt.legend()

    save_plot("string_search_comparisons.png")


# --------------------------------------------------
# Plot 6: Naive / KMP comparison ratio
# --------------------------------------------------

def plot_search_ratio(data):
    naive = get_experiments(data, "Naive", "worst_case")
    kmp = get_experiments(data, "KMP", "worst_case")

    require_data(naive, "Naive ratio")
    require_data(kmp, "KMP ratio")

    naive_by_size = {x["n"]: x for x in naive}
    kmp_by_size = {x["n"]: x for x in kmp}

    sizes = sorted(set(naive_by_size) & set(kmp_by_size))

    ratios = []

    for size in sizes:
        naive_count = naive_by_size[size]["char_comparisons"]
        kmp_count = kmp_by_size[size]["char_comparisons"]

        if kmp_count == 0:
            continue

        ratios.append(naive_count / kmp_count)

    sizes = [
        size for size in sizes
        if kmp_by_size[size]["char_comparisons"] != 0
    ]

    if not ratios:
        raise ValueError("Cannot calculate Naive / KMP comparison ratio.")

    prepare_plot(
        "String Search: Naive / KMP Comparison Ratio",
        "Text length (n)",
        "Naive comparisons / KMP comparisons"
    )

    plt.plot(sizes, ratios, marker="o", label="Comparison ratio")
    plt.axhline(y=1, linestyle="--", label="Equal number of comparisons")

    plt.xscale("log", base=2)
    plt.yscale("log")
    plt.legend()

    save_plot("string_search_ratio.png")


# --------------------------------------------------
# Plot 7: Fixed pattern, growing text
# --------------------------------------------------

def plot_fixed_pattern(data):
    naive = get_experiments(data, "Naive", "fixed_m_single_match")
    kmp = get_experiments(data, "KMP", "fixed_m_single_match")

    require_data(naive, "Naive / fixed_m_single_match")
    require_data(kmp, "KMP / fixed_m_single_match")

    naive.sort(key=lambda x: x["n"])
    kmp.sort(key=lambda x: x["n"])

    prepare_plot(
        "String Search: Fixed Pattern, Growing Text",
        "Text length (n), pattern length (m) = 40",
        "Character comparisons"
    )

    plt.plot(
        [x["n"] for x in naive],
        [x["char_comparisons"] for x in naive],
        marker="o",
        label="Naive"
    )

    plt.plot(
        [x["n"] for x in kmp],
        [x["char_comparisons"] for x in kmp],
        marker="s",
        label="KMP"
    )

    plt.xscale("log", base=2)
    plt.yscale("log")
    plt.legend()

    save_plot("fixed_pattern_comparisons.png")


# --------------------------------------------------
# Plot 8: Fixed text, growing pattern
# --------------------------------------------------

def plot_fixed_text(data):
    naive = get_experiments(data, "Naive", "fixed_n_two_matches", sort_by="m")
    kmp = get_experiments(data, "KMP", "fixed_n_two_matches", sort_by="m")

    require_data(naive, "Naive / fixed_n_two_matches")
    require_data(kmp, "KMP / fixed_n_two_matches")

    naive.sort(key=lambda x: x["m"])
    kmp.sort(key=lambda x: x["m"])

    prepare_plot(
        "String Search: Fixed Text, Growing Pattern",
        "Pattern length (m), text length (n) = 16000",
        "Character comparisons"
    )

    plt.plot(
        [x["m"] for x in naive],
        [x["char_comparisons"] for x in naive],
        marker="o",
        label="Naive"
    )

    plt.plot(
        [x["m"] for x in kmp],
        [x["char_comparisons"] for x in kmp],
        marker="s",
        label="KMP"
    )

    plt.xscale("log", base=2)
    plt.yscale("log")
    plt.legend()

    save_plot("fixed_text_comparisons.png")


# --------------------------------------------------
# Main
# --------------------------------------------------

def main():
    PLOTS_DIR.mkdir(parents=True, exist_ok=True)

    # Remove previous generated plots so the folder
    # contains only charts from the current CSV.
    for old_plot in PLOTS_DIR.glob("*.png"):
        old_plot.unlink()

    data = load_results()

    print(f"Loaded {len(data)} unique experiments from CSV.")

    plot_edit_distance_time(data)
    plot_edit_distance_memory(data)
    plot_recursive_time(data)
    plot_recursive_calls(data)
    plot_search_comparisons(data)
    plot_search_ratio(data)
    plot_fixed_pattern(data)
    plot_fixed_text(data)

    print("\nAll 8 plots have been generated successfully.")


if __name__ == "__main__":
    main()
