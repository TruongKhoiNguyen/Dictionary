package main.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

public class DictionaryManager {
    private static final String DB_NAME = "edict.db";
    private static final String TABLE_NAME = "tbl_edict";
    private static final String DB_URL = String.format("jdbc:sqlite:./%s", DB_NAME);

    /* Dictionary database field */
    private static final String EN_WORD = "word";
    private static final String VI_WORD = "detail";
    // private static final String PRONUNCIATION = "pronounce";

    /* Query builder */
    private static final Function<Word, String> INSERT_QUERY = word -> String.format(
            "insert into %s (%s, %s) values ('%s', '%s')",
            TABLE_NAME,
            EN_WORD,
            VI_WORD,
            word.en_word().toLowerCase(),
            word.vi_word().toLowerCase()
    );

    private static final Function<String, String> DELETE_QUERY = en_word -> String.format(
            "delete from %s where word = '%s'",
            TABLE_NAME,
            en_word.toLowerCase()
    );

    private static final Function<String, String> SEARCH_QUERY = searchTerm -> String.format(
            "select %s, %s from %s where %s like '%s%%'",
            EN_WORD,
            VI_WORD,
            TABLE_NAME,
            EN_WORD,
            searchTerm
    );

    Optional<Connection> dictionaryDBConnection;

    /** DictionaryManager :: DictionaryManager */
    public DictionaryManager() {
        try {
            var connection = DriverManager.getConnection(DB_URL);
            dictionaryDBConnection = Optional.of(connection);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            dictionaryDBConnection = Optional.empty();
        }
    }

    /** finalized :: dictionaryDBConnection -> IO () */
    public void close() {
        dictionaryDBConnection.ifPresent(
                connection -> {
                    try {
                        connection.close();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
        );
    }

    /** insertWord :: dictionaryDBConnection -> Word -> IO String */
    /* Return "" if the method successfully insert word */
    public String insertWord(Word word) {
        if (dictionaryDBConnection.isEmpty()) {
            return "Database not connected.";
        }

        final var query = INSERT_QUERY.apply(word);

        try {
            final var statement = dictionaryDBConnection.get().createStatement();
            statement.executeUpdate(query);
            statement.close();

        } catch (Exception e) {
            return e.getMessage();
        }

        return "";
    }

    /** removeWord :: dictionaryDBConnection -> String -> IO String */
    /* Return "" if the method successfully remove word */
    public String removeWord(String en_word) {
        if (dictionaryDBConnection.isEmpty()) {
            return "Database not connected.";
        }

        final var query = DELETE_QUERY.apply(en_word);

        try {
            final var statement = dictionaryDBConnection.get().createStatement();
            statement.executeUpdate(query);

            statement.close();

        } catch (Exception e) {
            return e.getMessage();
        }

        return "";
    }

    /** search :: String -> IO Either Map(String, Word) Error */
    /* If key "Error!" is not "" then the method return error */
    public HashMap<String, Word> search(String searchTerm) {
        final var result = new HashMap<String, Word>();

        if (dictionaryDBConnection.isEmpty()) {
            result.put("Error!", new Word("Database not connected.", null));
            return result;
        }

        final var searchQuery = SEARCH_QUERY.apply(searchTerm);

        try {
            final var statement = dictionaryDBConnection.get().createStatement();
            final var resultSet = statement.executeQuery(searchQuery);

            while (resultSet.next()) {
                final var word = resultSet.getString(EN_WORD);
                final var detail = resultSet.getString(VI_WORD);
                // System.out.printf("%s %s", word, detail);
                result.put(word, new Word(word, detail));
            }

            statement.close();
            resultSet.close();

        } catch (Exception e) {
            result.put("Error!", new Word(e.getMessage(), null));
        }

        return result;
    }
}