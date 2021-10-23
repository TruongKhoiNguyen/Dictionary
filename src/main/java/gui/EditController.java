package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import shared.Word;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EditController implements Initializable {
    @FXML
    ListView<Word> lvShow;
    @FXML
    ComboBox<String> cbbHistoryEdit;
    private ObservableList<String> observableList = FXCollections.observableArrayList("3D ago", "1W ago", "1M ago");

    private List<Word> list = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbbHistoryEdit.setItems(observableList);
    }
}
