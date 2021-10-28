package shared;

import javafx.scene.control.Alert;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DictionaryManager implements AutoCloseable {

    private Dictionary dictionary;
    private EditDatabaseController editDatabaseController;
    private GetDatabaseController getDatabaseController;
    final private List<String> error = new ArrayList<>();
    /* interface */

    /** Constructor creates new connection with the database. */
    public DictionaryManager() {
        try {
            dictionary = new Dictionary(
                    DriverManager.getConnection(Dictionary.DB_URL),
                    error
            );

            editDatabaseController = new EditDatabaseController(dictionary);
            getDatabaseController = new GetDatabaseController(dictionary);

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
                Dictionary.TABLE_NAME,
                Dictionary.KEY_WORD,
                Dictionary.DESCRIPTION,
                Dictionary.PRONUNCIATION,
                Dictionary.ADDED_DATE
        );

        try (
                final var preStatement = dictionary.connection().prepareStatement(insertQuery)
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

    private List<Word> getDataFromResultSet(ResultSet resultSet) {
        var list = new ArrayList<Word>();

        while (true) {
            try {
                if (!resultSet.next()) break;

                final var id = resultSet.getInt(Dictionary.ID);
                final var keyWord = resultSet.getString(Dictionary.KEY_WORD);
                final var description = resultSet.getString(Dictionary.DESCRIPTION);
                final var pronunciation = resultSet.getString(Dictionary.PRONUNCIATION);

                // process sql date
                final var sqlAddedDate = resultSet.getString(Dictionary.ADDED_DATE);
                final var addedDate = new SimpleDateFormat("yyyy-MM-dd").parse(sqlAddedDate);

                list.add(new Word(id, keyWord, description, pronunciation, addedDate));

            } catch (SQLException | ParseException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /** */
    public void insertHistory(Word word) {
        final var insertQuery = String.format(
                "INSERT INTO %s (%s, %s) VALUES (?, DATE())",
                Dictionary.TABLE_NAME_H,
                Dictionary.ID_WORD,
                Dictionary.DATE
        );

        try (
                final var preStatement = dictionary.connection().prepareStatement(insertQuery)
        ) {
            preStatement.setInt(1, word.getId());

            preStatement.executeUpdate();

        } catch (Exception e) {
            error.add(e.getMessage());
        }

    }

    /** */
    public void insertBookmark(Word word) {
        final var insertQuery = String.format(
                "INSERT INTO %s (%s, %s) VALUES (?, DATE())",
                Dictionary.TABLE_NAME_B_M,
                Dictionary.ID_WORD,
                Dictionary.DATE
        );

        try (
                final var preStatement = dictionary.connection().prepareStatement(insertQuery)
        ) {
            preStatement.setInt(1, word.getId());

            preStatement.executeUpdate();

        } catch (Exception e) {
            error.add(e.getMessage());
        }

    }

    /**
     *  Remove word method takes a string as key word to find and remove word in dictionary database.
     *  Like insert word, this method also interacts directly with the database. So it should
     *  also be avoided.
     */
    public boolean removeWord(String keyWord) {
        final var removeQuery = String.format(
                "DELETE FROM %s WHERE %s = ?",
                Dictionary.TABLE_NAME,
                Dictionary.KEY_WORD
        );

        try (final var preStatement = dictionary.connection().prepareStatement(removeQuery)) {
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
                Dictionary.TABLE_NAME,
                Dictionary.KEY_WORD
        );

        try (final var preStatement = dictionary.connection().prepareStatement(removeQuery)) {
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
                Dictionary.TABLE_NAME_H,
                Dictionary.ID_WORD
        );

        try (final var preStatement = dictionary.connection().prepareStatement(removeQueryH)) {
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
                Dictionary.TABLE_NAME_B_M,
                Dictionary.ID_WORD
        );

        try (final var preStatement = dictionary.connection().prepareStatement(removeQueryBM)) {
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
                Dictionary.TABLE_NAME,
                Dictionary.KEY_WORD
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
                Dictionary.TABLE_NAME,
                Dictionary.KEY_WORD,
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
                Dictionary.TABLE_NAME,
                Dictionary.KEY_WORD,
                Dictionary.ADDED_DATE
        );

        return getSearchResultFromDB(searchQuery, searchTerm);
    }



    /* supportive methods and functions */

    private List<Word> getSearchResultFromDB(String searchQuery, String searchTerm) {
        if (Objects.equals(searchTerm, "")) {
            return getHistory();
        }

        var result = new ArrayList<Word>();

        try (final var preStatement = dictionary.connection().prepareStatement(searchQuery)) {
            // search in database
            preStatement.setString(1, searchTerm + "%");
            final var searchResult = preStatement.executeQuery();

            // get result and add to result list
//            while (searchResult.next()) {
//                final var id = searchResult.getInt(ID);
//                final var keyWord = searchResult.getString(KEY_WORD);
//                final var description = searchResult.getString(DESCRIPTION);
//                final var pronunciation = searchResult.getString(PRONUNCIATION);
//
//                // process sql date
//                final var sqlAddedDate = searchResult.getString(ADDED_DATE);
//                final var addedDate = new SimpleDateFormat("yyyy-MM-dd").parse(sqlAddedDate);
//
//                result.add(new Word(id, keyWord, description, pronunciation, addedDate));
//            }
            result = (ArrayList<Word>) getDataFromResultSet(searchResult);

        } catch (Exception e) {
            error.add(e.getMessage());
            return Collections.emptyList();
        }

        return result;
    }

    /**
     * Return a list of words that match searchTerms.
     */
    public List<String> searchKeyWord(List<String> searchTerms, int limit) {
        final var searchTermsSize = searchTerms.size();

        final var tmp1 = Collections.nCopies(searchTermsSize, "?");
        final var tmp2 = String.join(" OR " + Dictionary.KEY_WORD + " LIKE ", tmp1);

        final var queryForm = String.format(
                "SELECT %s FROM %s WHERE %s LIKE %s LIMIT %d",
                Dictionary.KEY_WORD,
                Dictionary.TABLE_NAME,
                Dictionary.KEY_WORD,
                tmp2,
                limit
        );

        final var result = new ArrayList<String>();

        try (final var stmt = dictionary.connection().prepareStatement(queryForm)) {
            for (var i = 1; i <= searchTermsSize; ++i) {
                stmt.setString(i, searchTerms.get(i - 1));
            }

            final var rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(rs.getString(Dictionary.KEY_WORD));
            }

            rs.close();

        } catch (Exception e) {
            error.add(e.getMessage());
            return Collections.emptyList();
        }

        return result;
    }

    /** */
    public Word searchKey(String key) {
        Word word;

        final var searchQuery = String.format(
                "SELECT * FROM %s WHERE %s = ?",
                Dictionary.TABLE_NAME,
                Dictionary.KEY_WORD
        );

        try (final var preStatement = dictionary.connection().prepareStatement(searchQuery)) {
            // search in database
            preStatement.setString(1, key);
            final var searchResult = preStatement.executeQuery();

            // get result and add to result list
//            while (searchResult.next()) {
//                word.setId(searchResult.getInt(ID));
//                word.setKeyWord(searchResult.getString(KEY_WORD));
//                word.setDescription(searchResult.getString(DESCRIPTION));
//                word.setPronunciation(searchResult.getString(PRONUNCIATION));
//
//                // process sql date
//                final var sqlAddedDate = searchResult.getString(ADDED_DATE);
//                final var addedDate = new SimpleDateFormat("yyyy-MM-dd").parse(sqlAddedDate);
//                word.setAddedDate(addedDate);
//                break;
//            }
            word = getDataFromResultSet(searchResult).get(0);

        } catch (Exception e) {
            error.add(e.getMessage());
            return new Word();
        }

        return word;
    }

    /** */
    public List<Word> getHistory() {
        final var query = String.format(
                "SELECT * FROM %s h LEFT JOIN %s a ON h.%s = a.%s ORDER BY id_history DESC",
                Dictionary.TABLE_NAME_H,
                Dictionary.TABLE_NAME,
                Dictionary.ID_WORD,
                Dictionary.ID
        );

        try (
                final var preStatement = dictionary.connection().createStatement()
        ){

            final var searchResult = preStatement.executeQuery(query);

//            while (searchResult.next()) {
//                final var id = searchResult.getInt(ID);
//                final var keyWord = searchResult.getString(KEY_WORD);
//                final var description = searchResult.getString(DESCRIPTION);
//                final var pronunciation = searchResult.getString(PRONUNCIATION);
//
//                // process sql date
//                final var sqlAddedDate = searchResult.getString(ADDED_DATE);
//                final var addedDate = new SimpleDateFormat("yyyy-MM-dd").parse(sqlAddedDate);
//
//                result.add(new Word(id, keyWord, description, pronunciation, addedDate));
//            }
//            return result;
            return getDataFromResultSet(searchResult);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /** */
    public List<Word> getBookmark() {
        final var query = String.format(
                "SELECT * FROM %s b LEFT JOIN %s a ON b.%s = a.%s",
                Dictionary.TABLE_NAME_B_M,
                Dictionary.TABLE_NAME,
                Dictionary.ID_WORD,
                Dictionary.ID
        );

        try(
                final var preStatement = dictionary.connection().createStatement()
        ) {

            final var searchResult = preStatement.executeQuery(query);

//            while (searchResult.next()) {
//                final var id = searchResult.getInt(ID);
//                final var keyWord = searchResult.getString(KEY_WORD);
//                final var description = searchResult.getString(DESCRIPTION);
//                final var pronunciation = searchResult.getString(PRONUNCIATION);
//
//                // process sql date
//                final var sqlAddedDate = searchResult.getString(ADDED_DATE);
//                final var addedDate = new SimpleDateFormat("yyyy-MM-dd").parse(sqlAddedDate);
//
//                result.add(new Word(id, keyWord, description, pronunciation, addedDate));
//            }
//            return result;

            return getDataFromResultSet(searchResult);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /** */
    public List<Word> getWord(int someDay) {

        try (
                final var preStatement = dictionary.connection().createStatement()
        ) {
            final var sql = "SELECT * FROM av WHERE julianday('now') - julianday(date_add) < " + someDay;
            final var searchResult = preStatement.executeQuery(sql);

//            while (searchResult.next()) {
//                final var id = searchResult.getInt(ID);
//                final var keyWord = searchResult.getString(KEY_WORD);
//                final var description = searchResult.getString(DESCRIPTION);
//                final var pronunciation = searchResult.getString(PRONUNCIATION);
//
//                // process sql date
//                final var sqlAddedDate = searchResult.getString(ADDED_DATE);
//                final var addedDate = new SimpleDateFormat("yyyy-MM-dd").parse(sqlAddedDate);
//
//                result.add(new Word(id, keyWord, description, pronunciation, addedDate));
//            }
            return getDataFromResultSet(searchResult);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /** */
    public boolean updateWord(Word word) {
        final var insertQuery = String.format(
                "UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = DATE() WHERE %s = ?",
                Dictionary.TABLE_NAME,
                Dictionary.KEY_WORD,
                Dictionary.DESCRIPTION,
                Dictionary.PRONUNCIATION,
                Dictionary.ADDED_DATE,
                Dictionary.ID
        );

        try (
                final var preStatement = dictionary.connection().prepareStatement(insertQuery)
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
        dictionary.close();
    }

    public Alert getAlertInfo(String content, Alert.AlertType type) {
        String notification = "Database: " + content;
        Alert a = new Alert(type);
        a.setContentText(notification);
        return a;
    }
}