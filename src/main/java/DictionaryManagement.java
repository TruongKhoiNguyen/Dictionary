package main.java;

import java.util.Scanner;

public class DictionaryManagement {
    private final Dictionary dictionary;

    /** Constructor 1. */
    public DictionaryManagement(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    /** Read words from the command lines and add them to dictionary. */
    public void insertFromCommandline() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Input number of words: ");
        int t = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < t; i++) {
            System.out.printf("Word no %d\n", i + 1);

            System.out.print("Word target: ");
            String wordTarget = scanner.nextLine();

            System.out.print("Word explain: ");
            String wordExplain = scanner.nextLine();

            dictionary.addWord(wordTarget, wordExplain);
        }

        scanner.close();
    }
}
