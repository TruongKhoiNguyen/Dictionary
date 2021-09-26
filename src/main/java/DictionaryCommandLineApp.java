package main.java;

import java.util.Scanner;

public class DictionaryCommandLineApp {
    private static final Scanner scanner = new Scanner(System.in);

    private final Dictionary dictionary = new Dictionary();
    private final DictionaryManagement dictMan = new DictionaryManagement(dictionary);
    private final DictionaryCommandline dictCom = new DictionaryCommandline(dictionary);
    private boolean isRunning = true;

    /** isRunning. */
    public boolean isRunning() {
        return isRunning;
    }

    /** Run dictionary cli app. */
    public void dictionaryBasic() {
        showOptions();
        int option = getChoice();
        executeOption(option);
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
            case 1 -> dictMan.insertFromCommandline();
            case 2 -> dictCom.showAllWords();
            case 3 -> isRunning = false;
            default -> {}
        }
    }
}
