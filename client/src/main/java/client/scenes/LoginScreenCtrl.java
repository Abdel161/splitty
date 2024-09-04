package client.scenes;

import com.google.inject.Inject;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.Pane;

import java.util.ResourceBundle;

import client.utils.ServerUtils;

public class LoginScreenCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private ResourceBundle resources;

    @FXML
    private PasswordField passwordField;
    @FXML
    private Pane parent;

    /**
     * Constructs an LoginScreenCtrl instance.
     *
     * @param server    ServerUtils instance.
     * @param mainCtrl  MainCtrl instance.
     * @param resources ResourceBundle resources.
     */
    @Inject
    public LoginScreenCtrl(ServerUtils server, MainCtrl mainCtrl, ResourceBundle resources) {
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
     * Navigates back to the start screen.
     *
     * @param event Action event.
     */
    public void onBack(ActionEvent event) {
        mainCtrl.showStartScreen();
    }

    /**
     * Validates the entered password, and if correct, navigates to the admin screen.
     *
     * @param event Action event.
     */
    public void onEnter(ActionEvent event) {
        String password = passwordField.getText();
        if (password.isEmpty() || !server.validatePassword(password)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, resources.getString("invalid_password"), ButtonType.OK);
            alert.showAndWait();
            return;
        }

        mainCtrl.showAdminScreen();
        passwordField.setText("");
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
