package com.daa;
import com.daa.metrics.Metrics;
import com.daa.search.*;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StringSearchTest {

    @Test
    void referenceSearchValues() {

        StringSearch naive = new NaiveSearch(new Metrics());
        StringSearch kmp = new KMP(new Metrics());

        List<Integer> expected = List.of(10);

        assertEquals(expected,
                naive.search("ABABDABACDABABCABAB", "ABABCABAB"));

        assertEquals(expected,
                kmp.search("ABABDABACDABABCABAB", "ABABCABAB"));

        assertEquals(List.of(0, 1, 2, 3),
                naive.search("AAAAA", "AA"));

        assertEquals(List.of(0, 1, 2, 3),
                kmp.search("AAAAA", "AA"));

        assertEquals(List.of(0, 2, 4),
                naive.search("ABABABA", "ABA"));

        assertEquals(List.of(0, 2, 4),
                kmp.search("ABABABA", "ABA"));
    }

    @Test
    void emptyPatternMustThrowException() {

        StringSearch naive = new NaiveSearch(new Metrics());
        StringSearch kmp = new KMP(new Metrics());

        assertThrows(
                IllegalArgumentException.class,
                () -> naive.search("ABC", "")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> kmp.search("ABC", "")
        );
    }

    @Test
    void longerPatternMustReturnEmptyList() {

        StringSearch naive = new NaiveSearch(new Metrics());
        StringSearch kmp = new KMP(new Metrics());

        assertEquals(
                List.of(),
                naive.search("AB", "ABCD")
        );

        assertEquals(
                List.of(),
                kmp.search("AB", "ABCD")
        );
    }

    @Test
    void noMatchMustReturnEmptyList() {

        StringSearch naive = new NaiveSearch(new Metrics());
        StringSearch kmp = new KMP(new Metrics());

        assertEquals(
                List.of(),
                naive.search("AAAA", "BBB")
        );

        assertEquals(
                List.of(),
                kmp.search("AAAA", "BBB")
        );
    }
}
