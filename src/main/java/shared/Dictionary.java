package shared;

import java.sql.Connection;
import java.util.List;

public record Dictionary(Connection connection, List<String> error) implements AutoCloseable {
    public static final String DB_NAME = "src/main/resources/dict_hh.db";
    public static final String TABLE_NAME = "av";
    public static final String TABLE_NAME_H = "history";
    public static final String TABLE_NAME_B_M = "bookmark";
    public static final String DB_URL = String.format("jdbc:sqlite:%s", DB_NAME);

    public static final String ID = "id";
    public static final String KEY_WORD = "word";
    public static final String DESCRIPTION = "description";
    public static final String PRONUNCIATION = "pronounce";
    public static final String ADDED_DATE = "date_add";

    public static final String ID_WORD = "id_word";
    public static final String DATE = "date";

    public void close() {
        try {
            connection.close();
        } catch (Exception ignored) {}
    }
}