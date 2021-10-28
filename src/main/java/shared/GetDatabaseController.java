package shared;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record GetDatabaseController(Dictionary dictionary) {
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

            result = (ArrayList<Word>) getDataFromResultSet(searchResult);

        } catch (Exception e) {
            dictionary.error().add(e.getMessage());
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
            dictionary.error().add(e.getMessage());
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

            word = getDataFromResultSet(searchResult).get(0);

        } catch (Exception e) {
            dictionary.error().add(e.getMessage());
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

            return getDataFromResultSet(searchResult);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
