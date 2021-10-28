package shared;

import javafx.scene.control.Alert;

import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

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
        return editDatabaseController.insertWord(word);
    }


    /** */
    public void insertHistory(Word word) {
        editDatabaseController.insertHistory(word);
    }

    /** */
    public void insertBookmark(Word word) {
        editDatabaseController.insertBookmark(word);
    }

    /**
     *  Remove word method takes a string as key word to find and remove word in dictionary database.
     *  Like insert word, this method also interacts directly with the database. So it should
     *  also be avoided.
     */
    public boolean removeWord(String keyWord) {
        return editDatabaseController.removeWord(keyWord);
    }

    /** */
    public boolean removeWord(Word word) {
        return editDatabaseController.removeWord(word);
    }

    public boolean removeWordFromHistory(Word word) {
        return editDatabaseController.removeWordFromHistory(word);
    }

    public boolean removeWordFromBookmark(Word word) {
        return editDatabaseController.removeWordFromBookmark(word);
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
        return getDatabaseController.search(searchTerm);
    }


    /**
     * This search the same as the search method above, the only difference is the limitation of
     * result. So client methods and functions can be assured that the result can not grow
     * too large to make machine crash.
     */
    public List<Word> search(String searchTerm, int limitation) {
        return getDatabaseController.search(searchTerm, limitation);
    }

    /**
     * This search the same as the search method above, the only difference is the limitation of
     * result. So client methods and functions can be assured that the result can not grow
     * too large to make machine crash.
     */
    public List<Word> searchEdit(String searchTerm) {

        return getDatabaseController.searchEdit(searchTerm);
    }


    /**
     * Return a list of words that match searchTerms.
     */
    public List<String> searchKeyWord(List<String> searchTerms, int limit) {
        return getDatabaseController.searchKeyWord(searchTerms, limit);
    }

    /** */
    public Word searchKey(String key) {
        return getDatabaseController.searchKey(key);
    }

    /** */
    public List<Word> getHistory() {
        return getDatabaseController.getHistory();
    }

    /** */
    public List<Word> getBookmark() {
        return getDatabaseController.getBookmark();
    }

    /** */
    public List<Word> getWord(int someDay) {
        return getDatabaseController.getWord(someDay);
    }

    /** */
    public boolean updateWord(Word word) {
        return editDatabaseController.updateWord(word);
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