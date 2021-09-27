package main.java;

import java.util.Scanner;

public class DictionaryCommandLineApp {
    private static final Scanner scanner = new Scanner(System.in);

    private final Dictionary dictionary;
    private final DictionaryManagement dictionaryManager;
    private final DictionaryCommandline dictionaryCommandline;

    private boolean isRunning;

    public DictionaryCommandLineApp() {
        dictionary = new Dictionary();
        dictionaryManager = new DictionaryManagement(dictionary);
        dictionaryCommandline = new DictionaryCommandline(dictionary);
        isRunning = true;
    }

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
        System.out.println("3. Exit");
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
            case 3 -> isRunning = false;
            default -> {}
        }
    }
}
