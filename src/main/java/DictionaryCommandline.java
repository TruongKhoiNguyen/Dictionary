package main.java;

import main.java.Dictionary;

public class DictionaryCommandline {
    private Dictionary dictionary;

    /** Constructor 1. */
    public DictionaryCommandline(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void showAllWords() {
        System.out.printf("%-6s |%-18s |%-18s\n", "No", "English", "Vietnamese");

        int size = dictionary.size();
        for (int i = 0; i < size; i++) {
            String wordTarget = dictionary.getWordTarget(i);
            String wordExplain = dictionary.getWordExplain(i);

            System.out.printf("%-6d |%-18s |%-18s\n", i + 1, wordTarget, wordExplain);
        }
    }
}
