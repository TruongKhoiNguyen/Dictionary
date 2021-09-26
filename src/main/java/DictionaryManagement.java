package main.java;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
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

    public void insertFromFile() {
        File fileData = new File("src/dictionary.txt");

        try {
            List<String> listWord = Files.readAllLines(fileData.toPath());
            for (String wordLine : listWord) {
                String[] word = wordLine.split(";");
                dictionary.addWord(word[0], word[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
