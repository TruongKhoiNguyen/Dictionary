package shared;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.ceil;
import static java.lang.Math.min;

public record SpellChecker(DictionaryManager dictionaryManager) {
    private static final int EDIT_DISTANCE_THRESHOLD = 3;

    /**
     * Check spelling and return sorted list of similar word.
     */
    public List<String> correctSpelling(String word) {
        // generate search queries
        final var windowSize = (int)ceil(1.0 * word.length() / 3);

        final var queries = IntStream.rangeClosed(0, word.length() - windowSize)
                .boxed()
                .map(x -> word.substring(0, x) + "%" + word.substring(x + windowSize))
                .collect(Collectors.toList());

        // get dictionary of similar words
        final var similarWords = dictionaryManager.searchKeyWord(queries, 100);

        // calculate distance -> filter -> sorted
        return similarWords.stream()
                .collect(
                        Collectors.toMap(Function.identity(), w -> calculateEditDistance(word, w))
                )
                .entrySet()
                .stream()
                .filter(e -> e.getValue() < EDIT_DISTANCE_THRESHOLD)
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Calculate Levenshtein distance using iterative with two matrix rows ways.
     * Pseudocode of this algorithm is in Wikipedia.
     */
    public static int calculateEditDistance(String a, String b) {
        // create two work vectors of integer distances
        var v0 = new int[b.length() + 1];
        var v1 = new int[b.length() + 1];

        // initialize v0 (the previous row of distances)
        // this row is A[0][i]: edit distance for an empty s
        // the distance is just the number of characters to delete from t
        for (int i = 0; i <= b.length(); ++i) {
            v0[i] = i;
        }

        for (int i = 0; i < a.length(); ++i) {
            // calculate v1 (current row distances) from the previous row v0

            // first element of v1 is A[i + 1][0]
            // edit distance is delete (i + 1) chars from s to match empty t
            v1[0] = i + 1;

            // use formula to fill in the rest of the row
            for (int j = 0; j < b.length(); ++j) {
                // calculating costs for A[i + 1][j + 1]
                final var deletionCost = v0[j + 1] + 1;
                final var insertionCost = v1[j] + 1;

                var substitutionCost = 0;
                if (a.charAt(i) == b.charAt(j)) {
                    substitutionCost = v0[j];
                } else {
                    substitutionCost = v0[j] + 1;
                }

                v1[j + 1] = min(deletionCost, min(insertionCost, substitutionCost));
            }

            // copy v1 (current row) to v0 (previous row) for next iteration
            // since data in v1 is always invalidated, a swap without copy could be more efficient
            var tmp = v0;
            v0 = v1;
            v1 = tmp;
        }

        return v0[b.length()];
    }
}
