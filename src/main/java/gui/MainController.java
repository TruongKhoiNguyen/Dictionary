package gui;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private AnchorPane pane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            VBox box = FXMLLoader.load(getClass().getResource("/fxml/MenuBar.fxml"));
            drawer.setSidePane(box);
            drawer.setMouseTransparent(true);

            HamburgerBackArrowBasicTransition burgerTask2 = new HamburgerBackArrowBasicTransition(hamburger);
            burgerTask2.setRate(-1);
            hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
                burgerTask2.setRate(burgerTask2.getRate() * -1);
                burgerTask2.play();

                if (drawer.isOpened()) {
                    drawer.close();
                    drawer.setMouseTransparent(true);
                    pane.setEffect(null);
                    removeBlankLayer();
                } else {
                    drawer.open();
                    drawer.setMouseTransparent(false);
                    pane.setEffect(new BoxBlur());
                    addBlankLayer(burgerTask2);
                }
            });

            pane = FXMLLoader.load(getClass().getResource("/fxml/LookupGUI.fxml"));
            anchorPane.getChildren().set(0, pane);

            runMenu(box, burgerTask2);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void runMenu(VBox box, HamburgerBackArrowBasicTransition burgerTask2) {
        VBox box1 = (VBox) box.getChildren().get(1);
        for (Node node : box1.getChildren()) {
            if (node.getAccessibleText() != null) {
                node.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
                    switch (node.getAccessibleText()) {
                        case "Lookup" -> loadAppOnMenu("/fxml/LookupGUI.fxml", burgerTask2);
                        case "Translate" -> loadAppOnMenu("/fxml/TranslateGUI.fxml", burgerTask2);
                        case "Settings/Options" -> loadAppOnMenu("/fxml/EditGUI.fxml", burgerTask2);
                    }
                });
            }
        }

        AnchorPane quit = (AnchorPane) box.getChildren().get(2);
        Node node = quit.getChildren().get(0);
        if (node.getAccessibleText() != null) {
            node.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> System.exit(0));
        }
    }

    /**
     * loadAppOnMenu.
     * @param fxmlLink fxmlLink
     * @param burgerTask2 menuControl
     */
    private void loadAppOnMenu(String fxmlLink, HamburgerBackArrowBasicTransition burgerTask2) {
        try {
            pane = FXMLLoader.load(getClass().getResource(fxmlLink));
            anchorPane.getChildren().set(0, pane);

            removeBlankLayer();
            burgerTask2.setRate(burgerTask2.getRate() * -1);
            burgerTask2.play();
            drawer.close();
            drawer.setMouseTransparent(true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void addBlankLayer(HamburgerBackArrowBasicTransition burgerTask2) {
        AnchorPane blank = new AnchorPane();
        try {
            blank = FXMLLoader.load(getClass().getResource("/fxml/Blank.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        anchorPane.getChildren().add(1, blank);
        blank.setEffect(new BoxBlur());
        blank.setOnMousePressed(mouseEvent -> {
            anchorPane.getChildren().get(0).setEffect(null);
            anchorPane.getChildren().remove(1);
            burgerTask2.setRate(burgerTask2.getRate() * -1);
            burgerTask2.play();
            drawer.close();
            drawer.setMouseTransparent(true);
        });
    }

    private void removeBlankLayer() {
        anchorPane.getChildren().remove(1);
    }
}

