package main.java;

import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Dictionary dictionary = new Dictionary();
        DictionaryManagement dictMan = new DictionaryManagement(dictionary);
        DictionaryCommandline dictCom = new DictionaryCommandline(dictionary);

        loop: while (true) {
            System.out.println("Options: ");
            System.out.println("1. Insert");
            System.out.println("2. Show");
            System.out.println("3. Exit");

            int n = scanner.nextInt();
            scanner.nextLine();

            switch (n) {
                case 1 -> dictMan.insertFromCommandline();
                case 2 -> dictCom.showAllWords();
                case 3 -> { break loop; }
                default -> {}
            }
        }
    }
}
