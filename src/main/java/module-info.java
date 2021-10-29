module Dictionary {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.jfoenix;
    requires java.sql;
    requires jsapi;


    opens gui to javafx.fxml;
    exports gui;
    exports shared;
    exports cli;
}