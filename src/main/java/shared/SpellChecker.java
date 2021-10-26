package shared;

import static java.lang.Math.min;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record SpellChecker(DictionaryManager dictionaryManager) {
    private static final int EDIT_DISTANCE_THRESHOLD = 3;

    /**
     * This will assume that the word parameter has some spelling error at the back of the word.
     * Any spelling error like 'apple' to '*apple' will not be detected.
     */
    public List<String> spellCorrecting(String word) {
        // removing the same number of edit distance threshold in last letters of the word
        final var head = word.substring(0, word.length() - EDIT_DISTANCE_THRESHOLD);

        // get 100 likely words
        final var dictionary = dictionaryManager.searchKeyWord(head, 100);

        return suggestCorrections(dictionary, word);
    }

    /*
    Should be used with one word.
    This will linearly iterate over all word in dictionary and come with word that satisfied certain
    edit distance threshold.
     */
    private static List<String> suggestCorrections(List<String> dictionary, String word) {
        return dictionary.stream()
                .filter(w -> calculateEditDistance(word, w) <= EDIT_DISTANCE_THRESHOLD)
                .collect(Collectors.toList());
    }

    /**
    This function calculate Levenshtein distance using dynamic programming.
    Levenshtein distance is the minimum number of basic operations like add, delete or substitute
    for a string A to be string B.

    Formula
    lev(a, b) = max(a, b) if min(a, b) = 1
    lev(a, b) = lev(a-1, b-1) if last A == last B
    lev(a, b) = min(lev(a-1, b), lev(a, b-1), lev(a-1, b-1)) + 1

    In this formula a can refer to string a or length of string b.
     */
    public static int calculateEditDistance(String a, String b) {
        // get length of string a and b
        final var aSize = a.length();
        final var bSize = b.length();

        // create a distance table to store calculation, the size of this
        // is length a + 1 and length b + 1 because position (0, 0) is used
        // for empty substring
        // 100 is the default value, there are no word 100 letters long, so it
        // will not be a problem
        final var distance = new int[aSize + 1][bSize + 1];
        for (var row : distance) {
            Arrays.fill(row, 100);
        }

        // fill lev(a, b) = max(a, b) if min(a, b) = 1 part, this is actually the top
        // and left margin of the table
        distance[0][0] = 0;

        for (int i = 1; i <= aSize; i++) {
            distance[i][0] = i;
        }

        for (int i = 1; i <= bSize; i++) {
            distance[0][i] = i;
        }

        // start calculating distance
        // now table look like something like this
        // _ _ a b c
        // _ 0 1 2 3
        // a 1
        // b 2
        // c 3
        for (int i = 1; i <= aSize; i++) {
            for (int j = 1; j <= bSize; j++) {
                // a[1] and table[1] is actually refer to different character
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    distance[i][j] = distance[i - 1][j - 1];
                } else {
                    final var lessA = distance[i - 1][j];
                    final var lessB = distance[i][j - 1];
                    final var lessBoth = distance[i - 1][j - 1];

                    distance[i][j] = min(lessA, min(lessB, lessBoth)) + 1;
                }
            }
        }

        return distance[aSize][bSize];
    }
}
