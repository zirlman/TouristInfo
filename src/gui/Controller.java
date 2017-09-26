package gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

public class Controller {
    @FXML
    private JFXButton submitButton;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private JFXButton showButton;
    @FXML
    private JFXTextField field1;
    @FXML
    private JFXPasswordField field2;
    @FXML
    private ChoiceBox<String> choiceBox;
    private JFXDatePicker jfxDatePicker = new JFXDatePicker();
    @FXML
    private Label label;

    @FXML
    private void initialize() {
        choiceBox.getItems().addAll("Museum", "Historical monument", "Amusement park", "Church");
        choiceBox.setValue("Museum");
        showButton.setOnAction(e -> getChoice(choiceBox));
        jfxDatePicker.setPrefWidth(300);
        jfxDatePicker.setPrefHeight(200);
    }

    @FXML
    private void getChoice(ChoiceBox<String> choiceBox) {
        label.setText(choiceBox.getValue());

    }

}
