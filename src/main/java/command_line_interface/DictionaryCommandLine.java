package main.java.command_line_interface;

import main.java.DictionaryManager;
import main.java.Word;

import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class DictionaryCommandLine {
    // functions of this application
    //   change words
    //   show first 10 word
    //   dictionary lookup
    //   remove word
    //   insert word

    private static Scanner scanner;
    private final DictionaryManager dictionaryManager = new DictionaryManager();

    /**
     * This constructor is used to set UTF-8 encoding for reading and printing result.
     */
    public DictionaryCommandLine() {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
            scanner = new Scanner(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.out.println("Cannot output unicode characters, this app may not work as intended.");
            scanner = new Scanner(System.in);
        }
    }

    /**
     * Full application.
     */
    public void run() {
        var isRunning = true;

        while (isRunning) {
            // print menu and get choice
            // lookup, first 10 word and print error will need user to press enter to exit
            System.out.println("Dictionary");
            System.out.println("""
                    1. Lookup words
                    2. First 10 words
                    3. Print error
                    4. Insert
                    5. Remove
                    6. Quit
                    """);
            System.out.print("Choose option no: ");
            final var c = scanner.nextInt();
            scanner.nextLine(); // fix integer bug

            // go to each option
            switch (c) {
                case 1 -> dictionaryLookup();
                case 2 -> printFirst10Words();
                case 3 -> printError();
                case 4 -> insertWord();
                case 5 -> removeWord();
                case 6 -> isRunning = false;
            }
        }
    }


    /**
     * This method get search query from the user and print the result on the screen.
     * If any errors occurred during searching, print the error to the user.
     * Return only 20 results, if users want to get more, suggest user to write more specific search word.
     */
    public void dictionaryLookup() {
        // get search key word from the user
        System.out.print("Input your search key here: ");
        final var searchTerm = scanner.nextLine();
        System.out.println();

        // search result
        final var searchResult = dictionaryManager.search(searchTerm, 20);

        // print search result to the screen
        if (searchResult != null) {
            System.out.println("""
                    For some limitation we will just limit the number of results to 20.
                    Also, added date will not be displayed in this version.
                    If you want to have more functionalities, please use to the GUI version.
                    """);
            System.out.println("Total search result: " + searchResult.size());

            System.out.printf(
                    "%3s | %-25s | %-35s | %-25s\n",
                    "No",
                    "English",
                    "Vietnamese",
                    "Pronunciation"
            );

            // keep attention to counter!
            var counter = 1;
            for (var word : searchResult) {
                System.out.printf(
                        "%3d | %-25s | %-35s | %-25s\n",
                        counter,
                        word.keyWord(),
                        word.description(),
                        word.pronunciation()
                );
                counter += 1;
            }

            // wait for input
        } else {
            System.out.println("Can not search the word");
        }

        // wait for input
        scanner.nextLine();
    }

    /**
     * This function shows first 10 words in the dictionary database, just for showing.
     * Not many functionalities any way.
     */
    public void printFirst10Words() {
        // just get the result and print
        // get first 10 words
        final var first10Words = dictionaryManager.search("", 10);

        // print result
        if (first10Words != null) {
            System.out.printf(
                    "%3s | %-25s | %-35s | %-25s\n",
                    "No",
                    "English",
                    "Vietnamese",
                    "Pronunciation"
            );

            // keep attention to counter!
            var counter = 1;
            for (var word : first10Words) {
                System.out.printf(
                        "%3d | %-25s | %-35s | %-25s\n",
                        counter,
                        word.keyWord(),
                        word.description(),
                        word.pronunciation()
                );
                counter += 1;
            }

            // wait for input
        } else {
            System.out.println("Something have gone wrong :((.");
        }

        // wait for input
        scanner.nextLine();
    }

    /**
     * This function is used for both insert new word and change word.
     * Must notify the user for any changes in the dictionary.
     * Can not write pronunciation to new word.
     */
    public void insertWord() {
        // get the english word and check for existing word
        System.out.println("""
                This option can not input pronunciation.
                If you want to insert full words, please use the gui version.
                This dictionary distinguish upper and lower case, only use upper case when necessary.
                """);
        System.out.print("Insert your word here: ");
        final var keyWord = scanner.nextLine();
        System.out.println();

        final var checkedWord = dictionaryManager.search(keyWord, 1);

        // This try block is used to cancel the procedure
        try {
            if (checkedWord == null) {
                System.out.println("Some thing have gone wrong :((.");
                throw new Exception("Can not search for word");
            }

            // notify user about existing words
            if (checkedWord.size() == 1) {
                System.out.print("This word is already in the dictionary, do you want to continue changing? [y/n]");
                final var c = scanner.nextLine();
                if (c.equalsIgnoreCase("n")) {
                    throw new Exception("User changed mind.");
                }
            }

            // insert remaining information
            System.out.print("Insert meaning: ");
            final var description = scanner.nextLine();

            final var word = new Word(keyWord, description, "", null);

            final var s = dictionaryManager.insertWord(word);
            System.out.println(s ? "Insert completed" : "Insertion failed");

        } catch (Exception ignored) {}
    }

    /**
     * Dangerous.
     */
    public void removeWord() {
        try {
            System.out.print("Insert words you want to remove: ");
            final var tmp = scanner.nextLine();

            // notify user
            System.out.print("This will permanently remove this word, do you want to continue? [y/n]");
            final var c = scanner.nextLine();

            if (c.equalsIgnoreCase("n")) {
                throw new Exception("User changed mind.");
            }

            // remove
            final var rs = dictionaryManager.removeWord(tmp);
            System.out.println(rs ? "Remove word successful" : "Failed to remove word");

        } catch (Exception ignored) {}
    }

    /**
     * Print error for debug.
     */
    public void printError() {
        final var error = dictionaryManager.getError();
        error.forEach(System.out::println);
        scanner.nextLine();
    }
}
