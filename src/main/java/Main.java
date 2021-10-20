package main.java;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        DictionaryCommandLineApp app = new DictionaryCommandLineApp();

        while (app.isRunning()) {
            app.dictionaryBasic();
        }
    }
}
