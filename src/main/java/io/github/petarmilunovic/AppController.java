package io.github.petarmilunovic;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class AppController {

    @FXML
    private void initialize() {

    }

    /**
     * creates and display a new stage
     */
    private void createAdditionalStage(String stageTitle, String fxmlUrl, int width, int height) {

        try {
            URL fxml = getClass().getClassLoader().getResource(fxmlUrl);
            FXMLLoader loader = new FXMLLoader(fxml);
            VBox root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setMinWidth(width);
            stage.setMinHeight(height);
            stage.setScene(scene);
            stage.setTitle(stageTitle);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            stage.show();
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    /**
     * determining which new stage to open based on the used button in resources/main-menu.fxml file
     */
    @FXML
    private void displayAdditionalStage(ActionEvent event) {

        Button clickedButton = (Button) event.getSource();
        String buttonText = clickedButton.getText();

        switch (buttonText) {
            case "Add musician":
                createAdditionalStage("Add musician", "fxml-files/add-musician.fxml", 300, 315);
                break;
            case "Update musician":
                createAdditionalStage("Update musician", "#", 0, 0);
                break;
            case "Delete musician":
                createAdditionalStage("Delete musician", "#", 0, 0);
                break;
            case "Display all musicians":
                createAdditionalStage("Display all musicians", "#", 0, 0);
                break;
            case "Display all albums":
                createAdditionalStage("Display all albums", "#", 0, 0);
                break;
            case "Search musicians":
                createAdditionalStage("Search musicians", "#", 0, 0);
                break;
            case "Search albums":
                createAdditionalStage("Search albums", "#", 0, 0);
                break;
            case "Exit the program":
                Platform.exit();
                break;
            default:
                break;
        }
    }

}
