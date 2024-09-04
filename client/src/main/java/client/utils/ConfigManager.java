package client.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ConfigManager {

    private static final String DEFAULT_URL = "http://localhost:8080";
    private static final String DEFAULT_WS_URL = "ws://localhost:8080/websocket";
    private static final String DEFAULT_LANGUAGE = "en";
    private static final String DEFAULT_CURRENCY = "EUR";
    private static final int MAX_RECENTLY_VIEWED_EVENTS = 10;

    private final Properties properties;
    private final String configFile;

    /**
     * Initializes the Config Manager
     *
     * @param configFile name of the file to read from
     */
    public ConfigManager(String configFile) {
        this.properties = new Properties();
        this.configFile = configFile;

        try {
            (new File(configFile)).createNewFile();
        } catch (IOException ignored) {
        }

        loadProperties();
    }

    /**
     * Loads the properties from the file to the properties class
     */
    private void loadProperties() {
        try {
            properties.load(new FileInputStream(configFile));

            // needed for initialization of default properties
            getURL();
            getWebSocketURL();
            getEmailHost();
            getEmailPort();
            getEmailUsername();
            getEmailPassword();
            getEmailSenderEmail();
            getEmailSenderName();
            getLanguage();
            getCurrency();

            saveProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the URL specified in the config file
     */
    public String getURL() {
        if (!properties.containsKey("url")) {
            properties.setProperty("url", DEFAULT_URL);
        }
        return properties.getProperty("url");
    }

    /**
     * @return the WS URL specified in the config file
     */
    public String getWebSocketURL() {
        if (!properties.containsKey("ws_url")) {
            properties.setProperty("ws_url", DEFAULT_WS_URL);
        }
        return properties.getProperty("ws_url");
    }

    /**
     * @return the email host specified in the config file
     */
    public String getEmailHost() {
        if (!properties.containsKey("email_host")) {
            properties.setProperty("email_host", "");
        }
        return properties.getProperty("email_host");
    }

    /**
     * @return the email host specified in the config file
     */
    public String getEmailPort() {
        if (!properties.containsKey("email_port")) {
            properties.setProperty("email_port", "");
        }
        return properties.getProperty("email_port");
    }

    /**
     * @return the email username specified in the config file
     */
    public String getEmailUsername() {
        if (!properties.containsKey("email_username")) {
            properties.setProperty("email_username", "");
        }
        return properties.getProperty("email_username");
    }

    /**
     * @return the email password specified in the config file
     */
    public String getEmailPassword() {
        if (!properties.containsKey("email_password")) {
            properties.setProperty("email_password", "");
        }
        return properties.getProperty("email_password");
    }

    /**
     * @return the email sender email specified in the config file
     */
    public String getEmailSenderEmail() {
        if (!properties.containsKey("email_sender_email")) {
            properties.setProperty("email_sender_email", "");
        }
        return properties.getProperty("email_sender_email");
    }

    /**
     * @return the email sender name specified in the config file
     */
    public String getEmailSenderName() {
        if (!properties.containsKey("email_sender_name")) {
            properties.setProperty("email_sender_name", "");
        }
        return properties.getProperty("email_sender_name");
    }

    /**
     * @return the language specified in the config file
     */
    public String getLanguage() {
        if (!properties.containsKey("language")) {
            properties.setProperty("language", DEFAULT_LANGUAGE);
        }
        return properties.getProperty("language", DEFAULT_LANGUAGE);
    }

    /**
     * @return the currency specified in the config file
     */
    public String getCurrency() {
        if (!properties.containsKey("currency")) {
            properties.setProperty("currency", DEFAULT_CURRENCY);
        }
        return properties.getProperty("currency", DEFAULT_CURRENCY);
    }

    /**
     * Gets list of the recently viewed events.
     *
     * @return List containing the invite codes of the recently viewed events.
     */
    public List<String> getRecentlyViewedEvents() {
        return new LinkedList<>(Arrays.asList(properties.getProperty("recently_viewed_events", ",").split(",")));
    }

    /**
     * @param language to set in the config file
     */
    public void setLanguage(String language) {
        properties.setProperty("language", language);
        saveProperties();
    }

    /**
     * @param currency to set in the config file
     */
    public void setCurrency(String currency) {
        properties.setProperty("currency", currency);
        saveProperties();
    }

    /**
     * Sets the recently viewed events.
     *
     * @param events List of invite codes to store in the config file.
     */
    public void setRecentlyViewedEvents(List<String> events) {
        properties.setProperty("recently_viewed_events", String.join(",", events));
        saveProperties();
    }

    /**
     * Adds an event to the recently viewed events.
     *
     * @param eventCode Invite code of the event to be added.
     */
    public void addRecentlyViewedEvent(String eventCode) {
        List<String> events = getRecentlyViewedEvents();

        events.removeIf(s -> s.equals(eventCode));
        events.addFirst(eventCode);
        if (events.size() > MAX_RECENTLY_VIEWED_EVENTS)
            events.removeLast();

        setRecentlyViewedEvents(events);
    }

    /**
     * Save the currently updated properties to the file
     */
    private void saveProperties() {
        try {
            properties.store(new FileOutputStream(configFile), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
