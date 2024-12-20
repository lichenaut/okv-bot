package com.lichenaut.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BadWord {
    EXACT("mongo"),
    EXACT_2("lame"),
    EXACT_3("schizo"),
    EXACT_4("psycho"),
    SUB("crazy"),
    SUB_2("cretin"),
    SUB_3("degenera"),
    SUB_4("dumb"),
    SUB_5("fool"),
    SUB_6("freak"),
    SUB_7("hysteri"),
    SUB_8("idiot"),
    SUB_9("imbecile"),
    SUB_10("insane"),
    SUB_11("moron"),
    SUB_12("reeeee"),
    SUB_13("retard"),
    SUB_14("scum"),
    SUB_15("sociopath"),
    SUB_16("spastic"),
    SUB_17("spaz"),
    SUB_18("stupid");

    private final String word;

    /**
     * Retrieves an array of exact bad words.
     *
     * @return an array of exact bad words as strings.
     */
    public static String[] getExactWords() {
        return new String[] {
                EXACT.word, EXACT_2.word, EXACT_3.word, EXACT_4.word
        };
    }

    /**
     * Retrieves an array of sub-strings of bad words.
     *
     * @return an array of sub-string bad words as strings.
     */
    public static String[] getSubWords() {
        return new String[] {
                SUB.word, SUB_2.word, SUB_3.word, SUB_4.word, SUB_5.word,
                SUB_6.word, SUB_7.word, SUB_8.word, SUB_9.word,
                SUB_10.word, SUB_11.word, SUB_12.word, SUB_13.word,
                SUB_14.word, SUB_15.word, SUB_16.word, SUB_17.word, SUB_18.word
        };
    }
}
