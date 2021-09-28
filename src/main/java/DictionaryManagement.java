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
        insertFromFile();
    }

    /** Insert from command line */
    public void insertFromCommandline() {
        int t = getInputTime();
        readInputAndPutIntoDictionary(t);
    }

    private static int getInputTime() {
        System.out.print("Input number of words: ");
        int input = scanner.nextInt();
        scanner.nextLine();
        return input;
    }

    private void readInputAndPutIntoDictionary(int times) {
        for (int i = 0; i < times; i++) {
            System.out.printf("Word no %d\n", i + 1);

            String wordTarget = readStringWithMessage("Target word");
            String wordExplain = readStringWithMessage("Explain word");

            insertWord(wordTarget, wordExplain);
        }
    }

    private static String readStringWithMessage(String message) {
        System.out.printf("%s: ", message);
        return scanner.nextLine();
    }

    private void insertWord(String wordTarget, String wordExplain) {
        dictionary.put(wordTarget.toLowerCase(), wordExplain.toLowerCase());
    }

    /** Insert from file. */
    public void insertFromFile() {
        File dictionaryData = new File("src/main/resources/dictionary.txt");

        try {
            List<String> listWord = Files.readAllLines(dictionaryData.toPath());
            for (String wordLine : listWord) {
                String[] word = wordLine.split(";");
                insertWord(word[0], word[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Search. */
    public void dictionaryLookup() {
        String wordTarget = readStringWithMessage("Search key").toLowerCase();

        if (dictionary.containsKey(wordTarget)) {
            String wordExplain = dictionary.get(wordTarget);
            System.out.printf("Your searched word mean: %s\n", wordExplain);
        } else {
            System.out.println("404 Error!");
        }
    }
}
