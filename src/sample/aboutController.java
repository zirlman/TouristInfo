package sample;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class aboutController {
    @FXML
    private Label label;
    @FXML
    private JFXButton button;
    @FXML
    private Hyperlink link;

    @FXML
    private void initialize() {
        label.setText("Tourist Info uses the JFoenix library.\n" + "To get the program working properly please download the .jar file given below");
        link.setOnAction(e -> {
            Desktop browser = Desktop.getDesktop();
            try {
                browser.browse(new URI("http://www.jfoenix.com/download/jfoenix.jar"));
            } catch (IOException err) {
                err.printStackTrace();
            } catch (URISyntaxException err) {
                err.printStackTrace();
            }
        });
        button.setOnAction(e -> ((Stage) button.getScene().getWindow()).close()); // Zatvaranje pomocnog prozora
    }
}
