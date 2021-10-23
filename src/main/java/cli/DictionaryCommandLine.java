package cli;

import shared.DictionaryManager;
import shared.Word;

import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class DictionaryCommandLine {
    private static Scanner scanner;
    private final DictionaryManager dictionaryManager = new DictionaryManager();

    /**
     * Input and Output will be set to UTF-8 encoding.
     */
    public DictionaryCommandLine() {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
            scanner = new Scanner(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.out.println("Unicode encoding is not supported. Some character may be illegible.");
            scanner = new Scanner(System.in);
        }
    }


    /**
     * This method acts as an operating system for all the methods below. An option menu will
     * be displayed to the user to choose.
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
     * Dictionary lookup is a full small program consists of asking the user for input and display
     * result on the console. Maximum number of result is 20 for easy read, so the method will notify
     * user about it. Previously null pointer check will be removed because dictionary manager search no
     * longer return null for failed search.
     */
    public void dictionaryLookup() {
        // get search key word from the user
        System.out.print("Input your search key here: ");
        final var searchTerm = scanner.nextLine();
        System.out.println();

        // search result
        final var searchResult = dictionaryManager.search(searchTerm, 20);

        // print search result to the screen
        if (searchResult.size() != 0) {
            System.out.println("""
                    The result will be limited to 20 result. If you want to see more, please
                    use the GUI version.
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
                        word.getKeyWord(),
                        word.getDescription(),
                        word.getPronunciation()
                );
                counter += 1;
            }

        } else {
            System.out.println("This word is not in the dictionary.");
        }

        scanner.nextLine();
    }

    /**
     * This function shows first 10 words in the dictionary database, just for showing.
     * Not many functionalities any way.
     */
    public void printFirst10Words() {
        // get first 10 words
        final var first10Words = dictionaryManager.search("", 10);

        // print result
        if (first10Words.size() != 0) {
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
                        word.getKeyWord(),
                        word.getDescription(),
                        word.getPronunciation()
                );
                counter += 1;
            }

        } else {
            System.out.println("Something have gone wrong :((.");
        }

        scanner.nextLine();
    }


    /**
     * This method serve for both insert and update purpose. However, any consequence must be alerted
     * because this method change the content in the database permanently. Try, catch block is used to
     * quit instead or return void.
     */
    public void insertWord() {
        // get the english word and check for existing word
        System.out.println("""
                Pronunciation will not be insert through this. If you want to add new words
                with pronunciation, please use the GUI version.
                
                """);
        System.out.print("Insert your word here: ");
        final var keyWord = scanner.nextLine();

        final var checkedWord = dictionaryManager.search(keyWord, 1);

        // This try block is used to cancel the procedure
        try {
            // notify user about existing words
            if (checkedWord.size() == 1) {
                System.out.print("This word is already in the dictionary, do you want to update this word? [y/n]");
                final var c = scanner.nextLine();
                if (c.equalsIgnoreCase("n")) {
                    throw new Exception("User changed mind.");
                }
            }

            // insert remaining information
            System.out.print("Insert meaning: ");
            final var description = scanner.nextLine();
            final var word = new Word(0, keyWord, description, "", null);

            final var s = dictionaryManager.insertWord(word);
            System.out.println(s ? "Insert completed" : "Insertion failed");

        } catch (Exception ignored) {}
    }

    /**
     * Also dangerous!
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
