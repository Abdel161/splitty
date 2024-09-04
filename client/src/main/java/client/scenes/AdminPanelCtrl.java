package client.scenes;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.*;

import client.utils.ServerUtils;

import commons.dtos.EventDTO;

public class AdminPanelCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private ResourceBundle resources;

    private boolean isLight;

    @FXML
    private ComboBox<String> sortingDropdown;
    @FXML
    private GridPane eventsGrid;
    @FXML
    private Pane parent;

    private ObservableList<EventDTO> currentEvents;
    private String sortType = "created";

    /**
     * Constructs an AddExpenseCtrl instance.
     *
     * @param server    ServerUtils instance.
     * @param mainCtrl  MainCtrl instance.
     * @param resources ResourceBundle resources.
     */
    @Inject
    public AdminPanelCtrl(ServerUtils server, MainCtrl mainCtrl, ResourceBundle resources) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.resources = resources;
        isLight = true;
    }

    /**
     * Updates the resource bundle instance
     *
     * @param bundle new bundle to be set
     */
    public void updateResources(ResourceBundle bundle) {
        resources = bundle;
        populateDropdown();
    }

    @FXML
    private void initialize() {
        currentEvents = FXCollections.observableArrayList(server.getEvents());
        populateEventsGrid();

        server.registerForEventUpdates(events -> Platform.runLater(() -> {
            currentEvents.setAll(events);
            populateEventsGrid();
        }));

        populateDropdown();
    }

    /**
     * Populates the 'order by' dropdown.
     */
    public void populateDropdown() {
        ObservableList<String> sortTypes = FXCollections.observableArrayList(
                resources.getString("name_label"),
                resources.getString("last_updated"),
                resources.getString("created"));

        sortingDropdown.setItems(sortTypes);
        if (!isLight) {
            sortingDropdown.setStyle("-fx-text-fill: white;");
        }

        sortingDropdown.getSelectionModel().select(resources.getString(sortType));
    }

    /**
     * Navigates back to the start screen.
     *
     * @param event Action event.
     */
    public void onBack(ActionEvent event) {
        mainCtrl.showStartScreen();
    }

    /**
     * Shows a file chooser and uploads the chosen event dump.
     *
     * @param event Action event.
     */
    public void onUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select JSON File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }

                String everything = sb.toString();
                server.uploadEvent(everything);
            } catch (FileNotFoundException ignored) {
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    /**
     * Loads all events to the table
     */
    public void populateEventsGrid() {
        switch (sortType) {
            case "name_label" -> currentEvents.sort((event1, event2) -> {
                if (event1.title() == null) return -1;
                if (event2.title() == null) return 1;
                return event1.title().compareToIgnoreCase(event2.title());
            });
            case "last_updated" -> currentEvents.sort((event1, event2) -> {
                if (event1.updatedOn() == null) return -1;
                if (event2.updatedOn() == null) return 1;
                return -event1.updatedOn().compareTo(event2.updatedOn());
            });
            case "created" -> currentEvents.sort((event1, event2) -> {
                if (event1.createdOn() == null) return -1;
                if (event2.updatedOn() == null) return 1;
                return -event1.createdOn().compareTo(event2.createdOn());
            });
        }

        eventsGrid.getChildren().clear();
        for (int i = 0; i < currentEvents.size(); i++) {
            EventDTO currentEvent = currentEvents.get(i);
            Label eventNameLabel = new Label(currentEvent.title());
            Button enterEventButton = setUpEnterEventButton(currentEvent);
            Button downloadEventInfoButton = setUpDownloadButton(currentEvent);
            Button deleteButton = setUpDeleteButton(currentEvent);

            eventsGrid.addRow(i, eventNameLabel, enterEventButton, downloadEventInfoButton, deleteButton);
        }
    }

    /**
     * Re-sorts the events based on the changed sort type.
     *
     * @param actionEvent Action event.
     */
    public void onChange(ActionEvent actionEvent) {
        if (resources.getString(sortType).equals(sortingDropdown.getValue()) || sortingDropdown.getValue() == null)
            return;

        if (sortingDropdown.getValue().equals(resources.getString("name_label"))) {
            sortType = "name_label";
        } else if (sortingDropdown.getValue().equals(resources.getString("last_updated"))) {
            sortType = "last_updated";
        } else if (sortingDropdown.getValue().equals(resources.getString("created"))) {
            sortType = "created";
        }

        populateEventsGrid();
    }

    private Button setUpEnterEventButton(EventDTO currentEvent) {
        Button enterEventButton = generateButtonWithIcon("/client/scenes/icons/eyeIcon.png");
        enterEventButton.setOnAction((ActionEvent actionEvent) -> {
            mainCtrl.showEventOverview(currentEvent, "admin");
        });

        return enterEventButton;
    }

    private Button setUpDownloadButton(EventDTO currentEvent) {
        Button downloadEventButton = generateButtonWithIcon("/client/scenes/icons/downloadIcon.png");
        downloadEventButton.setOnAction((ActionEvent actionEvent) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save JSON File");
            fileChooser.setInitialFileName(currentEvent.title() + ".json");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(server.getEventDump(currentEvent.id()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return downloadEventButton;
    }

    private Button setUpDeleteButton(EventDTO currentEvent) {
        Button deleteButton = generateButtonWithIcon("/client/scenes/icons/deleteIcon.png");
        deleteButton.setOnAction((ActionEvent actionEvent) -> {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION,
                    resources.getString("delete_event_confirmation"), ButtonType.NO, ButtonType.YES);
            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                server.deleteEvent(currentEvent.id());
            }
        });

        return deleteButton;
    }

    /**
     * Generates a button with the corresponding image.
     *
     * @param resourcePath The path to the image.
     * @return The generated button with the corresponding image.
     */
    private Button generateButtonWithIcon(String resourcePath) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(resourcePath)));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(15);
        imageView.setFitWidth(15);

        if (!isLight) {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(1);
            colorAdjust.setContrast(0);
            colorAdjust.setSaturation(0);
            colorAdjust.setHue(0);

            imageView.setEffect(colorAdjust);
        }

        Button button = new Button();
        button.setGraphic(imageView);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        return button;
    }

    /**
     * Stops executors.
     */
    public void stopExecutors() {
        server.stopExecutors();
    }

    /**
     * Changes style mode
     *
     * @param isLightMode property
     */
    public void changeStyleMode(boolean isLightMode) {
        ObservableList<String> stylesheets = parent.getStylesheets();
        stylesheets.clear();

        if (!isLightMode) {
            parent.getStylesheets().add("/client/styles/DarkMode.css");
        } else {
            parent.getStylesheets().add("/client/styles/LightMode.css");
        }

        isLight = isLightMode;
    }
}
