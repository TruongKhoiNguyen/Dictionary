package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import shared.DictionaryManager;
import shared.Word;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    private final ObservableList<String> observableList = FXCollections.observableArrayList("3D ago",
            "1W ago",
            "1M ago",
            "History",
            "Bookmark");

    private Word wordCurrent = null;
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
        setUpShowWord();


        tfSearchE.textProperty().addListener((observableValue, s, t1) -> {
            btnSearchE.setVisible(true);
            if (!t1.isEmpty()) {
                wordList = dictionaryManager.searchEdit(tfSearchE.getText().trim());
                wordObservableList = FXCollections.observableList(wordList);
                lvShow.setItems(wordObservableList);
            } else {
                setUpShowWord();
                btnSearchE.setVisible(false);
            }
        });

        tfEditWord.textProperty().addListener((observableValue, s, t1) -> {
            if (!t1.isEmpty()) {
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

    public void onActionBtnSearchE() {
        try {
            String searchKey = tfSearchE.getText();
            List<Word> words = dictionaryManager.searchEdit(searchKey);

            if (!words.isEmpty()) {
                Word word = words.get(0);
                wordCurrent = word;

                tfEditWord.setText(word.getKeyWord());
                tfEditDescription.setText(word.getDescription());
                tfEditPronunciation.setText(word.getPronunciation());
            } else {
                Alert alert = dictionaryManager.getAlertInfo("Word is wrong!", Alert.AlertType.INFORMATION);
                alert.show();
            }
        } catch (Exception e) {
            Alert alert = dictionaryManager.getAlertInfo("Error", Alert.AlertType.ERROR);
            alert.show();
        }
    }

    public void onActionBtnInsert() {
        if (cbbHistoryEdit.getValue().equals("History")
                || cbbHistoryEdit.getValue().equals("Bookmark")) {
            String content = "No edit in history and bookmark";
            Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.INFORMATION);
            alert.show();

            return;
        }

        String keyWord = tfEditWord.getText();
        String description = tfEditDescription.getText();
        String pronunciation = tfEditPronunciation.getText();

        if (!(keyWord.isEmpty() || description.isEmpty())) {
            Word word = new Word(keyWord, description, pronunciation);
            if (dictionaryManager.insertWord(word)) {
                String content = "Insert completed";
                Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.INFORMATION);
                alert.show();

                resetEdit();
            } else {
                String content = "Insert failed";
                Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.ERROR);
                alert.show();
            }
        } else {
            String content = "Keyword or description is missing";
            Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.WARNING);
            alert.show();
        }
    }

    public void onActionBtnUpdate() {
        if (cbbHistoryEdit.getValue().equals("History")
                || cbbHistoryEdit.getValue().equals("Bookmark")) {
            String content = "No edit in history and bookmark";
            Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.INFORMATION);
            alert.show();

            return;
        }

        if (wordCurrent != null) {
            wordCurrent.setKeyWord(tfEditWord.getText());
            wordCurrent.setDescription(tfEditDescription.getText());
            wordCurrent.setPronunciation(tfEditPronunciation.getText());

            if (dictionaryManager.updateWord(wordCurrent)) {
                String content = "Update successful!";
                Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.INFORMATION);
                alert.show();

                resetEdit();
            } else {
                String content = "Update failed";
                Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.ERROR);
                alert.show();
            }
        } else {
            String content = "Please choose word";
            Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.WARNING);
            alert.show();
        }
    }

    public void onActionBtnDelete() {
        if (wordCurrent != null) {
            if (cbbHistoryEdit.getValue().equals("History")) {
                if (onActionRemoveHistory(wordCurrent)) {
                    String content = "remove form history successful!";
                    Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.INFORMATION);
                    alert.show();

                    resetEdit();
                } else {
                    String content = "remove form history failed!";
                    Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.ERROR);
                    alert.show();
                }
            } else if (cbbHistoryEdit.getValue().equals("Bookmark")) {
                if (onActionRemoveBookmark(wordCurrent)) {
                    String content = "remove form bookmark successful!";
                    Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.INFORMATION);
                    alert.show();

                    resetEdit();
                } else {
                    String content = "remove form bookmark failed!";
                    Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.ERROR);
                    alert.show();
                }
            } else {
                wordList = dictionaryManager.searchEdit(wordCurrent.getKeyWord());
                if (!wordList.isEmpty()) {
                    if (dictionaryManager.removeWord(wordCurrent)) {
                        String content = "Delete successful!";
                        Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.INFORMATION);
                        alert.show();

                        resetEdit();
                    } else {
                        String content = "Delete failed";
                        Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.ERROR);
                        alert.show();
                    }

                } else {
                    String content = "Word does not exist";
                    Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.WARNING);
                    alert.show();
                }
            }
        } else {
            String content = "Please choose word";
            Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.WARNING);
            alert.show();
        }
    }

    public void resetEdit() {
        tfEditWord.clear();
        tfEditDescription.clear();
        tfEditPronunciation.clear();
        wordCurrent = null;

        setUpShowWord();
    }

    public boolean onActionRemoveHistory(Word word) {
        List<Word> listHistory = dictionaryManager.getHistory();

        for (Word word1 : listHistory) {
            if (word.getKeyWord().equals(word1.getKeyWord())) {
                return dictionaryManager.removeWordFromHistory(word1);
            }
        }

        return false;
    }

    public boolean onActionRemoveBookmark(Word word) {
        List<Word> listBookmark = dictionaryManager.getBookmark();

        for (Word word1 : listBookmark) {
            if (word.getKeyWord().equals(word1.getKeyWord())) {
                return dictionaryManager.removeWordFromBookmark(word1);
            }
        }

        return false;
    }

    public void onActionCbbChooseEdit() {
        tfEditWord.clear();
        tfEditDescription.clear();
        tfEditPronunciation.clear();
        wordCurrent = null;

        setUpShowWord();
    }

    public void setUpShowWord() {
        String cbbTextCurrent = cbbHistoryEdit.getValue();
        if (cbbTextCurrent.equals("History")) {
            wordList = dictionaryManager.getHistory();
            wordObservableList = FXCollections.observableArrayList(wordList);
            lvShow.setItems(wordObservableList);
        } else if (cbbTextCurrent.equals("Bookmark")) {
            wordList = dictionaryManager.getBookmark();
            wordObservableList = FXCollections.observableArrayList(wordList);
            lvShow.setItems(wordObservableList);
        } else {
            int setTime = setTimeGetWord(cbbTextCurrent);
            wordList = dictionaryManager.getWord(setTime);
            wordObservableList = FXCollections.observableArrayList(wordList);
            lvShow.setItems(wordObservableList);
        }
    }

    public void onActionChooseCellE() {
        try {
            Word word = lvShow.getSelectionModel().getSelectedItem();
            wordCurrent = word;

            tfEditWord.setText(word.getKeyWord());
            tfEditDescription.setText(word.getDescription());
            tfEditPronunciation.setText(word.getPronunciation());
        } catch (Exception ignored) {}
    }

    public int setTimeGetWord(String time) {
        switch (time) {
            case "3D ago" -> {
                return 2;
            }
            case "1W ago" -> {
                return 4;
            }
            case "1M ago" -> {
                return 6;
            }
        }
        return 3;
    }
}
