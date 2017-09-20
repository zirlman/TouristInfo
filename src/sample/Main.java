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
//        FXMLLoader loader1 = new FXMLLoader(getClass().getResource("sample.fxml"));
//        Parent root = loader1.load();
//        primaryStage.setTitle("First GUI app");
//        primaryStage.setScene(new Scene(root, 600, 400));
//        primaryStage.setMinWidth(500);
//        primaryStage.setMinHeight(300);
//
//        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("about.fxml"));
//        Parent root2 = loader2.load();
//        Stage stage = new Stage();
//        stage.setScene(new Scene(root2, 450, 200));
//        stage.setTitle("** ATTENTION **");
//        stage.setMinWidth(450);
//        stage.setMaxWidth(450);
//        stage.setMinHeight(200);
//        stage.setMaxHeight(200);
//        primaryStage.show();
//        stage.show();
        FXMLLoader loader1 = new FXMLLoader(getClass().getResource("adminApp.fxml"));
        Parent root = loader1.load();
        primaryStage.setScene(new Scene(root, 590, 393));
        primaryStage.setTitle("Tourist Info");
        primaryStage.setResizable(false);
//        primaryStage.setMinWidth(600);
//        primaryStage.setMinHeight(440);
//        primaryStage.setMaxWidth(600);
//        primaryStage.setMaxHeight(440);
        primaryStage.getIcons().add(new Image("res/icons/icon.png"));
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
