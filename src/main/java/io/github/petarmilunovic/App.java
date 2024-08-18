package io.github.petarmilunovic;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        URL fxml = getClass().getClassLoader().getResource("fxml-files/main-menu.fxml");
        VBox root = FXMLLoader.load(fxml);
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Hibernate Demo");
        stage.setResizable(false);
        stage.setMinWidth(275);
        stage.setMinHeight(285);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}