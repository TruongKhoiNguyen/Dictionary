package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import shared.Word;
import translator.GoogleTranslate;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TranslateController implements Initializable {
    private final static String VIETNAMESE = "vi";
    private final static String ENGLISH = "en";
    private final static String JAPANESE = "ja";
    private final static String CHINESE = "zh-CN";
    private final static String KOREAN = "ko";
    private final static String THAILAND = "th";
    private ObservableList<String> languageList = FXCollections.observableArrayList("Vietnamese", "English", "Japanese",
            "Chinese", "Thailand", "Korean");

    @FXML
    ComboBox<String> cbbLanguageTarget;
    @FXML
    ComboBox<String> cbbLanguageExplain;
    @FXML
    Button btnTranslate;
    @FXML
    TextArea taTarget;
    @FXML
    TextArea taExplain;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbbLanguageTarget.setValue("English");
        cbbLanguageExplain.setValue("Vietnamese");
        cbbLanguageExplain.setItems(languageList);
        cbbLanguageTarget.setItems(languageList);
        btnTranslate.setVisible(false);

        taTarget.textProperty().addListener((observableValue, s, t1) -> {
            if (t1 != "") {
                btnTranslate.setVisible(true);
            } else {
                btnTranslate.setVisible(false);
            }
        });
    }

    public void onActionTranslate(ActionEvent event) {
        String keyTarget = keyLanguage(cbbLanguageTarget.getValue());
        String keyExplain = keyLanguage(cbbLanguageExplain.getValue());

        if (taTarget.getText() != null) {
            try {
                String translating = GoogleTranslate.translate(keyTarget, keyExplain, taTarget.getText());
                taExplain.setText(translating);
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = GoogleTranslate.getAlertInfo("Error", Alert.AlertType.ERROR);
                alert.show();
            }
        } else {
            Alert alert = GoogleTranslate.getAlertInfo("Error", Alert.AlertType.ERROR);
            alert.show();
        }
    }

    public String keyLanguage(String language) {
        switch (language) {
            case "Vietnamese" -> {
                return VIETNAMESE;
            }
            case "English" -> {
                return ENGLISH;
            }
            case "Japanese" -> {
                return JAPANESE;
            }
            case "Chinese" -> {
                return CHINESE;
            }
            case "Thailand" -> {
                return THAILAND;
            }
            case "Korean" -> {
                return KOREAN;
            }
        }

        return ENGLISH;
    }
}
