package main.java;

import main.java.DictionaryCommandLineApp;

public class Main {
    public static void main(String[] args) {
        DictionaryCommandLineApp app = new DictionaryCommandLineApp();

        while (app.isRunning()) {
            app.dictionaryBasic();
        }
    }
}
