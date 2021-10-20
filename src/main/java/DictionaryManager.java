package main.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/*
1. Separation
2. Generic
3. Immutable
*/

public class DictionaryManager implements AutoCloseable {
    // database related configuration
    private static final String DB_NAME = "src/main/resources/dict_hh.db";
    private static final String TABLE_NAME = "av";
    private static final String DB_URL = String.format("jdbc:sqlite:%s", DB_NAME);

    // dictionary main table fields
    private static final String KEY_WORD = "word";
    private static final String DESCRIPTION = "description";
    private static final String PRONUNCIATION = "pronounce";
    private static final String ADDED_DATE = "date_add";

    // attributes
    private Connection dictionaryDBConnection = null;
    private final List<String> error = new ArrayList<>();

    // interface

    public DictionaryManager() {
        try {
            dictionaryDBConnection = DriverManager.getConnection(DB_URL);

        } catch (Exception e) {
            error.add(e.getMessage());
        }
    }

    /**
     *  Override AutoClosable interface, used to close db connection.
     */
    @Override
    public void close() {
        if (dictionaryDBConnection != null) {
            try {
                dictionaryDBConnection.close();

            } catch (Exception e) {
                error.add(e.getMessage());
            }
        }
    }

    /**
     * @return false if new word can not be inserted to the database.
     */
    public boolean insertWord(Word word) {
        // initiate current date
        final var now = new java.util.Date();
        final var sqlNow = new java.sql.Date(now.getTime());

        final var insertQuery = String.format(
          "INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
          TABLE_NAME,
          KEY_WORD,
          DESCRIPTION,
          PRONUNCIATION,
          ADDED_DATE
        );

        try (
                final var preStatement = dictionaryDBConnection.prepareStatement(insertQuery)
        ) {
            // set value
            preStatement.setString(1, word.keyWord());
            preStatement.setString(2, word.description());
            preStatement.setString(3, word.pronunciation());
            preStatement.setDate(4, sqlNow);

            // execute update
            preStatement.executeUpdate();

        } catch (Exception e) {
            error.add(e.getMessage());
            return false;
        }

        return true;
    }


    /**
     * @return false if word can not be deleted from the database.
     */
    public boolean removeWord(String keyWord) {
        final var removeQuery = String.format(
                "DELETE FROM %s WHERE %s = ?",
                TABLE_NAME,
                KEY_WORD
        );

        try (final var preStatement = dictionaryDBConnection.prepareStatement(removeQuery)) {
            // set deleting word
            preStatement.setString(1, keyWord);

            preStatement.executeUpdate();

        } catch (Exception e) {
            error.add(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * @param searchTerm first part of the word (eg: "app" -> "apple").
     * @return null if error occurred during searching.
     */
    public List<Word> search(String searchTerm) {
        final var searchQuery = String.format(
                "SELECT * FROM %s WHERE %s LIKE ?",
                TABLE_NAME,
                KEY_WORD
        );

        return getSearchResultFromDB(searchQuery, searchTerm);
    }

    /**
     * This is search function with limitation, recommend using this instead of the search method above.
     */
    public List<Word> search(String searchTerm, int limitation) {
        final var searchQuery = String.format(
                "SELECT * FROM %s WHERE %s LIKE ? LIMIT %d",
                TABLE_NAME,
                KEY_WORD,
                limitation
        );

        return getSearchResultFromDB(searchQuery, searchTerm);
    }

    // getter, setter

    public List<String> getError() {
        return error;
    }

    // supportive methods and procedures
    private List<Word> getSearchResultFromDB(String searchQuery, String searchTerm) {
        var result = new ArrayList<Word>();

        try (final var preStatement = dictionaryDBConnection.prepareStatement(searchQuery)) {
            // search in database
            preStatement.setString(1, searchTerm + "%");
            final var searchResult = preStatement.executeQuery();

            // get result and add to result list
            while (searchResult.next()) {
                final var keyWord = searchResult.getString(KEY_WORD);
                final var description = searchResult.getString(DESCRIPTION);
                final var pronunciation = searchResult.getString(PRONUNCIATION);

                // process sql date
                final var sqlAddedDate = searchResult.getString(ADDED_DATE);
                final var addedDate = new SimpleDateFormat("yyyy-MM-dd").parse(sqlAddedDate);

                result.add(new Word(keyWord, description, pronunciation, addedDate));
            }

        } catch (Exception e) {
            error.add(e.getMessage());
            return null;
        }

        return result;
    }
}