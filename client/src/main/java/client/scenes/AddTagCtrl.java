package client.scenes;

import com.google.inject.Inject;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.Optional;
import java.util.ResourceBundle;

import client.utils.ServerUtils;

import commons.dtos.EventDTO;
import commons.dtos.TagDTO;

public class AddTagCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private ResourceBundle resources;

    @FXML
    private Button addButton;
    @FXML
    private Button backButton;
    @FXML
    private ColorPicker tagColor;
    @FXML
    private TextField tagName;
    @FXML
    private Pane parent;

    private EventDTO event;
    private boolean isEditingExpense;

    /**
     * Constructor
     *
     * @param server    server
     * @param mainCtrl  mainCtrl
     * @param resources resources
     */
    @Inject
    public AddTagCtrl(ServerUtils server, MainCtrl mainCtrl, ResourceBundle resources) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.resources = resources;
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
     * Sets the event
     *
     * @param event            event to be set
     * @param isEditingExpense if the previous screen was edit expense
     */
    public void setEvent(EventDTO event, boolean isEditingExpense) {
        this.event = event;
        this.isEditingExpense = isEditingExpense;
    }

    /**
     * goes back to the previous screen and keeps the info on that screen
     *
     * @param event action
     */
    public void goBack(ActionEvent event) {
        mainCtrl.returnToAddExpenseScreen(isEditingExpense);
        clearFields();
    }

    private void clearFields() {
        tagName.clear();
        tagColor.setValue(Color.WHITE);
    }

    /**
     * Adds the tag to the db
     */
    public void addTag() {
        if (tagName.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, resources.getString("enter_tag_name"), ButtonType.OK);
            alert.setHeaderText(resources.getString("no_tag_name"));
            alert.showAndWait();
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, resources.getString("add_tag_confirmation_message"), ButtonType.OK);
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            TagDTO tagDTO = new TagDTO(tagName.getText(), tagColor.getValue().toString(), 0);
            server.addTag(event.id(), tagDTO);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, resources.getString("add_tag_successful"), ButtonType.OK);
            alert.showAndWait();
            clearFields();
        }
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
    }
}
