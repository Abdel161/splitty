package client.utils;

import com.google.inject.Inject;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.ResourceBundle;

public class EmailManager {
    private final ConfigManager configManager;
    private ResourceBundle resources;

    /**
     * Creates an Email Manager instance.
     *
     * @param configManager  instance
     * @param resourceBundle instance
     */
    @Inject
    public EmailManager(ConfigManager configManager, ResourceBundle resourceBundle) {
        this.configManager = configManager;
        this.resources = resourceBundle;
    }

    /**
     * Updates the resource bundle instance
     *
     * @param bundle new bundle to be set
     */
    public void updateResources(ResourceBundle bundle) {
        this.resources = bundle;
    }

    /**
     * Checks if config file credentials are properly set
     *
     * @return true iff credentials are non-empty
     */
    public boolean checkCredentials() {
        if (configManager.getEmailSenderName().isEmpty() || configManager.getEmailSenderEmail().isEmpty()
                || configManager.getEmailPassword().isEmpty() || configManager.getEmailUsername().isEmpty()) {
            Alert errorAlert = new Alert(Alert.AlertType.INFORMATION, resources.getString("email_config_not_set"), ButtonType.OK);
            errorAlert.setTitle(resources.getString("confirmation_title"));
            errorAlert.showAndWait();
            return false;
        }

        return true;
    }

    /**
     * Handle the sending of email
     *
     * @param recipient email to receive email
     * @param title     of the email
     * @param text      of the email
     */
    public void sendEmail(String recipient, String title, String text) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", configManager.getEmailHost());
        props.put("mail.smtp.port", configManager.getEmailPort());

        String username = configManager.getEmailUsername();
        String password = configManager.getEmailPassword();

        String senderEmail = configManager.getEmailSenderEmail();
        String senderName = configManager.getEmailSenderName();

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                }
        );

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail, senderName));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipient));
            message.setRecipients(Message.RecipientType.CC,
                    InternetAddress.parse(senderEmail));
            message.setSubject(title);
            message.setText(text);

            Transport.send(message);

            Alert errorAlert = new Alert(Alert.AlertType.CONFIRMATION, resources.getString("sent_mails_success"), ButtonType.OK);
            errorAlert.setTitle(resources.getString("confirmation_title"));
            errorAlert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, resources.getString("error_sending_mails"), ButtonType.OK);
            errorAlert.setTitle(resources.getString("error_title"));
            errorAlert.showAndWait();
        }
    }
}
