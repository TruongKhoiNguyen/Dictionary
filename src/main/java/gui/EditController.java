package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import shared.DictionaryManager;
import shared.Word;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class EditController implements Initializable {
    private final static int _3_DAY_AGO = 3;
    private final static int _1_WEEK_AGO = 7;
    private final static int _1_MONTH_AGO = 30;

    @FXML
    ListView<Word> lvShow;
    @FXML
    ComboBox<String> cbbHistoryEdit;
    @FXML
    Button btnSearchE;
    @FXML
    Button btnInsert;
    @FXML
    Button btnUpdate;
    @FXML
    Button btnDelete;
    @FXML
    TextField tfSearchE;
    @FXML
    TextField tfEditWord;
    @FXML
    TextField tfEditDescription;
    @FXML
    TextField tfEditPronunciation;

    private final DictionaryManager dictionaryManager = new DictionaryManager();
    private final ObservableList<String> observableList = FXCollections.observableArrayList("3D ago", "1W ago", "1M ago");

    private Word wordNow = null;
    private List<Word> wordList = new ArrayList<>();
    private ObservableList<Word> wordObservableList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnSearchE.setVisible(false);
        btnInsert.setDisable(true);
        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);

        cbbHistoryEdit.setValue("3D ago");
        cbbHistoryEdit.setItems(observableList);
        int setTime = setTimeGetWord(cbbHistoryEdit.getValue());
        wordList = dictionaryManager.getWord(setTime);
        wordObservableList = FXCollections.observableArrayList(wordList);
        lvShow.setItems(wordObservableList);


        tfSearchE.textProperty().addListener((observableValue, s, t1) -> {
            btnSearchE.setVisible(true);
            if (t1 != "") {
                wordList = dictionaryManager.searchEdit(tfSearchE.getText().trim());
                wordObservableList = FXCollections.observableList(wordList);
                lvShow.setItems(wordObservableList);
            } else {
                int setTimeA = setTimeGetWord(cbbHistoryEdit.getValue());
                wordList = dictionaryManager.getWord(setTimeA);
                wordObservableList = FXCollections.observableArrayList(wordList);
                lvShow.setItems(wordObservableList);
                btnSearchE.setVisible(false);
            }
        });

        tfEditWord.textProperty().addListener((observableValue, s, t1) -> {
            if (!Objects.equals(t1, "")) {
                btnInsert.setDisable(false);
                btnDelete.setDisable(false);
                btnUpdate.setDisable(false);
            } else {
                btnInsert.setDisable(true);
                btnDelete.setDisable(true);
                btnUpdate.setDisable(true);
            }
        });
    }

    public void onActionBtnSearchE(ActionEvent event) {
        try {
            String searchKey = tfSearchE.getText();
            List<Word> words = dictionaryManager.searchEdit(searchKey);

            if (!words.isEmpty()) {
                Word word = words.get(0);
                wordNow = word;

                // edit word
                tfEditWord.setText(word.getKeyWord());
                tfEditDescription.setText(word.getDescription());
                tfEditPronunciation.setText(word.getPronunciation());
            } else {
                Alert alert = dictionaryManager.getAlertInfo("Word was wrong!", Alert.AlertType.INFORMATION);
                alert.show();
            }
        } catch (Exception e) {
            Alert alert = dictionaryManager.getAlertInfo("Error", Alert.AlertType.ERROR);
            alert.show();
        }
    }

    public void onActionBtnInsert(ActionEvent event) {
        String keyWord = tfEditWord.getText();
        String description = tfEditDescription.getText();
        String pronunciation = tfEditPronunciation.getText();

        if (!(keyWord.equals("") || description.equals(""))) {
            Word word = new Word(keyWord, description, pronunciation);
            if (dictionaryManager.insertWord(word)) {
                String content = "";
                Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.INFORMATION);
                alert.show();

                tfEditWord.clear();
                tfEditDescription.clear();
                tfEditPronunciation.clear();
                wordNow = null;
                int setTime = setTimeGetWord(cbbHistoryEdit.getValue());
                wordList = dictionaryManager.getWord(setTime);
                wordObservableList = FXCollections.observableArrayList(wordList);
                lvShow.setItems(wordObservableList);
            } else {
                String content = "";
                Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.ERROR);
                alert.show();
            }
        } else {
            String content = "";
            Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.WARNING);
            alert.show();
        }
    }

    public void onActionBtnUpdate(ActionEvent event) {
        if (wordNow != null) {
            wordNow.setKeyWord(tfEditWord.getText());
            wordNow.setDescription(tfEditDescription.getText());
            wordNow.setPronunciation(tfEditPronunciation.getText());

            if (dictionaryManager.updateWord(wordNow)) {
                String content = "Update successful!";
                Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.INFORMATION);
                alert.show();

                tfEditWord.clear();
                tfEditDescription.clear();
                tfEditPronunciation.clear();
                wordNow = null;

                int setTime = setTimeGetWord(cbbHistoryEdit.getValue());
                wordList = dictionaryManager.getWord(setTime);
                wordObservableList = FXCollections.observableArrayList(wordList);
                lvShow.setItems(wordObservableList);
            } else {
                String content = "";
                Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.ERROR);
                alert.show();
            }
        } else {
            String content = "Please choose word";
            Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.WARNING);
            alert.show();
        }
    }

    public void onActionBtnDelete(ActionEvent event) {
        if (wordNow != null) {
            wordList = dictionaryManager.searchEdit(wordNow.getKeyWord());
            if (!wordList.isEmpty()) {
                if (dictionaryManager.removeWord(wordNow)) {
                    String content = "Delete successful!";
                    Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.INFORMATION);
                    alert.show();

                    tfEditWord.clear();
                    tfEditDescription.clear();
                    tfEditPronunciation.clear();
                    wordNow = null;
                    int setTime = setTimeGetWord(cbbHistoryEdit.getValue());
                    wordList = dictionaryManager.getWord(setTime);
                    wordObservableList = FXCollections.observableArrayList(wordList);
                    lvShow.setItems(wordObservableList);
                } else {
                    String content = "";
                    Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.ERROR);
                    alert.show();
                }

            } else {
                String content = "";
                Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.WARNING);
                alert.show();
            }
        } else {
            String content = "Please choose word";
            Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.WARNING);
            alert.show();
        }
    }

    public void onActionCbbChooseTime(ActionEvent event) {
        int setTime = setTimeGetWord(cbbHistoryEdit.getValue());
        wordList = dictionaryManager.getWord(setTime);
        wordObservableList = FXCollections.observableArrayList(wordList);
        lvShow.setItems(wordObservableList);
    }

    public void onActionChooseCellE() {
        try {
            Word word = lvShow.getSelectionModel().getSelectedItem();
            wordNow = word;

            // edit word
            tfEditWord.setText(word.getKeyWord());
            tfEditDescription.setText(word.getDescription());
            tfEditPronunciation.setText(word.getPronunciation());
        } catch (Exception e) {
            Alert alert = dictionaryManager.getAlertInfo("Row is empty!", Alert.AlertType.WARNING);
            alert.show();
        }
    }

    public int setTimeGetWord(String time) {
        switch (time) {
            case "3D ago" -> {
                return 2;
            }
            case "1W ago" -> {
                return 3;
            }
            case "1M ago" -> {
                return 4;
            }
        }
        return 3;
    }
}
