package main.java;

import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

/*
1. Separation
2. Generic
3. Immutable
*/

public class DictionaryManager {
    // database related configuration
    private static final String DB_NAME = "src/main/resources/dict_hh.db";
    private static final String TABLE_NAME = "av";
    private static final String DB_URL = String.format("jdbc:sqlite:%s", DB_NAME);

    // dictionary main table fields
    private static final String KEY_WORD = "word";
    private static final String DESCRIPTION = "description";
    private static final String PRONUNCIATION = "pronounce";
    // private static final String ADDED_DATE = "date_add";

    private static final String ALL_FIELD = String.format(
            "%s, %s, %s",
            KEY_WORD,
            DESCRIPTION,
            PRONUNCIATION);

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

    public boolean close() {
        if (dictionaryDBConnection != null) {
            try {
                dictionaryDBConnection.close();

            } catch (Exception e) {
                error.add(e.getMessage());
                return false;
            }
        }

        return true;
    }

    /**
     * @return false if new word can not be inserted to the database.
     */
    public boolean insertWord(Word word) {
        final var query = String.format(
                "insert into %s (%s) values ('%s', '%s', '%s')",
                TABLE_NAME,
                ALL_FIELD,
                word.keyWord().toLowerCase(),
                word.description().toLowerCase(),
                word.pronunciation()
        );

        return executeUpdate(query);
    }


    /**
     * @return false if word can not be deleted from the database.
     */
    public boolean removeWord(String keyWord) {
        final var query = String.format(
                "delete from %s where word = '%s'",
                TABLE_NAME,
                keyWord.toLowerCase()
        );

        return executeUpdate(query);
    }

    /**
     * @param searchTerm first part of the word (eg: "app" -> "apple").
     * @return null if error occurred during searching.
     */
    public List<Word> search(String searchTerm) {
        final var searchQuery = String.format(
                "select %s from %s where %s like '%s%%'",
                ALL_FIELD,
                TABLE_NAME,
                KEY_WORD,
                searchTerm
        );

        return searchByQuery(searchQuery);
    }

    /**
     * This is search function with limitation, recommend using this instead of the search method above.
     */
    public List<Word> search(String searchTerm, int limitation) {
        final var searchQuery = String.format(
                "select %s from %s where %s like '%s%%' limit %d",
                ALL_FIELD,
                TABLE_NAME,
                KEY_WORD,
                searchTerm,
                limitation
        );

        return searchByQuery(searchQuery);
    }

    // getter, setter

    public List<String> getError() {
        return error;
    }

    // supportive methods and procedures
    private @Nullable
    List<Word> searchByQuery(String searchQuery) {
        if (dictionaryDBConnection == null) {
            error.add("Database not connected");
            return null;
        }

        final var result = new ArrayList<Word>();

        try {
            final var statement = dictionaryDBConnection.createStatement();
            final var resultSet = statement.executeQuery(searchQuery);

            while (resultSet.next()) {
                final var word = resultSet.getString(KEY_WORD);
                final var detail = resultSet.getString(DESCRIPTION);
                final var pronunciation = resultSet.getString(PRONUNCIATION);

                result.add(new Word(word, detail, pronunciation));
            }

            statement.close();
            resultSet.close();

        } catch (Exception e) {
            error.add(e.getMessage());
            return null;
        }

        return result;
    }

    private boolean executeUpdate(String query) {
        if (dictionaryDBConnection == null) {
            error.add("Database not connected");
            return false;
        }

        try {
            var statement = dictionaryDBConnection.createStatement();
            statement.executeUpdate(query);
            statement.close();

        } catch(Exception e) {
            error.add(e.getMessage());
            return false;
        }

        return true;
    }
}