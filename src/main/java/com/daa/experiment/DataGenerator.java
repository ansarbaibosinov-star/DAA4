
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
        if (length < 0) {
            throw new IllegalArgumentException("Length cannot be negative");
        }

        StringBuilder result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            char character = (char) ('A' + random.nextInt(26));
            result.append(character);
        }

        return result.toString();
    }

    // Generates a string containing one repeated character.
    public String repeatedString(int length, char character) {
        if (length < 0) {
            throw new IllegalArgumentException("Length cannot be negative");
        }

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

    // Generates text containing one occurrence of the pattern.
    public String textWithMatch(int textLength, String pattern) {
        if (textLength < 0 || pattern == null) {
            throw new IllegalArgumentException("Invalid input");
        }

        if (pattern.length() > textLength) {
            throw new IllegalArgumentException(
                    "Pattern cannot be longer than text"
            );
        }

        StringBuilder text = new StringBuilder(
                repeatedString(textLength, '#')
        );

        int position = (textLength - pattern.length()) / 2;

        text.replace(
                position,
                position + pattern.length(),
                pattern
        );

        return text.toString();
    }

    // Generates text containing several occurrences of the pattern.
    public String textWithMultipleMatches(
            int textLength,
            String pattern
    ) {
        if (textLength < 0 || pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("Invalid input");
        }

        if (pattern.length() > textLength) {
            throw new IllegalArgumentException(
                    "Pattern cannot be longer than text"
            );
        }

        StringBuilder text = new StringBuilder(
                repeatedString(textLength, '#')
        );

        int firstPosition = 0;
        int lastPosition = textLength - pattern.length();

        text.replace(
                firstPosition,
                firstPosition + pattern.length(),
                pattern
        );

        if (lastPosition != firstPosition) {
            text.replace(
                    lastPosition,
                    lastPosition + pattern.length(),
                    pattern
            );
        }

        return text.toString();
    }
}
