package main.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;

/*
1. Separation
2. Generic
3. Immutable
*/

public class DictionaryManager {
    private static final String DB_NAME = "edict.db";
    private static final String TABLE_NAME = "tbl_edict";
    private static final String DB_URL = String.format("jdbc:sqlite:./%s", DB_NAME);

    private static final String KEY_WORD = "word";
    private static final String DESCRIPTION = "detail";

    private static final String ALL_FIELD = String.format("%s, %s", KEY_WORD, DESCRIPTION);

    private Connection dictionaryDBConnection = null;
    private List<String> error;

    /* Main method */

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
     * @param word key word, description
     * @return false if the operation failed
     */
    public boolean insertWord(Word word) {
        final var query = String.format(
                "insert into %s (%s) values ('%s', '%s')",
                TABLE_NAME,
                ALL_FIELD,
                word.keyWord().toLowerCase(),
                word.description().toLowerCase()
        );

        return executeUpdate(query);
    }

    /**
     * @param keyWord word to delete
     * @return false if the operation failed.
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
     * @param searchTerm first part of searched word
     * @return null if the operation failed.
     */
    public HashMap<String, Word> search(String searchTerm) {
        if (dictionaryDBConnection == null) {
            error.add("Database not connected");
            return null;
        }

        final var searchQuery = String.format(
                "select %s from %s where %s like '%s%%'",
                ALL_FIELD,
                TABLE_NAME,
                KEY_WORD,
                searchTerm
        );

        final var result = new HashMap<String, Word>();

        try {
            final var statement = dictionaryDBConnection.createStatement();
            final var resultSet = statement.executeQuery(searchQuery);

            while (resultSet.next()) {
                final var word = resultSet.getString(KEY_WORD);
                final var detail = resultSet.getString(DESCRIPTION);

                result.put(word, new Word(word, detail));
            }

            statement.close();
            resultSet.close();

        } catch (Exception e) {
            error.add(e.getMessage());
            return null;
        }

        return result;
    }

    public List<String> getError() {
        return error;
    }

    /* Support methods and functions */

    private boolean executeUpdate(String query) {
        if (dictionaryDBConnection == null) {
            error.add("Database not connected");
            return false;
        }

        try {
            var statement = dictionaryDBConnection.createStatement();
            statement.executeQuery(query);
            statement.close();

        } catch(Exception e) {
            error.add(e.getMessage());
            return false;
        }

        return true;
    }
}