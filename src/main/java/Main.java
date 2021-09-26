package main.java;

import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    static Dictionary dictionary = new Dictionary();
    static DictionaryManagement dictMan = new DictionaryManagement(dictionary);
    static DictionaryCommandline dictCom = new DictionaryCommandline(dictionary);

    public static void main(String[] args) {
        boolean isRunning = true;
        while (isRunning) {
            showOptions();
            int option = getChoice();
            isRunning = executeOption(option);
        }
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

    private static boolean executeOption(int option) {
        switch (option) {
            case 1 -> dictMan.insertFromCommandline();
            case 2 -> dictCom.showAllWords();
            case 3 -> { return false; }
            default -> {}
        }

        return true;
    }
}
