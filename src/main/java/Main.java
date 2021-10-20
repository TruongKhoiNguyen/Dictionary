package main.java;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        } catch (Exception ignored) {}

         var dictionaryManager = new DictionaryManager();
         var result = dictionaryManager.search("apple");
         result.forEach(word -> System.out.printf("%s %s %s\n",
                 word.keyWord(),
                 word.description(),
                 word.pronunciation()));
         var error = dictionaryManager.getError();
         error.forEach(System.out::println);

         dictionaryManager.close();

//        DictionaryCommandLineApp app = new DictionaryCommandLineApp();
//
//        while (app.isRunning()) {
//            app.dictionaryBasic();
//        }
    }
}
