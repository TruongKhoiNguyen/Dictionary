package main.java;

import java.io.*;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

public class DictionaryManagement {
    private static final Scanner scanner = new Scanner(System.in);

    private final LinkedHashMap<String, String> dictionary;

    public DictionaryManagement(LinkedHashMap<String, String> dictionary) {
        this.dictionary = dictionary;
    }

    public void insertFromCommandline() {
        System.out.print("Input number of words: ");
        int t = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < t; i++) {
            System.out.printf("Word no %d\n", i + 1);

            String wordTarget = readStringWithMessage("Target word");
            String wordExplain = readStringWithMessage("Explain word");

            dictionary.put(wordTarget, wordExplain);
        }
    }

    private static String readStringWithMessage(String message) {
        System.out.printf("%s: ", message);
        return scanner.nextLine();
    }

    public void insertFromFile() {
        File dictionaryData = new File("src/main/resources/dictionary.txt");

        try {
            List<String> listWord = Files.readAllLines(dictionaryData.toPath());
            for (String wordLine : listWord) {
                String[] word = wordLine.split(";");
                dictionary.put(word[0], word[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dictionaryLookup() {
        String wordTarget = readStringWithMessage("Search key: ");

        if (dictionary.containsKey(wordTarget)) {
            String wordExplain = dictionary.get(wordTarget);
            System.out.printf("Your searched word mean: %s\n", wordExplain);
        } else {
            System.out.println("404 Error!");
        }
    }
}
