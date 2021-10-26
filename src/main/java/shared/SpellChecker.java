package shared;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.*;

public record SpellChecker(DictionaryManager dictionaryManager) {
    private static final int EDIT_DISTANCE_THRESHOLD = 3;
    private static final int GENERATE_WINDOW = 3; // 1/3 of the word;

    /**
     * This will assume that the word parameter has some spelling error at the back of the word.
     * Any spelling error like 'apple' to '*apple' will not be detected.
     */
    public List<String> correctSpelling(String word) {
        final var cutWindowSize = (int)round(floor(1.0 * word.length() / GENERATE_WINDOW));

        // create a list of query for possible words
        final var cutPosition = IntStream.rangeClosed(0, word.length() - cutWindowSize)
                .boxed().collect(Collectors.toList());

        final var queries = cutPosition.stream()
                .map(x -> word.replace(word.substring(x, x + cutWindowSize - 1), "%"))
                .collect(Collectors.toList());

        // get possible words
        final var similarWords = queries.stream()
                .map(query -> new HashSet<>(dictionaryManager.searchKeyWord(query, 30)))
                .reduce(
                        (set1, set2) -> Stream.concat(set1.stream(), set2.stream())
                                .collect(Collectors.toCollection(HashSet::new))
                );

        final var dictionary = similarWords.isPresent() ?
                similarWords.get()
                : Collections.<String>emptySet();

        // sort result and return it as a list
        return suggestCorrections(dictionary, word).entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /*
    Should be used with one word.
    This will linearly iterate over all word in dictionary and come with word that satisfied certain
    edit distance threshold.
     */
    private static Map<String, Integer> suggestCorrections(Set<String> dictionary, String word) {
        return dictionary.stream()
                // calculate edit distance and put all into a map
                .collect(Collectors.toMap(Function.identity(), x -> calculateEditDistance(word, x)))
                .entrySet()
                // filter all word that satisfies edit distance threshold
                .stream()
                .filter(entry -> entry.getValue() <= EDIT_DISTANCE_THRESHOLD)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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
