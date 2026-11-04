package com.daa;
import com.daa.metrics.Metrics;
import com.daa.search.KMP;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KMPTest {

    @Test
    void lpsReferenceValues() {

        KMP kmp = new KMP(new Metrics());

        assertArrayEquals(
                new int[]{0, 1, 2, 0, 1, 2, 3, 3},
                kmp.lps("AAACAAAA")
        );

        assertArrayEquals(
                new int[]{0, 0, 1, 2, 0, 1, 2, 3, 4},
                kmp.lps("ABABCABAB")
        );

        assertArrayEquals(
                new int[]{},
                kmp.lps("")
        );
    }
}
