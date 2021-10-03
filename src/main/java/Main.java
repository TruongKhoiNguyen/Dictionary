package main.java;

import main.java.DictionaryCommandLineApp;

public class Main {
    public static void main(String[] args) {
        DictionaryCommandLineApp app = new DictionaryCommandLineApp();
        DictionaryManager manager = new DictionaryManager();

        while (app.isRunning()) {
            app.dictionaryBasic();
        }
    }
}
