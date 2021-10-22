import java.util.LinkedHashMap;
import java.util.Map;

public class DictionaryCommandline {
    private final LinkedHashMap<String, String> dictionary;

    public DictionaryCommandline(LinkedHashMap<String, String> dictionary) {
        this.dictionary = dictionary;
    }

    public void showAllWords() {
        System.out.printf("%-6s |%-18s |%-18s\n", "No", "English", "Vietnamese");

        int counter = 1;
        for (Map.Entry<String, String> i : dictionary.entrySet()) {
            String wordTarget = i.getKey();
            String wordExplain = i.getValue();
            System.out.printf("%-6d |%-18s |%-18s\n", counter, wordTarget, wordExplain);
            counter++;
        }

        System.out.println();
    }
}
