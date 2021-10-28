package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import shared.DictionaryManager;
import shared.SpellChecker;
import shared.VoiceSpeaker;
import shared.Word;

import java.net.URL;
import java.util.*;

public class LookupController implements Initializable {
    @FXML
    Button btnSearch;
    @FXML
    ListView<Word> lvShowWord;
    @FXML
    TextField tfSearch;
    @FXML
    TextArea taDescription;
    @FXML
    Button btnBookmark;
    @FXML
    Button btnBookmarkList;
    @FXML
    Button btnSpeech;
    @FXML
    ImageView imgSpell;

    private final DictionaryManager dictionaryManager = new DictionaryManager();

    private Word wordCurrent = null;
    private List<Word> wordList = new ArrayList<>();
    private ObservableList<Word> wordObservableList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnSearch.setVisible(false);
        imgSpell.setVisible(false);
        wordList = dictionaryManager.getHistory();
        wordObservableList = FXCollections.observableList(wordList);
        lvShowWord.setItems(wordObservableList);

        tfSearch.textProperty().addListener((observableValue, s, t1) -> {
            btnSearch.setVisible(true);
            if (!t1.isEmpty()) {
                wordList = dictionaryManager.search(tfSearch.getText().trim(), 20);
                if (!wordList.isEmpty()) {
                    imgSpell.setVisible(false);
                } else {
                    imgSpell.setVisible(true);
                    wordList = spellCheck(tfSearch.getText().trim());
                }
                wordObservableList = FXCollections.observableList(wordList);
                lvShowWord.setItems(wordObservableList);
            } else {
                wordList = dictionaryManager.getHistory();
                wordObservableList = FXCollections.observableList(wordList);
                lvShowWord.setItems(wordObservableList);
                btnSearch.setVisible(false);
            }
        });
    }


    public List<Word> spellCheck(String key) {
        List<Word> list = new ArrayList<>();
        SpellChecker spellChecker = new SpellChecker(dictionaryManager);
        List<String> listSuggest = spellChecker.correctSpelling(key);

        for (String s : listSuggest) {
            list.add(dictionaryManager.searchKey(s));
        }

        return list;
    }

    public void onActionChooseCell() {
        try {
            Word word = lvShowWord.getSelectionModel().getSelectedItem();
            dictionaryManager.insertHistory(word);
            wordCurrent = word;
            tfSearch.setText(word.getKeyWord());

            String selectWord = word.getKeyWord();
            if (!word.getPronunciation().isEmpty()) {
                selectWord += "/" + word.getPronunciation() + "/";
            }
            selectWord += "\n" + word.getDescription() + ".";

            taDescription.setText(selectWord);
        } catch (Exception e){
            Alert alert = dictionaryManager.getAlertInfo("Row is empty!", Alert.AlertType.WARNING);
            alert.show();
        }
    }

    public void onActionBtnSearch() {
        try {
            String searchKey = tfSearch.getText();
            List<Word> words = dictionaryManager.search(searchKey);

            if (!words.isEmpty()) {
                Word word = words.get(0);
                dictionaryManager.insertHistory(wordList.get(0));
                wordCurrent = word;

                String selectWord = word.getKeyWord();
                if (!word.getPronunciation().isEmpty()) {
                    selectWord += "/" + word.getPronunciation() + "/";
                }
                selectWord += "\n" + word.getDescription() + ".";

                taDescription.setText(selectWord);
            } else {
                Alert alert = dictionaryManager.getAlertInfo("Word was wrong!", Alert.AlertType.INFORMATION);
                alert.show();
            }
        } catch (Exception e) {
            Alert alert = dictionaryManager.getAlertInfo("Error", Alert.AlertType.ERROR);
            alert.show();
        }
    }

    public void onActionBtnBookmark() {
        try {
            List<Word> words = dictionaryManager.getBookmark();
            List<String> keyWord = new ArrayList<>();
            for (Word word : words) {
                keyWord.add(word.getKeyWord());
            }
            if (!keyWord.contains(wordCurrent.getKeyWord())) {
                dictionaryManager.insertBookmark(wordCurrent);
                String content = "add \"" + wordCurrent.getKeyWord() + "\" into bookmark successful!";
                Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.INFORMATION);
                alert.show();
            } else {
                String content = "\"" + wordCurrent.getKeyWord() + "\" has been added!";
                Alert alert = dictionaryManager.getAlertInfo(content, Alert.AlertType.WARNING);
                alert.show();
            }
        } catch (Exception e) {
            Alert alert = dictionaryManager.getAlertInfo("Error", Alert.AlertType.INFORMATION);
            alert.show();
        }

    }

    public void onActionBtnBookmarkList() {
        List<Word> words = dictionaryManager.getBookmark();
        wordObservableList = FXCollections.observableArrayList(words);
        lvShowWord.setItems(wordObservableList);
    }

    public void onActionBtnSpeech() {
        VoiceSpeaker voiceSpeaker = new VoiceSpeaker();
        if (wordCurrent != null) {
            voiceSpeaker.speak(wordCurrent.getKeyWord());
        } else {
            Alert alert = voiceSpeaker.getAlertInfo("Please choose word!",
                    Alert.AlertType.INFORMATION);
            alert.show();
        }
    }
}
