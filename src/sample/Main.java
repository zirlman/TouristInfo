package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader1 = new FXMLLoader(getClass().getResource("adminApp.fxml"));
        Parent root = loader1.load();
        primaryStage.setScene(new Scene(root, 590, 393));
        primaryStage.setTitle("Tourist Info");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("res/icons/icon.png"));
        primaryStage.show();

        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("about.fxml"));
        Parent root2 = loader2.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root2, 450, 200));
        stage.setTitle("** ATTENTION **");
        stage.getIcons().add(new Image("res/icons/alertIcon.png"));
        stage.setMinWidth(460);
        stage.setMaxWidth(460);
        stage.setMinHeight(200);
        stage.setMaxHeight(200);
        stage.initOwner(primaryStage);
        stage.showAndWait();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
