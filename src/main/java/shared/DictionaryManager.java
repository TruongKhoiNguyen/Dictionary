package shared;

import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DictionaryManager implements AutoCloseable {
    /* constants */
    private static final String DB_NAME = "src/main/resources/dict_hh.db";
    private static final String TABLE_NAME = "av";
    private static final String TABLE_NAME_H = "history";
    private static final String TABLE_NAME_B_M = "bookmark";
    private static final String DB_URL = String.format("jdbc:sqlite:%s", DB_NAME);

    private static final String ID = "id";
    private static final String KEY_WORD = "word";
    private static final String DESCRIPTION = "description";
    private static final String PRONUNCIATION = "pronounce";
    private static final String ADDED_DATE = "date_add";

    private static final String ID_WORD = "id_word";
    private static final String DATE = "date";

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
            preStatement.setString(1, word.getKeyWord());
            preStatement.setString(2, word.getDescription());
            preStatement.setString(3, word.getPronunciation());

            preStatement.executeUpdate();

        } catch (Exception e) {
            error.add(e.getMessage());
            return false;
        }

        return true;
    }

    /** */
    public boolean insertHistory(Word word) {
        final var insertQuery = String.format(
                "INSERT INTO %s (%s, %s) VALUES (?, DATE())",
                TABLE_NAME_H,
                ID_WORD,
                DATE
        );

        try (
                final var preStatement = dictionaryDBConnection.prepareStatement(insertQuery)
        ) {
            preStatement.setInt(1, word.getId());

            preStatement.executeUpdate();

        } catch (Exception e) {
            error.add(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean insertBookmark(Word word) {
        final var insertQuery = String.format(
                "INSERT INTO %s (%s, %s) VALUES (?, DATE())",
                TABLE_NAME_B_M,
                ID_WORD,
                DATE
        );

        try (
                final var preStatement = dictionaryDBConnection.prepareStatement(insertQuery)
        ) {
            preStatement.setInt(1, word.getId());

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

    /** */
    public boolean removeWord(Word word) {
        final var removeQuery = String.format(
                "DELETE FROM %s WHERE %s = ?",
                TABLE_NAME,
                KEY_WORD
        );

        try (final var preStatement = dictionaryDBConnection.prepareStatement(removeQuery)) {
            preStatement.setString(1, word.getKeyWord());
            preStatement.executeUpdate();

        } catch (Exception e) {
            error.add(e.getMessage());
            return false;
        }

        return removeWordFromHistory(word) && removeWordFromBookmark(word);
    }

    public boolean removeWordFromHistory(Word word) {
        final var removeQueryH = String.format(
                "DELETE FROM %s WHERE %s = ?",
                TABLE_NAME_H,
                ID_WORD
        );

        try (final var preStatement = dictionaryDBConnection.prepareStatement(removeQueryH)) {
            preStatement.setInt(1, word.getId());
            preStatement.executeUpdate();

        } catch (Exception e) {
            error.add(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean removeWordFromBookmark(Word word) {
        final var removeQueryBM = String.format(
                "DELETE FROM %s WHERE %s = ?",
                TABLE_NAME_B_M,
                ID_WORD
        );

        try (final var preStatement = dictionaryDBConnection.prepareStatement(removeQueryBM)) {
            preStatement.setInt(1, word.getId());
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
     * result. So client methods and functions can be assured that the result can not grow
     * too large to make machine crash.
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

    /**
     * This search the same as the search method above, the only difference is the limitation of
     * result. So client methods and functions can be assured that the result can not grow
     * too large to make machine crash.
     */
    public List<Word> searchEdit(String searchTerm) {
        final var searchQuery = String.format(
                "SELECT * FROM %s WHERE %s LIKE ? AND %s > '2021-10-20'",
                TABLE_NAME,
                KEY_WORD,
                ADDED_DATE
        );

        return getSearchResultFromDB(searchQuery, searchTerm);
    }



    /* supportive methods and functions */

    private List<Word> getSearchResultFromDB(String searchQuery, String searchTerm) {
        if (Objects.equals(searchTerm, "")) {
            return getHistory();
        }

        var result = new ArrayList<Word>();

        try (final var preStatement = dictionaryDBConnection.prepareStatement(searchQuery)) {
            // search in database
            preStatement.setString(1, searchTerm + "%");
            final var searchResult = preStatement.executeQuery();

            // get result and add to result list
            while (searchResult.next()) {
                final var id = searchResult.getInt(ID);
                final var keyWord = searchResult.getString(KEY_WORD);
                final var description = searchResult.getString(DESCRIPTION);
                final var pronunciation = searchResult.getString(PRONUNCIATION);

                // process sql date
                final var sqlAddedDate = searchResult.getString(ADDED_DATE);
                final var addedDate = new SimpleDateFormat("yyyy-MM-dd").parse(sqlAddedDate);

                result.add(new Word(id, keyWord, description, pronunciation, addedDate));
            }

        } catch (Exception e) {
            error.add(e.getMessage());
            return new ArrayList<>();
        }

        return result;
    }

    /**
     * Return a list of words that match searchTerm.
     */
    public List<String> searchKeyWord(List<String> searchTerms, int limitation) {
        final var searchTermsSize = searchTerms.size();

        // generate skeleton for query
        final var tmp1 = Collections.nCopies(searchTermsSize, "?");
        final var tmp2 = String.join(" OR " + KEY_WORD + " LIKE ", tmp1);

        final var queryForm = String.format(
                "SELECT %s FROM %s WHERE %s LIKE %s LIMIT %d",
                KEY_WORD,
                TABLE_NAME,
                KEY_WORD,
                tmp2,
                limitation
        );

        final var result = new ArrayList<String>();

        try (final var stmt = dictionaryDBConnection.prepareStatement(queryForm)) {
            // add parameters
            // sql counts from 1, why?
            for (var i = 1; i <= searchTermsSize; ++i) {
                stmt.setString(i, searchTerms.get(i - 1));
            }

            // search and parse values
            final var rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(rs.getString(KEY_WORD));
            }

        } catch (Exception e) {
            error.add(e.getMessage());
            return Collections.emptyList();
        }

        return result;
    }

    public Word searchKey(String key) {
        Word word = new Word();

        final var searchQuery = String.format(
                "SELECT * FROM %s WHERE %s = ?",
                TABLE_NAME,
                KEY_WORD
        );

        try (final var preStatement = dictionaryDBConnection.prepareStatement(searchQuery)) {
            // search in database
            preStatement.setString(1, key);
            final var searchResult = preStatement.executeQuery();

            // get result and add to result list
            while (searchResult.next()) {
                word.setId(searchResult.getInt(ID));
                word.setKeyWord(searchResult.getString(KEY_WORD));
                word.setDescription(searchResult.getString(DESCRIPTION));
                word.setPronunciation(searchResult.getString(PRONUNCIATION));

                // process sql date
                final var sqlAddedDate = searchResult.getString(ADDED_DATE);
                final var addedDate = new SimpleDateFormat("yyyy-MM-dd").parse(sqlAddedDate);
                word.setAddedDate(addedDate);
                break;
            }

        } catch (Exception e) {
            error.add(e.getMessage());
            return null;
        }

        return word;
    }

    /** */
    public List<Word> getHistory() {
        List<Word> result = new ArrayList<>();
        try {
            final var sql = "SELECT * FROM history h LEFT JOIN av a ON h.id_word = a.id ORDER BY id_history DESC";
            final var preStatement = dictionaryDBConnection.createStatement();
            final var searchResult = preStatement.executeQuery(sql);

            while (searchResult.next()) {
                final var id = searchResult.getInt(ID);
                final var keyWord = searchResult.getString(KEY_WORD);
                final var description = searchResult.getString(DESCRIPTION);
                final var pronunciation = searchResult.getString(PRONUNCIATION);

                // process sql date
                final var sqlAddedDate = searchResult.getString(ADDED_DATE);
                final var addedDate = new SimpleDateFormat("yyyy-MM-dd").parse(sqlAddedDate);

                result.add(new Word(id, keyWord, description, pronunciation, addedDate));
            }
            return result;
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Word> getBookmark() {
        List<Word> result = new ArrayList<>();
        try {
            final var sql = "SELECT * FROM bookmark b LEFT JOIN av a ON b.id_word = a.id";
            final var preStatement = dictionaryDBConnection.createStatement();
            final var searchResult = preStatement.executeQuery(sql);

            while (searchResult.next()) {
                final var id = searchResult.getInt(ID);
                final var keyWord = searchResult.getString(KEY_WORD);
                final var description = searchResult.getString(DESCRIPTION);
                final var pronunciation = searchResult.getString(PRONUNCIATION);

                // process sql date
                final var sqlAddedDate = searchResult.getString(ADDED_DATE);
                final var addedDate = new SimpleDateFormat("yyyy-MM-dd").parse(sqlAddedDate);

                result.add(new Word(id, keyWord, description, pronunciation, addedDate));
            }
            return result;
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<Word> getWord(int someDay) {
        List<Word> result = new ArrayList<>();
        try {
            final var sql = "SELECT * FROM av WHERE julianday('now') - julianday(date_add) < " + someDay;
            final var preStatement = dictionaryDBConnection.createStatement();
            final var searchResult = preStatement.executeQuery(sql);

            while (searchResult.next()) {
                final var id = searchResult.getInt(ID);
                final var keyWord = searchResult.getString(KEY_WORD);
                final var description = searchResult.getString(DESCRIPTION);
                final var pronunciation = searchResult.getString(PRONUNCIATION);

                // process sql date
                final var sqlAddedDate = searchResult.getString(ADDED_DATE);
                final var addedDate = new SimpleDateFormat("yyyy-MM-dd").parse(sqlAddedDate);

                result.add(new Word(id, keyWord, description, pronunciation, addedDate));
            }
            return result;
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateWord(Word word) {
        final var insertQuery = String.format(
                "UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = DATE() WHERE %s = ?",
                TABLE_NAME,
                KEY_WORD,
                DESCRIPTION,
                PRONUNCIATION,
                ADDED_DATE,
                ID
        );

        try (
                final var preStatement = dictionaryDBConnection.prepareStatement(insertQuery)
        ) {
            preStatement.setString(1, word.getKeyWord());
            preStatement.setString(2, word.getDescription());
            preStatement.setString(3, word.getPronunciation());
            preStatement.setInt(4, word.getId());

            preStatement.executeUpdate();

        } catch (Exception e) {
            error.add(e.getMessage());
            return false;
        }

        return true;
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

    public Alert getAlertInfo(String content, Alert.AlertType type) {
        String notification = "Database: " + content;
        Alert a = new Alert(type);
        a.setContentText(notification);
        return a;
    }
}