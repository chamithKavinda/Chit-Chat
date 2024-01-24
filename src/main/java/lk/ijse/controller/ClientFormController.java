package lk.ijse.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class ClientFormController {

    @FXML
    private ImageView btnSend;

    @FXML
    private Label lblClientName;

    @FXML
    private VBox msgVbox;

    @FXML
    private TextField txtMessage;

    @FXML
    void txtMessageOnAction(ActionEvent event) {

    }

}
