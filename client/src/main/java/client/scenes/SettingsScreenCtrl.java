package client.scenes;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.*;
import java.util.*;
import java.util.function.Function;

import client.Main;
import client.utils.ConfigManager;
import client.utils.ServerUtils;

public class SettingsScreenCtrl {

    private record Language(String name, String code) {
    }

    private static final String[] availableLanguages = new String[]{"en", "bg", "nl"};

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final ConfigManager configManager;
    private ResourceBundle resources;
    private boolean isLightMode = true;
    private final StartScreenCtrl startScreenCtrl;
    private final EventOverviewCtrl eventOverviewCtrl;
    private final AddExpenseCtrl addExpenseCtrl;
    private final AddParticipantCtrl addParticipantCtrl;
    private final AdminPanelCtrl adminPanelCtrl;
    private final LoginScreenCtrl loginScreenCtrl;
    private final InviteScreenCtrl inviteScreenCtrl;
    private final DebtOverviewCtrl debtOverviewCtrl;
    private final AddTagCtrl addTagCtrl;
    private final EditTagCtrl editTagCtrl;
    private final StatisticsScreenCtrl statisticsScreenCtrl;

    @FXML
    private ImageView flag;
    @FXML
    private Button backButton;
    @FXML
    private ComboBox<Language> languageDropdown;
    @FXML
    private Button saveButton;

    @FXML
    private Label labelMode;
    @FXML
    private Button btnMode;
    @FXML
    private Pane parent;
    @FXML
    private ImageView imgMode;

    private Function<Void, Void> onReturn;

    /**
     * Initializes the settings screen controller
     *
     * @param server               instance to be set
     * @param mainCtrl             instance to be set
     * @param configManager        ConfigManager instance to be set
     * @param resources            ResourceBundle resources.
     * @param startScreenCtrl      StartScreenCtrl instance
     * @param eventOverviewCtrl    EventScreenCtrl instance
     * @param addExpenseCtrl       ExpenseScreenCtrl instance
     * @param addParticipantCtrl   ParticipantScreenCtrl instance
     * @param loginScreenCtrl      LoginScreenCtrl instance
     * @param adminPanelCtrl       AdminScreenCtrl instance
     * @param inviteScreenCtrl     InviteScreenCtrl instance
     * @param debtOverviewCtrl     DebtScreenCtrl instance
     * @param addTagCtrl           AddTagCtrl instance
     * @param editTagCtrl          EditTagCtrl instance
     * @param statisticsScreenCtrl StatisticsScreenCtrl instance
     */
    @Inject
    public SettingsScreenCtrl(ServerUtils server, MainCtrl mainCtrl, ConfigManager configManager,
                              ResourceBundle resources, StartScreenCtrl startScreenCtrl,
                              EventOverviewCtrl eventOverviewCtrl, AddExpenseCtrl addExpenseCtrl,
                              AddParticipantCtrl addParticipantCtrl, LoginScreenCtrl loginScreenCtrl,
                              AdminPanelCtrl adminPanelCtrl, InviteScreenCtrl inviteScreenCtrl,
                              DebtOverviewCtrl debtOverviewCtrl, AddTagCtrl addTagCtrl,
                              EditTagCtrl editTagCtrl, StatisticsScreenCtrl statisticsScreenCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.configManager = configManager;
        this.resources = resources;
        this.startScreenCtrl = startScreenCtrl;
        this.eventOverviewCtrl = eventOverviewCtrl;
        this.addExpenseCtrl = addExpenseCtrl;
        this.addParticipantCtrl = addParticipantCtrl;
        this.adminPanelCtrl = adminPanelCtrl;
        this.loginScreenCtrl = loginScreenCtrl;
        this.inviteScreenCtrl = inviteScreenCtrl;
        this.debtOverviewCtrl = debtOverviewCtrl;
        this.addTagCtrl = addTagCtrl;
        this.editTagCtrl = editTagCtrl;
        this.statisticsScreenCtrl = statisticsScreenCtrl;
    }

    /**
     * Updates the resource bundle instance
     *
     * @param bundle new bundle to be set
     */
    public void updateResources(ResourceBundle bundle) {
        resources = bundle;
    }

