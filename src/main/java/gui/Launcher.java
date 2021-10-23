package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Launcher extends javafx.application.Application {
    @Override
    public void start(Stage primaryStage) {
//        try {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/Main.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Dictionary App");
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.show();
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
