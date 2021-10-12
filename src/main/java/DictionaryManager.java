package main.java;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Optional;

public class DictionaryManager {
    private static final String DB_NAME = "edict.db";
    private static final String TABLE_NAME = "tbl_edict";
    private static final String EN_WORD = "word";
    private static final String VI_WORD = "detail";


    Optional<Connection> dictionaryDBConnection;

    /** DictionaryManager :: DictionaryManager */
    public DictionaryManager() {
        final var dbUrl = String.format(
            "jdbc:sqlite:../resources/%s",
            DB_NAME
        );

        try {
            var connection = DriverManager.getConnection(dbUrl);
            dictionaryDBConnection = Optional.of(connection);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            dictionaryDBConnection = Optional.empty();
        }
    }

    /** finalized :: dictionaryDBConnection -> dictionaryDBConnection */
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

    /** insertWord :: dictionaryDBConnection -> String -> String -> IO String */
    /* Return "" if the method successfully insert word */
    public String insertWord(String word, String detail) {
        if (dictionaryDBConnection.isEmpty()) {
            return "Database not connected.";
        }

        final var query = createInsertQuery(word, detail);

        try {
            final var statement = dictionaryDBConnection.get().createStatement();
            statement.executeUpdate(query);

            statement.close();

        } catch (Exception e) {
            return e.getMessage();
        }

        return "";
    }

    /** createInsertQuery :: String -> String -> String */
    private static String createInsertQuery(String word, String detail) {
        return String.format(
            "insert into %s (%s, %s) values ('%s', '%s')",
            TABLE_NAME,
            EN_WORD,
            VI_WORD,
            word.toLowerCase(),
            detail.toLowerCase()
        );
    }

    /** removeWord :: dictionaryDBConnection -> String -> IO String */
    /* Return "" if the method successfully remove word */
    public String removeWord(String word) {
        if (dictionaryDBConnection.isEmpty()) {
            return "Database not connected.";
        }

        final var query = createRemoveQuery(word);

        try {
            final var statement = dictionaryDBConnection.get().createStatement();
            statement.executeUpdate(query);

            statement.close();

        } catch (Exception e) {
            return e.getMessage();
        }

        return "";
    }

    /** createRemoveQuery :: String -> String */
    private static String createRemoveQuery(String word) {
        return String.format(
            "delete from %s where word = '%s'",
            TABLE_NAME,
            word.toLowerCase()
        );
    }

    /** search :: String -> IO Either Map(String, String) Error */
    /* If key "Error!" is not "" then the method return error */
    public HashMap<String, String> search(String searchTerm) {
        final var result = new HashMap<String, String>();

        if (dictionaryDBConnection.isEmpty()) {
            result.put("Error!", "Database not connected.");
            return result;
        }

        final var searchQuery = createSearchQuery(searchTerm);

        try {
            final var statement = dictionaryDBConnection.get().createStatement();
            final var resultSet = statement.executeQuery(searchQuery);

            while (resultSet.next()) {
                final var word = resultSet.getString(EN_WORD);
                final var detail = resultSet.getString(VI_WORD);
                // System.out.printf("%s %s", word, detail);
                result.put(word, detail);
            }

            statement.close();
            resultSet.close();

        } catch (Exception e) {
            result.put("Error!", e.getMessage());
        }

        return result;
    }

    /** createSearchQuery:: String -> String */
    private static String createSearchQuery(String searchTerm) {
        return String.format(
            "select %s, %s from %s where %s like '%s%%'",
            EN_WORD,
            VI_WORD,
            TABLE_NAME,
            EN_WORD,
            searchTerm
        );
    }
}

/* 
DATA

database : Connection
operationResult: String
searchResult: Map(String, String)
callDatabaseIO: String -> IO Maybe ResultSet Error

FLOW

insertWord:
(word, detail) -> query -> ResultSet or Error -> operationResult
database -> callDatabaseIO -> ResultSet or Error

removeWord:
word -> query -> ResultSet or Error -> operationResult
database -> callDatabaseIO -> ResultSet or Error

search:
word -> query -> ResultSet or Error -> searchResult(maybe error)
database -> callDatabaseIO -> ResultSet or Error

FUNCTION

insertWord = parseResult callDatabaseIO createInsertQuery

removeWord = parseResult callDatabaseIO createRemoveQuery

search = parseResult callDatabaseIO createSearchQuery
*/