    @FXML
    private void initialize() {
        Image styleButtonIcon = new Image("client/scenes/icons/lightMode.png");
        imgMode.setImage(styleButtonIcon);

        languageDropdown.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Language> call(ListView<Language> languages) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Language item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? "" : item.name());
                    }
                };
            }
        });

        languageDropdown.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Language item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.name());
            }
        });

        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/flags/" + configManager.getLanguage() + ".png")));
        flag.setImage(image);

        ObservableList<Language> languagesList = FXCollections.observableArrayList();
        Language selectedLanguage = null;

        for (String code : availableLanguages) {
            ResourceBundle rb = ResourceBundle.getBundle("languages.labels", new Locale(code));
            String name = rb.getString("language_name");
            Language language = new Language(name, code);
            languagesList.add(language);

            if (configManager.getLanguage().equals(code)) {
                selectedLanguage = language;
            }
        }

        languageDropdown.setItems(languagesList);
        languageDropdown.getSelectionModel().select(selectedLanguage);

        setLightMode();
    }

    /**
     * Triggered when the back button is pressed
     *
     * @param event of the button press
     */
    public void onBack(ActionEvent event) {
        if (onReturn == null) {
            mainCtrl.showStartScreen();
        } else {
            onReturn.apply(null);
        }
    }

    /**
     * Sets up the dropdown menu on the language selection when the screen is opened
     *
     * @param onReturn callback function so when the back button is pressed, the user is redirected back to the correct page
     */
    public void onShowSettings(Function<Void, Void> onReturn) {
        this.onReturn = onReturn;
    }

    /**
     * Triggered when the language is changed
     *
     * @param event of the button press
     */
    public void onLanguageSwitch(ActionEvent event) {
        if (languageDropdown.getSelectionModel().getSelectedItem() == null) return;
        configManager.setLanguage(languageDropdown.getSelectionModel().getSelectedItem().code());
    }

    /**
     * Triggered when the save button is pressed
     *
     * @param event of the button press
     */
    @FXML
    void onSave(ActionEvent event) {
        ResourceBundle bundle = ResourceBundle.getBundle("languages.labels", new Locale(configManager.getLanguage()));

        var start = Main.FXML.load(StartScreenCtrl.class, bundle, "client", "scenes", "StartScreen.fxml");
        var overview = Main.FXML.load(EventOverviewCtrl.class, bundle, "client", "scenes", "EventOverview.fxml");
        var invite = Main.FXML.load(InviteScreenCtrl.class, bundle, "client", "scenes", "InviteScreen.fxml");
        var participant = Main.FXML.load(AddParticipantCtrl.class, bundle, "client", "scenes", "AddParticipant.fxml");
        var expense = Main.FXML.load(AddExpenseCtrl.class, bundle, "client", "scenes", "AddExpense.fxml");
        var tag = Main.FXML.load(AddTagCtrl.class, bundle, "client", "scenes", "AddTag.fxml");
        var tagEdit = Main.FXML.load(EditTagCtrl.class, bundle, "client", "scenes", "EditTag.fxml");
        var debt = Main.FXML.load(DebtOverviewCtrl.class, bundle, "client", "scenes", "DebtOverview.fxml");
        var settings = Main.FXML.load(SettingsScreenCtrl.class, bundle, "client", "scenes", "SettingsScreen.fxml");
        var login = Main.FXML.load(LoginScreenCtrl.class, bundle, "client", "scenes", "LoginScreen.fxml");
        var admin = Main.FXML.load(AdminPanelCtrl.class, bundle, "client", "scenes", "AdminPanel.fxml");
        var statistics = Main.FXML.load(StatisticsScreenCtrl.class, bundle, "client", "scenes", "StatisticsScreen.fxml");

        var mainCtrl = Main.INJECTOR.getInstance(MainCtrl.class);
        mainCtrl.reinitialize(start, overview, invite, participant, expense, tag, tagEdit, debt, settings, login, admin, statistics, bundle);
        mainCtrl.showSettingsScreen(onReturn);

        changeStyleMode(isLightMode);
    }

    /**
     * Triggered when the download template button is pressed
     *
     * @param actionEvent of the button press
     */
    public void onDownloadTemplate(ActionEvent actionEvent) {
        InputStream inputStream = getClass().getResourceAsStream("/languages/labels.properties");

        if (inputStream != null) {
            try {
                String destinationPath = System.getProperty("user.home") + "/Downloads/template.properties";
                OutputStream outputStream = new FileOutputStream(destinationPath);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                outputStream.close();
                inputStream.close();

                Alert alert = new Alert(Alert.AlertType.INFORMATION, resources.getString("language_template_saved_successfully"), ButtonType.OK);
                alert.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Template file not found.", ButtonType.OK);
            alert.show();
        }
    }

    /**
     * Triggered when mode is changed
     *
     * @param event of the click
     */
    public void changeMode(ActionEvent event) {
        isLightMode = !isLightMode;
        changeStyleMode(isLightMode);
    }

    /**
     * Changes the mode of all screens.
     *
     * @param isLightMode True iff in light mode.
     */
    public void changeStyleMode(boolean isLightMode) {
        if (isLightMode) {
            setLightMode();
        } else {
            setDarkMode();
        }

        startScreenCtrl.changeStyleMode(isLightMode);
        eventOverviewCtrl.changeStyleMode(isLightMode);
        inviteScreenCtrl.changeStyleMode(isLightMode);
        addParticipantCtrl.changeStyleMode(isLightMode);
        addExpenseCtrl.changeStyleMode(isLightMode);
        addTagCtrl.changeStyleMode(isLightMode);
        debtOverviewCtrl.changeStyleMode(isLightMode);
        loginScreenCtrl.changeStyleMode(isLightMode);
        adminPanelCtrl.changeStyleMode(isLightMode);
        statisticsScreenCtrl.changeStyleMode(isLightMode);
        editTagCtrl.changeStyleMode(isLightMode);
    }

    private void setLightMode() {
        parent.getStylesheets().remove("/client/styles/DarkMode.css");
        parent.getStylesheets().add("/client/styles/LightMode.css");
        Image image = new Image("client/scenes/icons/lightMode.png");
        labelMode.setText(resources.getString("light_mode"));
        labelMode.setTextFill(new Color(0, 0, 0, 1));
        imgMode.setImage(image);
    }

    private void setDarkMode() {
        parent.getStylesheets().remove("/client/styles/LightMode.css");
        parent.getStylesheets().add("/client/styles/DarkMode.css");
        Image image = new Image("client/scenes/icons/darkMode.png");
        labelMode.setText(resources.getString("dark_mode"));
        labelMode.setTextFill(new Color(1, 1, 1, 1));
        imgMode.setImage(image);
    }

}
