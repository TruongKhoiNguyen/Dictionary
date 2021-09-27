package main.java;

import java.util.Scanner;

public class DictionaryCommandLineApp {
    private static final Scanner scanner = new Scanner(System.in);

    private final Dictionary dictionary = new Dictionary();
    private final DictionaryManagement dictionaryManager = new DictionaryManagement(dictionary);
    private final DictionaryCommandline dictionaryCommandline = new DictionaryCommandline(dictionary);
    private boolean isRunning = true;

    /** isRunning. */
    public boolean isRunning() {
        return isRunning;
    }

    /** Run dictionary cli app. */
    public void dictionaryBasic() {
        insertFromFile();
        showOptions();
        int option = getChoice();
        executeOption(option);
    }

    private void insertFromFile() {
        dictionaryManager.insertFromFile();
    }

    private static void showOptions() {
        System.out.println("Options: ");
        System.out.println("1. Insert");
        System.out.println("2. Show");
        System.out.println("3. Search");
        System.out.println("4. Exit");
    }

    private static int getChoice() {
        int option = scanner.nextInt();
        scanner.nextLine();
        return option;
    }

    private void executeOption(int option) {
        switch (option) {
            case 1 -> dictionaryManager.insertFromCommandline();
            case 2 -> dictionaryCommandline.showAllWords();
            case 3 -> dictionaryCommandline.searchWord();
            case 4 -> isRunning = false;
            default -> {}
        }
    }
}
