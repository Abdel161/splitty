package client.scenes;

import client.utils.EmailManager;
import commons.dtos.ParticipantDTO;
import jakarta.inject.Inject;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.util.Arrays;
import java.util.ResourceBundle;

import client.utils.ConfigManager;
import client.utils.ServerUtils;

import commons.dtos.EventDTO;

public class InviteScreenCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private ResourceBundle resources;
    private final ConfigManager configManager;
    private final EmailManager emailManager;

    @FXML
    private Button backButton;
    @FXML
    private Label codeLabel;
    @FXML
    private Label eventNameLabel;
    @FXML
    private Button sendInvitesButton;
    @FXML
    private TextArea textArea;
    @FXML
    private Pane parent;

    private EventDTO currentEvent;

    /**
     * Initializes the invite screen controller
     *
     * @param server        ServerUtils instance
     * @param mainCtrl      MainCtrl instance
     * @param resources     ResourceBundle resources.
     * @param configManager ConfigManager instance
     * @param emailManager  EmailManager instance
     */
    @Inject
    public InviteScreenCtrl(ServerUtils server, MainCtrl mainCtrl, ResourceBundle resources, ConfigManager configManager, EmailManager emailManager) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.resources = resources;
        this.configManager = configManager;
        this.emailManager = emailManager;
    }

    /**
     * Updates the resource bundle instance
     *
     * @param bundle new bundle to be set
     */
    public void updateResources(ResourceBundle bundle) {
        resources = bundle;
        emailManager.updateResources(bundle);
    }

    /**
     * Navigates back to the start screen.
     *
     * @param event Action event.
     */
    public void onBack(ActionEvent event) {
        mainCtrl.showEventOverview();
    }

    /**
     * Sends invites.
     *
     * @param event Action event.
     */
    public void onSendInvites(ActionEvent event) {
        if (textArea.getText().isEmpty()) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, resources.getString("empty_textbox_error"), ButtonType.OK);
            errorAlert.setTitle(resources.getString("error"));
            errorAlert.showAndWait();
            return;
        }

        if (!emailManager.checkCredentials()) {
            return;
        }

        String[] recipients = textArea.getText().split("\n");

        for (String recipient : recipients) {
            emailManager.sendEmail(recipient,
                    resources.getString("splitty_invite") ,
                    resources.getString("invite_email_message") + " " + currentEvent.inviteCode() + "\nSplitty server: " + configManager.getURL()
            );
            server.addParticipant(currentEvent.id(),
                    new ParticipantDTO(0, Arrays.stream(recipient.split("@")).findFirst().get(), recipient, null, null));
        }

        textArea.setText("");
    }

    /**
     * Sets the current event.
     *
     * @param event Event DTO.
     */
    public void setEvent(EventDTO event) {
        currentEvent = event;
        eventNameLabel.setText(event.title());
        codeLabel.setText(codeLabel.getText().split(":")[0] + ": " + event.inviteCode());
    }

    /**
     * Changes the style of the screen
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
     * Sends test email.
     *
     * @param actionEvent Action event.
     */
    public void onTest(ActionEvent actionEvent) {
        if (!emailManager.checkCredentials()) {
            return;
        }
        String senderEmail = configManager.getEmailSenderEmail();
        emailManager.sendEmail(senderEmail, resources.getString("splitty_invite"), resources.getString("default_email"));
    }
}
