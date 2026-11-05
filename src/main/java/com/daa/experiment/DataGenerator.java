package com.daa.experiment;
import java.util.Random;

public class DataGenerator {

    private static final long SEED = 42L;

    private final Random random;

    public DataGenerator() {
        random = new Random(SEED);
    }

    // Generates a random string using uppercase English letters.
    public String randomString(int length) {

        StringBuilder result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            char character = (char) ('A' + random.nextInt(26));
            result.append(character);
        }

        return result.toString();
    }

    // Generates a string containing one repeated character.
    public String repeatedString(int length, char character) {
        return String.valueOf(character).repeat(length);
    }

    // Best Case: the first character does not match.
    public String bestCaseText(int length) {
        return repeatedString(length, 'B');
    }

    public String bestCasePattern(int length) {
        return repeatedString(length, 'A');
    }

    // Worst Case for Naive: many characters match before a mismatch.
    public String worstCaseText(int length) {
        return repeatedString(length, 'A');
    }

    public String worstCasePattern(int length) {

        if (length < 2) {
            throw new IllegalArgumentException(
                    "Pattern length must be at least 2"
            );
        }

        return repeatedString(length - 1, 'A') + "B";
    }

    // Generates identical strings for Edit Distance.
    public String identicalString(int length) {
        return repeatedString(length, 'A');
    }

    // Generates different strings for Edit Distance.
    public String differentString(int length) {
        return repeatedString(length, 'B');
    }
}
