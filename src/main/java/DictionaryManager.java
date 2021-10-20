package main.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;

/*
1. Separation
2. Generic
3. Immutable
*/

public class DictionaryManager {
    private static final String DB_NAME = "dict_hh.db";
    private static final String TABLE_NAME = "av";
    private static final String DB_URL = String.format("jdbc:sqlite:./main\\resources\\%s", DB_NAME);

    private static final String ID = "id";
    private static final String KEY_WORD = "word";
    private static final String DESCRIPTION = "description";
    private static final String PRONOUNCE = "pronounce";
    private static final String DATE_ADD = "date_add";

    private static final String ALL_FIELD = String.format("%s, %s, %s, %s, %s", ID, KEY_WORD,
            DESCRIPTION, PRONOUNCE, DATE_ADD);
    private static final String NOT_ID_FIELD = String.format("%s, %s, %s, %s", KEY_WORD,
            DESCRIPTION, PRONOUNCE, DATE_ADD);

    private Connection dictionaryDBConnection = null;
    private ArrayList<String> error = new ArrayList<>();

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
                "insert into %s (%s) values ('%s', '%s', '%s', DATE())",
                TABLE_NAME,
                NOT_ID_FIELD,
                word.getWord().toLowerCase(),
                word.getDescription().toLowerCase(),
                word.getPronounce().toLowerCase()
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
                final var id = resultSet.getInt(ID);
                final var word = resultSet.getString(KEY_WORD);
                final var description = resultSet.getString(DESCRIPTION);
                final var pronounce = resultSet.getString(PRONOUNCE);
                final var date_add = resultSet.getString(DATE_ADD);

                result.put(word, new Word(id, word, description, pronounce, date_add));
            }

            statement.close();
            resultSet.close();

        } catch (Exception e) {
            error.add(e.getMessage());
            return null;
        }

        return result;
    }

    /**
     * This is override for the search function above, used to limit result.
     * @param searchTerm like above
     * @param limitation limit number of result
     * @return like above
     */
    public HashMap<String, Word> search(String searchTerm, int limitation) {
        if (dictionaryDBConnection == null) {
            error.add("Database not connected");
            return null;
        }

        final var searchQuery = String.format(
                "select %s from %s where %s like '%s%%' limit %d",
                ALL_FIELD,
                TABLE_NAME,
                KEY_WORD,
                searchTerm,
                limitation
        );

        final var result = new HashMap<String, Word>();

        try {
            final var statement = dictionaryDBConnection.createStatement();
            final var resultSet = statement.executeQuery(searchQuery);

            while (resultSet.next()) {
                final var id = resultSet.getInt(ID);
                final var word = resultSet.getString(KEY_WORD);
                final var description = resultSet.getString(DESCRIPTION);
                final var pronounce = resultSet.getString(PRONOUNCE);
                final var date_add = resultSet.getString(DATE_ADD);

                result.put(word, new Word(id, word, description, pronounce, date_add));
            }

            statement.close();
            resultSet.close();

        } catch (Exception e) {
            error.add(e.getMessage());
            return null;
        }

        return result;
    }

    public ArrayList<String> getError() {
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