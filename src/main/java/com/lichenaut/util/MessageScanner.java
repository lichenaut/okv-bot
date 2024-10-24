package com.lichenaut.util;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MessageScanner {

    /**
     * Scans for exact bad words in the message.
     *
     * @param message The message to scan.
     * @return A list of all exact bad words found, or null if none found.
     */
    @Nullable
    public List<String> exactScan(String message) {
        List<String> foundWords = new ArrayList<>();
        String[] words = message.toLowerCase().split("\\s+");

        for (String word : words) {
            for (String exact : BadWord.getExactWords()) {
                if (word.equals(exact)) {
                    foundWords.add(exact);
                }
            }
        }

        return foundWords.isEmpty() ? null : foundWords;
    }

    /**
     * Scans for sub-strings of bad words in the message.
     *
     * @param message The message to scan.
     * @return A list of all sub-string bad words found, or null if none found.
     */
    @Nullable
    public List<String> subScan(String message) {
        List<String> foundWords = new ArrayList<>();
        String lowerMessage = message.toLowerCase().replaceAll("[^a-z]", "");

        for (String subWord : BadWord.getSubWords()) {
            if (lowerMessage.contains(subWord)) {
                foundWords.add(subWord);
            }
        }

        return foundWords.isEmpty() ? null : foundWords;
    }
}
