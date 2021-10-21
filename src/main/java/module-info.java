module untitled104 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.jfoenix;
    requires java.sql;


    opens gui to javafx.fxml;
    exports gui;
}