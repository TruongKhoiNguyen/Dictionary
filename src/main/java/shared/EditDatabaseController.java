package shared;

public record EditDatabaseController(Dictionary dictionary) {

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
            dictionary.error().add(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Insert word method takes a parameter type word, and insert id_word and description to
     * dictionary history database.
     */
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
            dictionary.error().add(e.getMessage());
        }

    }

    /**
     * Insert word method takes a parameter type word, and insert id_word and description to
     * dictionary bookmark database.
     */
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
            dictionary.error().add(e.getMessage());
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
            dictionary.error().add(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Remove word method takes Word to find and remove word in dictionary database.
     */
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
            dictionary.error().add(e.getMessage());
            return false;
        }

        return removeWordFromHistory(word) && removeWordFromBookmark(word);
    }

    /**
     * Remove word method takes Word to find and remove word in dictionary history database.
     */
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
            dictionary.error().add(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Remove word method takes Word to find and remove word in dictionary bookmark database.
     */
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
            dictionary.error().add(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Update Word's changes to the dictionary database.
     */
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
            dictionary.error().add(e.getMessage());
            return false;
        }

        return true;
    }

}
