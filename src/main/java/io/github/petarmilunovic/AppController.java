package io.github.petarmilunovic;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class AppController {

    @FXML
    private TextField tfieldMusicianId, tfieldMusicianName, tfieldMusicianType, tfieldStartedPerforming, tfieldStoppedPerforming, tfieldWebsite,
            tfieldAlbumName, tfieldAlbumRelease, tfieldPublishingHouse;
    @FXML
    private Stage focusedStage;
    @FXML
    private TableView<Musician> musicianTable;
    @FXML
    private TableView<Album> albumTable;
    @FXML
    private TableColumn<Musician, String> musicianNameColumn, typeColumn, startedPerformingColumn, stoppedPerformingColumn, websiteColumn,
            albumColumn, releaseYearColumn, publishingHouseColumn;
    @FXML
    private TableColumn<Album, String> musicianColumn;
    @FXML
    private Label labelAddInfo;
    @FXML
    private Button deleteButtonYes, deleteButtonNo;

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

            // controller associated with the loaded fxml
            AppController controller = loader.getController();

            // this only applies to resources/add-musician.fxml file
            if (stageTitle.matches("Add musician")) {
                controller.tfieldAlbumRelease.setDisable(true);
                controller.tfieldPublishingHouse.setDisable(true);

                // ensures that fields are enabled/disabled based on the field for album name
                if (controller.tfieldAlbumName != null) {
                    BooleanBinding emptyAlbumName = controller.tfieldAlbumName.textProperty().isEmpty();
                    controller.tfieldAlbumRelease.disableProperty().bind(emptyAlbumName);
                    controller.tfieldPublishingHouse.disableProperty().bind(emptyAlbumName);
                }
            }

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
                createAdditionalStage("Update musician", "", 0, 0);
                break;
            case "Delete musician":
                createAdditionalStage("Delete musician", "fxml-files/delete-musicians.fxml", 1000, 275);
                break;
            case "Display all musicians":
                createAdditionalStage("Display all musicians", "fxml-files/display-musicians.fxml", 290, 275);
                break;
            case "Search musicians":
                createAdditionalStage("Search musicians", "fxml-files/search-musicians.fxml", 290, 275);
                break;
            case "Search albums":
                createAdditionalStage("Search albums", "fxml-files/search-albums.fxml", 290, 275);
                break;
            case "Exit the program":
                if (HibernateUtil.sessionFactory != null)
                    HibernateUtil.closeSessionFactory();

                Platform.exit();
                break;
            default:
                break;
        }
    }

    /**
     * methods for interacting with the database
     */
    @FXML
    private void addMusician(ActionEvent event) {

        focusedStage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // saving stage reference for later use

        Session session = null;
        Transaction tx = null;
        Musician musician;

        // catching errors related to invalid input
        try {
            String name = tfieldMusicianName.getText(); //todo ensure that field can only accept characters
            String type = tfieldMusicianType.getText(); //todo ensure that value is either solo/band
            int startedPerforming = Integer.parseInt(tfieldStartedPerforming.getText());
            int stoppedPerforming = Integer.parseInt(tfieldStoppedPerforming.getText());
            String website = tfieldWebsite.getText();
            String albumName = tfieldAlbumName.getText();
            int albumRelease = tfieldAlbumRelease.getText().isEmpty() ? 0 : Integer.parseInt(tfieldAlbumRelease.getText());
            String publishingHouse = tfieldPublishingHouse.getText();

            musician = new Musician();
            musician.setName(name);
            musician.setType(type);
            musician.setStartedPerforming(startedPerforming);
            musician.setStoppedPerforming(stoppedPerforming);
            musician.setWebsite(website);

            Album album = new Album();
            album.setAlbumName(albumName);
            album.setReleaseYear(albumRelease);
            album.setPublishingHouse(publishingHouse);
            album.setMusician(musician);

            if (!albumName.isEmpty())
                musician.getAlbums().add(album);

        } catch (NumberFormatException err) {
            displayDialog("ERROR", "Make sure that all fields are correctly populated. Invalid input: " + err.getMessage());
            return;
        }

        // catching errors related to the database
        try {
            session = HibernateUtil.createSessionFactory().openSession();
            tx = session.beginTransaction();
            session.persist(musician); // adding musician and album to the database
            tx.commit();

            displayDialog("INFORMATION", "Musician successfully added! Press OK to return to the main menu.");
            focusedStage.close(); // closing the window
        } catch (HibernateException err) {
            if (tx != null)
                tx.rollback();

            displayDialog("ERROR", "There was a communication error with the database. Try again later.");
            focusedStage.close(); // closing the window
        } finally {
            if (session != null) {
                session.close();
                HibernateUtil.closeSessionFactory();
            }
        }
    }
    @FXML
    private void deleteMusician(ActionEvent event) {

        focusedStage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // saving stage reference for later use

        Session session = null;
        Transaction tx = null;
        Musician musician;

        // catching errors related to the database
        try {
            session = HibernateUtil.createSessionFactory().openSession();
            tx = session.beginTransaction();

            String selectedId = tfieldMusicianId.getText();

            Button clickedButton = (Button) event.getSource();
            String buttonText = clickedButton.getText();

            switch (buttonText) {

                case "Yes":
                    // accepting only digits
                    if (selectedId.matches("\\d+")) {
                        int id = Integer.parseInt(selectedId);
                        musician = session.get(Musician.class, id);

                        if (musician != null) {
                            session.delete(musician);
                            tx.commit();

                            displayDialog("INFORMATION", "Musician successfully deleted! Press OK to return to the main menu.");
                            focusedStage.close(); // closing the window
                        } else {
                            displayDialog("ERROR", "No musicians with the ID " + selectedId + " found.");
                        }
                    } else {
                        displayDialog("ERROR", "Make sure that ID field is correctly populated.");
                    }
                    break;
                case "No":
                    focusedStage.close();
                    break;
                default:
                    displayDialog("ERROR", "Unknown button clicked!");
                    break;
            }

        } catch (HibernateException err) {
            if (tx != null)
                tx.rollback();

            displayDialog("ERROR", "There was a communication error with the database. Try again later.");
            focusedStage.close(); // closing the window
        } finally {
            if (session != null) {
                session.close();
                HibernateUtil.closeSessionFactory();
            }
        }
    }
    @FXML
    private void displayAllMusicians(ActionEvent event) {

        focusedStage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // saving stage reference for later use

        Session session;
        List<Musician> musicians;

        // catching errors related to the database
        try {
            session = HibernateUtil.createSessionFactory().openSession();

            // collecting data from the database
            musicians = session.createQuery("FROM Musician", Musician.class).list();

            if (musicians != null && !musicians.isEmpty()) {
                populateMusicianTable(musicians);
            } else {
                displayDialog("ERROR", "No musicians found.");
            }
        } catch (HibernateException err) {
            displayDialog("ERROR", "There was a communication error with the database. Try again later.");
            focusedStage.close(); // closing the window
        }
    }
    @FXML
    private void searchMusiciansById(ActionEvent event) {

        focusedStage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // saving stage reference for later use

        Session session;
        Musician musician;

        // catching errors related to the database
        try {
            session = HibernateUtil.createSessionFactory().openSession();
            String selectedId = tfieldMusicianId.getText();
            // accepting only digits
            if (selectedId.matches("\\d+")) {
                int id = Integer.parseInt(tfieldMusicianId.getText());
                musician = session.get(Musician.class, id);

                // this only applies to resources/delete-musicians.fxml file
                 if (focusedStage.getTitle().matches("Delete musician")) {
                    if (musician != null) {

                        populateMusicianTable(musician); // populating table with musician data

                        deleteButtonYes.setVisible(true);
                        deleteButtonNo.setVisible(true);
                        labelAddInfo.setVisible(true);
                    } else {
                        displayDialog("ERROR", "Musician with ID " + selectedId + " not found!");
                    }
                } else {
                    displayDialog("ERROR", "Musician with ID " + selectedId + " not found!");
                }
            } else {
                displayDialog("ERROR", "Make sure that ID field is correctly populated. ");
            }
        } catch (HibernateException err) {
            displayDialog("ERROR", "There was a communication error with the database. Try again later.");
            focusedStage.close();
        }
    }
    @FXML
    private void searchMusiciansByName(ActionEvent event) {

        focusedStage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // saving stage reference for later use

        Session session;
        List<Musician> musicians;

        // catching errors related to the database
        try {
            session = HibernateUtil.createSessionFactory().openSession();
            String userInput = tfieldMusicianName.getText();
            // accepting only characters
            if (userInput.matches("[a-zA-Z]+")) {
                musicians = session.createQuery("FROM Musician WHERE name LIKE :name", Musician.class)
                        .setParameter("name", "%" + userInput + "%")
                        .list();

                if (!musicians.isEmpty()) {
                    populateMusicianTable(musicians); // populating table with list of musicians

                } else {
                    displayDialog("ERROR", "No musicians with the name " + userInput + " found.");
                }
            } else {
                displayDialog("ERROR", "Make sure that the name field is correctly populated.");
            }
        } catch (HibernateException err) {
            displayDialog("ERROR", "There was a communication error with the database. Try again later.");
            focusedStage.close(); // closing the window
        }
    }
    @FXML
    private void searchAlbumsByName(ActionEvent event) {

        focusedStage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // saving stage reference for later use

        Session session;
        List<Album> albums;

        // catching errors related to the database
        try {
            session = HibernateUtil.createSessionFactory().openSession();
            String userInput = tfieldAlbumName.getText();
            // accepting only characters
            if (userInput.matches("[a-zA-Z]+")) {
                albums = session.createQuery("FROM Album WHERE albumName LIKE :albumName", Album.class)
                        .setParameter("albumName", "%" + userInput + "%")
                        .list();

                if (!albums.isEmpty()) {
                    populateAlbumTable(albums); // populating table with list of albums

                } else {
                    displayDialog("ERROR", "No albums with the name " + userInput + " found.");
                }
            } else {
                displayDialog("ERROR", "Make sure that the name field is correctly populated.");
            }
        } catch (HibernateException err) {
            displayDialog("ERROR", "There was a communication error with the database. Try again later.");
            focusedStage.close(); // closing the window
        }
    }

    /**
     * methods for populating data with musician/album data
     */
    private void populateMusicianTable(Object musicians) {

        List<Musician> musicianList = null;

        // populates the list in a specific way depending on the method input parameter (one or multiple objects)
        if (musicians instanceof Musician)
            musicianList = Collections.singletonList((Musician) musicians);
        else if (musicians instanceof List)
            musicianList = (List<Musician>) musicians;

        musicianNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        startedPerformingColumn.setCellValueFactory(new PropertyValueFactory<>("startedPerforming"));
        stoppedPerformingColumn.setCellValueFactory(new PropertyValueFactory<>("stoppedPerforming"));
        websiteColumn.setCellValueFactory(new PropertyValueFactory<>("website"));

        albumColumn.setCellValueFactory(cellData -> {
            if (!cellData.getValue().getAlbums().isEmpty()) {
                Album album = cellData.getValue().getAlbums().iterator().next();
                return new SimpleStringProperty(album.getAlbumName());
            } else {
                return new SimpleStringProperty("No album");
            }
        });

        releaseYearColumn.setCellValueFactory(cellData -> {
            if (!cellData.getValue().getAlbums().isEmpty()) {
                Album album = cellData.getValue().getAlbums().iterator().next();
                return new SimpleStringProperty(String.valueOf(album.getReleaseYear()));
            } else {
                return new SimpleStringProperty("");
            }
        });

        publishingHouseColumn.setCellValueFactory(cellData -> {
            if (!cellData.getValue().getAlbums().isEmpty()) {
                Album album = cellData.getValue().getAlbums().iterator().next();
                return new SimpleStringProperty(album.getPublishingHouse());
            } else {
                return new SimpleStringProperty("");
            }
        });

        ObservableList<Musician> musicianDetails = FXCollections.observableArrayList(musicianList);
        musicianTable.setItems(musicianDetails);
    }
    private void populateAlbumTable(Object albums) {

        List<Album> albumList = null;

        // populates the list in a specific way depending on the method input parameter (one or multiple objects)
        if (albums instanceof Album)
            albumList = Collections.singletonList((Album) albums);
        else if (albums instanceof List)
            albumList = (List<Album>) albums;

        albumColumn.setCellValueFactory(new PropertyValueFactory<>("albumName"));

        musicianColumn.setCellValueFactory(cellData -> {
            Album album = cellData.getValue();
            if (album != null) {
                Musician musician = album.getMusician();
                return new SimpleStringProperty(musician != null ? musician.getName() : "");
            }
            return new SimpleStringProperty("");
        });

        releaseYearColumn.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));
        publishingHouseColumn.setCellValueFactory(new PropertyValueFactory<>("publishingHouse"));

        ObservableList<Album> albumDetails = FXCollections.observableArrayList(albumList);
        albumTable.setItems(albumDetails);
    }

    /**
     * displays a dialog to the user with additional information on what happened
     */
    public void displayDialog (String dialogType, String message) {

        switch (dialogType) {
            case "INFORMATION": {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.getDialogPane().setPrefWidth(300);
                alert.initModality(Modality.WINDOW_MODAL);
                alert.initOwner(focusedStage);
                alert.showAndWait();

                break;
            }
            case "ERROR": {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.getDialogPane().setPrefWidth(300);
                alert.initModality(Modality.WINDOW_MODAL);
                alert.initOwner(focusedStage);
                alert.showAndWait();

                break;
            }
        }
    }
}
