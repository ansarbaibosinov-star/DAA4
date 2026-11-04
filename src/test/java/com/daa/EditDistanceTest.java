package com.daa;
import com.daa.editdistance.*;
import com.daa.metrics.Metrics;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class EditDistanceTest {

    @Test
    void referenceValues() {

        EditDistance recursive =
                new RecursiveEditDistance(new Metrics());

        EditDistance table =
                new TableEditDistance(new Metrics());

        EditDistance twoRows =
                new TwoRowsEditDistance(new Metrics());

        EditDistance[] algorithms = {
                recursive, table, twoRows
        };

        for (EditDistance algorithm : algorithms) {

            assertEquals(3, algorithm.distance("kitten", "sitting"));
            assertEquals(2, algorithm.distance("flaw", "lawn"));
            assertEquals(5, algorithm.distance("intention", "execution"));

            assertEquals(3, algorithm.distance("", "abc"));
            assertEquals(0, algorithm.distance("abc", "abc"));
        }
    }

    @Test
    void allVersionsMustReturnSameResultOnRandomStrings() {

        EditDistance recursive =
                new RecursiveEditDistance(new Metrics());

        EditDistance table =
                new TableEditDistance(new Metrics());

        EditDistance twoRows =
                new TwoRowsEditDistance(new Metrics());

        Random random = new Random(42);

        String alphabet = "ABC";

        for (int test = 0; test < 100; test++) {

            String a = randomString(random, alphabet);
            String b = randomString(random, alphabet);

            int expected = table.distance(a, b);

            assertEquals(expected, twoRows.distance(a, b));
            assertEquals(expected, recursive.distance(a, b));
        }
    }

    private String randomString(Random random, String alphabet) {

        int length = random.nextInt(7);

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            result.append(alphabet.charAt(
                    random.nextInt(alphabet.length())
            ));
        }

        return result.toString();
    }
}
