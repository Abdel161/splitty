package client.scenes;

import com.google.inject.Inject;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.util.ResourceBundle;

import client.utils.ServerUtils;

import commons.dtos.EventDTO;
import commons.dtos.ParticipantDTO;

public class AddParticipantCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private ResourceBundle resources;

    @FXML
    private Label title;
    @FXML
    private Button saveButton;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField ibanTextField;
    @FXML
    private TextField bicTextField;
    @FXML
    private Pane parent;
    private EventDTO currentEvent;
    private ParticipantDTO currentParticipant;

    /**
     * Constructs a AddParticipantCtrl instance.
     *
     * @param server    ServerUtils instance.
     * @param mainCtrl  MainCtrl instance.
     * @param resources ResourceBundle resources.
     */
    @Inject
    public AddParticipantCtrl(ServerUtils server, MainCtrl mainCtrl, ResourceBundle resources) {
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
     * Sets the Event to which the Participant belongs.
     *
     * @param event The event to set as the current event.
     */
    public void setEvent(EventDTO event) {
        saveButton.setText(resources.getString("add_label"));
        title.setText(resources.getString("add_participant"));
        currentEvent = event;
    }

    /**
     * Action called when edit button is pressed
     *
     * @param participant The ParticipantDto object containing the details to be displayed in the UI.
     */
    public void setParticipant(ParticipantDTO participant) {
        title.setText(resources.getString("edit_participant"));
        saveButton.setText(resources.getString("edit_label"));

        nameTextField.setText(participant.name());
        emailTextField.setText(participant.email() == null ? "" : participant.email());
        ibanTextField.setText(participant.iban() == null ? "" : participant.iban());
        bicTextField.setText(participant.bic() == null ? "" : participant.bic());

        currentParticipant = participant;
    }

    /**
     * Action called when add button is pressed
     */
    public void onSave() {
        if (!checkInputValidity()) {
            return;
        }

        String name = nameTextField.getText();
        String email = emailTextField.getText().isBlank() ? null : emailTextField.getText();
        String iban = ibanTextField.getText().isBlank() ? null : ibanTextField.getText();
        String bic = bicTextField.getText().isBlank() ? null : bicTextField.getText();

        if (currentParticipant == null) {
            ParticipantDTO newParticipant = new ParticipantDTO(0, name, email, iban, bic);
            server.addParticipant(currentEvent.id(), newParticipant);
            showSuccess(resources.getString("participant_success_create_message"));
        } else {
            ParticipantDTO updatedParticipant = new ParticipantDTO(currentParticipant.id(), name, email, iban, bic);
            server.updateParticipant(currentEvent.id(), currentParticipant.id(), updatedParticipant);
            showSuccess(resources.getString("participant_success_edit_message"));
        }

        cancel();
    }

    /**
     * Action called when cancel button is pressed
     */
    public void cancel() {
        nameTextField.clear();
        emailTextField.clear();
        ibanTextField.clear();
        bicTextField.clear();
        currentParticipant = null;

        mainCtrl.showEventOverview();
        saveButton.setText(resources.getString("add_label"));
        title.setText(resources.getString("add_participant"));
    }

    /**
     * Checks the input fields for validity.
     * This method validates the input fields for name, email, IBAN, and BIC. It displays an error message
     * if the name field is empty or if the email, IBAN, or BIC format is incorrect.
     *
     * @return true if all input fields are valid, false otherwise.
     */
    private boolean checkInputValidity() {
        if (nameTextField.getText().isBlank()) {
            showError(resources.getString("all_fields_must_be_filled_out_message"));
            return false;
        }

        if (!emailTextField.getText().isBlank() && !validEmail(emailTextField.getText())) {
            showError(resources.getString("email_incorrect"));
            return false;
        }

        if (!ibanTextField.getText().isBlank() && !validIBAN(ibanTextField.getText())) {
            showError(resources.getString("iban_incorrect"));
            return false;
        }

        if (!bicTextField.getText().isBlank() && !validBIC(bicTextField.getText())) {
            showError(resources.getString("bic_incorrect"));
            return false;
        }

        return true;
    }

    /**
     * Displays an error message to the user in an alert dialog.
     *
     * @param message The message that is displayed.
     */
    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(resources.getString("error"));
        alert.showAndWait();
    }

    /**
     * Displays an alert box indicating that the operation was completed successfully.
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(resources.getString("success"));
        alert.setContentText(message);
        alert.showAndWait();
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

    /**
     * Checks if the email is valid.
     *
     * @param s the input value
     * @return true if the email has a valid format
     */
    private static boolean validEmail(String s) {
        return s.matches(".+@.+\\..+");
    }

    /**
     * Checks if the IBAN is valid.
     *
     * @param s the input value
     * @return true if the IBAN has a valid format
     */
    private static boolean validIBAN(String s) {
        return s.matches("^[A-Z]{2}\\d{2}[A-Za-z0-9]{1,30}$");
    }

    /**
     * Checks if the BIC is valid.
     *
     * @param s the input value
     * @return true if the BIC has a valid format
     */
    private static boolean validBIC(String s) {
        return s.matches("[a-zA-Z]{6}[a-zA-Z0-9]{2}([a-zA-Z0-9]{3})?");
    }
}
