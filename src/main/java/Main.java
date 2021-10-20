package main.java;

import main.java.DictionaryCommandLineApp;

public class Main {
    public static void main(String[] args) {
//        DictionaryCommandLineApp app = new DictionaryCommandLineApp();
//
//        while (app.isRunning()) {
//            app.dictionaryBasic();
//        }
        Word word = new Word("thinh", "student", "null");
        DictionaryManager dictionaryManager = new DictionaryManager();
        if (dictionaryManager.insertWord(word)) {
            System.out.println("Yes");
        } else {
            System.out.println("No");
        }
    }
}
