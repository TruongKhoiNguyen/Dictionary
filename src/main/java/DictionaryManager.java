package main.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DictionaryManager implements AutoCloseable {
    /* constants */
    private static final String DB_NAME = "src/main/resources/dict_hh.db";
    private static final String TABLE_NAME = "av";
    private static final String DB_URL = String.format("jdbc:sqlite:%s", DB_NAME);

    private static final String KEY_WORD = "word";
    private static final String DESCRIPTION = "description";
    private static final String PRONUNCIATION = "pronounce";
    private static final String ADDED_DATE = "date_add";

    private Connection dictionaryDBConnection = null;
    private final List<String> error = new ArrayList<>();

    /* interface */

    /** Constructor creates new connection with the database. */
    public DictionaryManager() {
        try {
            dictionaryDBConnection = DriverManager.getConnection(DB_URL);

        } catch (Exception e) {
            error.add(e.getMessage());
        }
    }

    /**
     * Insert word method takes a parameter type word, and insert key word and description to
     * dictionary database. Pronunciation is not required as it is not possible to write it easily.
     * @return true if the insertion process is done properly. Otherwise, return false. Due to its
     * direct interaction with the database, it is recommended to avoid using this function if possible.
     */
    public boolean insertWord(Word word) {
        final var insertQuery = String.format(
          "INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, DATE())",
          TABLE_NAME,
          KEY_WORD,
          DESCRIPTION,
          PRONUNCIATION,
          ADDED_DATE
        );

        try (
                final var preStatement = dictionaryDBConnection.prepareStatement(insertQuery)
        ) {
            preStatement.setString(1, word.keyWord());
            preStatement.setString(2, word.description());
            preStatement.setString(3, word.pronunciation());

            preStatement.executeUpdate();

        } catch (Exception e) {
            error.add(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     *  Remove word method takes a string as key word to find and remove word in dictionary database.
     *  Like insert word, this method also interacts directly with the database. So it should
     *  also be avoided.
     */
    public boolean removeWord(String keyWord) {
        final var removeQuery = String.format(
                "DELETE FROM %s WHERE %s = ?",
                TABLE_NAME,
                KEY_WORD
        );

        try (final var preStatement = dictionaryDBConnection.prepareStatement(removeQuery)) {
            preStatement.setString(1, keyWord);
            preStatement.executeUpdate();

        } catch (Exception e) {
            error.add(e.getMessage());
            return false;
        }

        return true;
    }


    /**
     * This method return all possible results which may result in large ram consumption and crashing.
     * It should be used with caution.
     * @param searchTerm a string represent first part of the searched word. For examples, if the searchTerm
     *                   is "app", this method may return "apple" or "application".
     * @return a list of word representing the result, result set returned from the database have been parsed
     * in this function for easy manipulation in client methods and functions.
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
     * This search the same as the search method above, the only difference is the limitation of
     * result. So client methods and functions can be assured that the result can not growth to large
     * that can make machine crash.
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

    /* supportive methods and functions */

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
            return new ArrayList<>();
        }

        return result;
    }

    // getter, setter

    public List<String> getError() {
        return error;
    }

    /* other methods and functions */
    /**
     * This method override close method of AutoClosable interface. The purpose of this method
     * is to be able to be closed in try-with-resources block. As a result, database connection can be
     * properly closed after any objects of this class being removed by Garbage Collector.
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
}