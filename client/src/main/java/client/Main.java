package client;

import static com.google.inject.Guice.createInjector;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.ResourceBundle;

import client.utils.ConfigManager;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.stage.Stage;

import client.scenes.*;

public class Main extends Application {

    public static final Injector INJECTOR = createInjector(new MyModule());
    public static final MyFXML FXML = new MyFXML(INJECTOR);

    /**
     * Launches the application.
     *
     * @param args Program arguments.
     * @throws URISyntaxException URISyntaxException.
     * @throws IOException        IOException.
     */
    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }

    /**
     * Starts the UI.
     *
     * @param primaryStage The primary stage.
     * @throws IOException IOException.
     */
    @Override
    public void start(Stage primaryStage) {
        ConfigManager configManager = new ConfigManager("config.properties");
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

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        mainCtrl.initialize(primaryStage, start, overview, invite, participant, expense, tag, tagEdit,
                debt, settings, login, admin, statistics, bundle);

        primaryStage.setOnCloseRequest(event -> admin.getKey().stopExecutors());
    }
}
