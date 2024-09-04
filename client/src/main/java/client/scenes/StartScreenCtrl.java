package client.scenes;

import com.google.inject.Inject;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;

import jakarta.ws.rs.WebApplicationException;

import java.util.*;

import client.utils.ConfigManager;
import client.utils.ServerUtils;

import commons.dtos.EventDTO;
import commons.dtos.EventTitleDTO;

public class StartScreenCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final ConfigManager configManager;
    private ResourceBundle resources;
    private boolean isLight;

    @FXML
    private ImageView flag;
    @FXML
    private Button adminButton;
    @FXML
    private Button settingsButton;
    @FXML
    private TextField title;
    @FXML
    private TextField inviteCode;
    @FXML
    private GridPane recentlyViewedGrid;
    @FXML
    private Pane parent;
    @FXML
    private ScrollPane scrollPane;

    /**
     * Constructs a StartScreenCtrl instance.
     *
     * @param server        ServerUtils instance.
     * @param mainCtrl      MainCtrl instance.
     * @param configManager ConfigManager instance.
     * @param resources     ResourceBundle resources.
     */
    @Inject
    public StartScreenCtrl(ServerUtils server, MainCtrl mainCtrl, ConfigManager configManager, ResourceBundle resources) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.configManager = configManager;
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
    }

    /**
     * Shows the event overview screen.
     */
    public void createEvent() {
        EventDTO event;

        try {
            event = server.createEvent(new EventTitleDTO(title.getText()));
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        configManager.addRecentlyViewedEvent(event.inviteCode());
        clearFields();
        mainCtrl.showEventOverview(event);
    }

    /**
     * Shows the event overview screen.
     */
    public void joinEvent() {
        EventDTO event;

        try {
            event = server.getEvent(inviteCode.getText());
        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        configManager.addRecentlyViewedEvent(event.inviteCode());
        clearFields();
        mainCtrl.showEventOverview(event);
    }

    /**
     * Clears the text fields.
     */
    private void clearFields() {
        title.clear();
        inviteCode.clear();
    }

    /**
     * Populates the grid RecentlyViewed.
     */
    public void populateRecentlyViewedGrid() {
        Image flagImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/flags/" + configManager.getLanguage() + ".png")));
        flag.setImage(flagImage);

        recentlyViewedGrid.getChildren().clear();

        List<EventDTO> eventList = getEventList();
        for (int i = 0; i < eventList.size(); i++) {
            Label eventNameLabel = new Label(eventList.get(i).title());

            Button joinEventButton = new Button(resources.getString("join_label"));
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/client/scenes/icons/eyeIcon.png")));
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

            joinEventButton.setGraphic(imageView);
            joinEventButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

            final int index = i;
            joinEventButton.setOnAction((ActionEvent actionEvent) -> {
                inviteCode.setText(eventList.get(index).inviteCode());
                joinEvent();
            });

            recentlyViewedGrid.addRow(i, eventNameLabel, joinEventButton);
        }
    }

    /**
     * Gets the list of recently viewed events, and removes non-existing events from the stored invite codes.
     *
     * @return List of recently viewed events.
     */
    private List<EventDTO> getEventList() {
        List<String> inviteCodeList = configManager.getRecentlyViewedEvents();
        List<EventDTO> eventList = new ArrayList<>();

        Iterator<String> inviteCodeIt = inviteCodeList.iterator();
        while (inviteCodeIt.hasNext()) {
            try {
                eventList.add(server.getEvent(inviteCodeIt.next()));
            } catch (WebApplicationException e) {
                inviteCodeIt.remove();
            }
        }

        configManager.setRecentlyViewedEvents(inviteCodeList);
        return eventList;
    }

    /**
     * Triggered when the admin button is pressed
     *
     * @param actionEvent of the button press
     */
    public void onAdminPress(ActionEvent actionEvent) {
        mainCtrl.showLoginScreen();
    }

    /**
     * Triggered when the settings button is pressed
     *
     * @param actionEvent of the button press
     */
    public void onSettingsPress(ActionEvent actionEvent) {
        mainCtrl.showSettingsScreen(null);
    }

    /**
     * Triggered when the flag icon is clicked
     *
     * @param mouseEvent event of the click
     */
    public void onLanguageClick(MouseEvent mouseEvent) {
        mainCtrl.showSettingsScreen(null);
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
        populateRecentlyViewedGrid();
    }
}
