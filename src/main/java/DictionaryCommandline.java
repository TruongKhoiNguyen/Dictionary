package main.java;

import java.util.Objects;
import java.util.Scanner;

public class DictionaryCommandline {
    private final Dictionary dictionary;

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

    public void searchWord() {
        System.out.print("Enter the word you want to find : ");
        Scanner getSearchWord = new Scanner(System.in);
        String searchWord = getSearchWord.next();

        System.out.printf("%-6s |%-18s |%-18s\n", "No", "English", "Vietnamese");

        int size = dictionary.size();
        for (int i = 0; i < size; i++) {
            if (Objects.equals(dictionary.getWordTarget(i), searchWord)) {
                String wordTarget = dictionary.getWordTarget(i);
                String wordExplain = dictionary.getWordExplain(i);

                System.out.printf("%-6s |%-18s |%-18s\n", "", wordTarget, wordExplain);
            }
        }
    }
}
