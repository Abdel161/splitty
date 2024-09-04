package client.scenes;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ResourceBundle;
import java.util.function.Function;

import commons.dtos.EventDTO;
import commons.dtos.ExpenseDTO;
import commons.dtos.ParticipantDTO;
import commons.dtos.TagDTO;
import commons.Debt;

public class MainCtrl {

    private Stage primaryStage;
    private ResourceBundle resources;

    private StartScreenCtrl startScreenCtrl;
    private Scene startScreen;

    private EventOverviewCtrl eventOverviewCtrl;
    private Scene eventOverview;

    private InviteScreenCtrl inviteScreenCtrl;
    private Scene inviteScreen;

    private AddParticipantCtrl addParticipantCtrl;
    private Scene addParticipant;

    private AddExpenseCtrl addExpenseCtrl;
    private Scene addExpense;

    private AddTagCtrl addTagCtrl;
    private Scene addTagScreen;

    private EditTagCtrl editTagCtrl;
    private Scene editTagScreen;

    private DebtOverviewCtrl debtOverviewCtrl;
    private Scene debtOverviewScreen;

    private SettingsScreenCtrl settingsScreenCtrl;
    private Scene settingsScreen;

    private LoginScreenCtrl loginScreenCtrl;
    private Scene loginScreen;

    private AdminPanelCtrl adminPanelCtrl;
    private Scene adminPanel;

    private StatisticsScreenCtrl statisticsScreenCtrl;
    private Scene statisticsScreen;

    /**
     * Initializes the main control.
     *
     * @param primaryStage The primary stage.
     * @param start        The start screen menu.
     * @param overview     The event overview menu.
     * @param invite       The invite screen menu
     * @param participant  The add participant menu.
     * @param expense      The add expense menu.
     * @param tag          The add tag screen.
     * @param tagEdit      The edit tag screen.
     * @param debt         The open debts menu
     * @param settings     The settings screen menu.
     * @param login        The login screen menu.
     * @param admin        The admin screen menu.
     * @param statistics   The statistics screen menu.
     * @param resources    ResourceBundle resources
     */
    public void initialize(Stage primaryStage, Pair<StartScreenCtrl, Parent> start, Pair<EventOverviewCtrl, Parent> overview,
                           Pair<InviteScreenCtrl, Parent> invite, Pair<AddParticipantCtrl, Parent> participant, Pair<AddExpenseCtrl, Parent> expense,
                           Pair<AddTagCtrl, Parent> tag, Pair<EditTagCtrl, Parent> tagEdit, Pair<DebtOverviewCtrl, Parent> debt,
                           Pair<SettingsScreenCtrl, Parent> settings,
                           Pair<LoginScreenCtrl, Parent> login, Pair<AdminPanelCtrl, Parent> admin, Pair<StatisticsScreenCtrl, Parent> statistics,
                           ResourceBundle resources) {
        this.primaryStage = primaryStage;

        reinitialize(start, overview, invite, participant, expense, tag, tagEdit, debt, settings, login, admin, statistics, resources);

        showStartScreen();
        primaryStage.show();
    }

    /**
     * Reinitializes the main control.
     *
     * @param start       The start screen menu.
     * @param overview    The event overview menu.
     * @param invite      The invite screen menu
     * @param participant The add participant menu.
     * @param expense     The add expense menu.
     * @param tag         The add tag screen.
     * @param tagEdit     The edit tag screen.
     * @param debt        The open debts menu
     * @param settings    The settings screen menu.
     * @param login       The login screen menu.
     * @param admin       The admin screen menu.
     * @param statistics  The statistics screen menu.
     * @param resources   ResourceBundle resources.
     */
    public void reinitialize(Pair<StartScreenCtrl, Parent> start, Pair<EventOverviewCtrl, Parent> overview, Pair<InviteScreenCtrl, Parent> invite,
                             Pair<AddParticipantCtrl, Parent> participant, Pair<AddExpenseCtrl, Parent> expense,
                             Pair<AddTagCtrl, Parent> tag, Pair<EditTagCtrl, Parent> tagEdit, Pair<DebtOverviewCtrl, Parent> debt,
                             Pair<SettingsScreenCtrl, Parent> settings,
                             Pair<LoginScreenCtrl, Parent> login, Pair<AdminPanelCtrl, Parent> admin, Pair<StatisticsScreenCtrl, Parent> statistics,
                             ResourceBundle resources) {
        String styleCssPath = getClass().getResource("/client/styles/ProjectStyle.css").toExternalForm();

        this.startScreenCtrl = start.getKey();
        this.startScreen = new Scene(start.getValue());
        this.startScreen.getStylesheets().add(styleCssPath);
        startScreenCtrl.updateResources(resources);

        this.eventOverviewCtrl = overview.getKey();
        this.eventOverview = new Scene(overview.getValue());
        this.eventOverview.getStylesheets().add(styleCssPath);
        eventOverviewCtrl.updateResources(resources);
        setUpKeyboardBack(eventOverview, () -> eventOverviewCtrl.onBack(new ActionEvent()));

        this.inviteScreenCtrl = invite.getKey();
        this.inviteScreen = new Scene(invite.getValue());
        this.inviteScreen.getStylesheets().add(styleCssPath);
        inviteScreenCtrl.updateResources(resources);
        setUpKeyboardBack(inviteScreen, () -> inviteScreenCtrl.onBack(new ActionEvent()));

        this.addParticipantCtrl = participant.getKey();
        this.addParticipant = new Scene(participant.getValue());
        this.addParticipant.getStylesheets().add(styleCssPath);
        addParticipantCtrl.updateResources(resources);
        setUpKeyboardBack(addParticipant, () -> addParticipantCtrl.cancel());

        this.addExpenseCtrl = expense.getKey();
        this.addExpense = new Scene(expense.getValue());
        this.addExpense.getStylesheets().add(styleCssPath);
        addExpenseCtrl.updateResources(resources);
        setUpKeyboardBack(addExpense, () -> addExpenseCtrl.cancel());

        this.addTagCtrl = tag.getKey();
        this.addTagScreen = new Scene(tag.getValue());
        this.addTagScreen.getStylesheets().add(styleCssPath);
        addTagCtrl.updateResources(resources);
        setUpKeyboardBack(addTagScreen, () -> addTagCtrl.goBack(new ActionEvent()));

        this.editTagCtrl = tagEdit.getKey();
        this.editTagScreen = new Scene(tagEdit.getValue());
        this.editTagScreen.getStylesheets().add(styleCssPath);
        editTagCtrl.updateResources(resources);
        setUpKeyboardBack(editTagScreen, () -> editTagCtrl.goBack(new ActionEvent()));

        this.debtOverviewCtrl = debt.getKey();
        this.debtOverviewScreen = new Scene(debt.getValue());
        this.debtOverviewScreen.getStylesheets().add(styleCssPath);
        debtOverviewCtrl.updateResources(resources);
        setUpKeyboardBack(debtOverviewScreen, () -> debtOverviewCtrl.onBack(new ActionEvent()));

        this.settingsScreenCtrl = settings.getKey();
        this.settingsScreen = new Scene(settings.getValue());
        this.settingsScreen.getStylesheets().add(styleCssPath);
        settingsScreenCtrl.updateResources(resources);
        setUpKeyboardBack(settingsScreen, () -> settingsScreenCtrl.onBack(new ActionEvent()));

        this.loginScreenCtrl = login.getKey();
        this.loginScreen = new Scene(login.getValue());
        this.loginScreen.getStylesheets().add(styleCssPath);
        loginScreenCtrl.updateResources(resources);
        setUpKeyboardBack(loginScreen, () -> loginScreenCtrl.onBack(new ActionEvent()));

        this.adminPanelCtrl = admin.getKey();
        this.adminPanel = new Scene(admin.getValue());
        this.adminPanel.getStylesheets().add(styleCssPath);
        adminPanelCtrl.updateResources(resources);
        setUpKeyboardBack(adminPanel, () -> adminPanelCtrl.onBack(new ActionEvent()));

        this.statisticsScreenCtrl = statistics.getKey();
        this.statisticsScreen = new Scene(statistics.getValue());
        this.statisticsScreen.getStylesheets().add(styleCssPath);
        statisticsScreenCtrl.updateResources(resources);
        setUpKeyboardBack(statisticsScreen, () -> statisticsScreenCtrl.onBack(new ActionEvent()));

        settingsScreenCtrl.changeStyleMode(true);
        this.resources = resources;
    }

    private void setUpKeyboardBack(Scene scene, Runnable function) {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.BACK_SPACE) {
                    function.run();
                }
            }
        });
    }

    /**
     * Shows the event overview screen.
     */
    public void showStartScreen() {
        primaryStage.setTitle(resources.getString("start_screen"));
        primaryStage.setScene(startScreen);
        primaryStage.setResizable(false);
        startScreenCtrl.populateRecentlyViewedGrid();
    }

    /**
     * Shows the event overview screen.
     */
    public void showEventOverview() {
        primaryStage.setTitle(resources.getString("event_overview"));
        primaryStage.setScene(eventOverview);
        primaryStage.setResizable(false);
    }

    /**
     * Shows the event overview screen and sets the specified event data.
     *
     * @param event Event data.
     */
    public void showEventOverview(EventDTO event) {
        primaryStage.setTitle(resources.getString("event_overview"));
        primaryStage.setScene(eventOverview);
        primaryStage.setResizable(false);
        eventOverviewCtrl.setEvent(event);
    }

    /**
     * Shows the event overview screen and sets the specified event data.
     *
     * @param event      Event data.
     * @param backScreen Screen to go back.
     */
    public void showEventOverview(EventDTO event, String backScreen) {
        primaryStage.setTitle(resources.getString("event_overview"));
        primaryStage.setScene(eventOverview);
        eventOverviewCtrl.setEvent(event);
        primaryStage.setResizable(false);
        eventOverviewCtrl.setBackScreen(backScreen);
    }

    /**
     * Shows the add participant screen.
     *
     * @param event The event for which the participant is being added.
     */
    public void showAddParticipant(EventDTO event) {
        primaryStage.setTitle(resources.getString("add_participant"));
        primaryStage.setScene(addParticipant);
        primaryStage.setResizable(false);
        addParticipantCtrl.setEvent(event);
    }

    /**
     * Shows the edit participant screen.
     *
     * @param event       The event for which the participant is being edited.
     * @param participant The ParticipantDto object containing the details of the participant to be edited.
     */
    public void showEditParticipant(EventDTO event, ParticipantDTO participant) {
        primaryStage.setTitle(resources.getString("edit_participant"));
        primaryStage.setScene(addParticipant);
        primaryStage.setResizable(false);
        addParticipantCtrl.setEvent(event);
        addParticipantCtrl.setParticipant(participant);
    }

    /**
     * Shows the add expense screen.
     *
     * @param event        The event to which the expenses belong
     * @param participants The list of participants involved in the event.
     * @param tags         The list of tags that were created in the event
     * @param expenses     The list of expenses that were created in the event
     */
    public void showAddExpense(EventDTO event, ObservableList<ParticipantDTO> participants,
                               ObservableList<TagDTO> tags, ObservableList<ExpenseDTO> expenses) {
        primaryStage.setTitle(resources.getString("add_expense"));
        primaryStage.setScene(addExpense);
        primaryStage.setResizable(false);
        addExpenseCtrl.setEvent(event);
        addExpenseCtrl.setParticipants(participants);
        addExpenseCtrl.setTags(tags);
        addExpenseCtrl.setExpenses(expenses);
    }

    /**
     * Shows the edit expense screen.
     *
     * @param event        The event to which the expenses belong
     * @param participants The list of participants involved in the event.
     * @param tags         The list of tags that were created in the event
     * @param expense      The expense to be edited.
     * @param expenses     The expenses to of the event.
     */
    public void showEditExpense(EventDTO event, ObservableList<ParticipantDTO> participants,
                                ObservableList<TagDTO> tags, ExpenseDTO expense, ObservableList<ExpenseDTO> expenses) {
        primaryStage.setTitle(resources.getString("edit_expense"));
        primaryStage.setScene(addExpense);
        primaryStage.setResizable(false);
        addExpenseCtrl.setEvent(event);
        addExpenseCtrl.setParticipants(participants);
        addExpenseCtrl.setTags(tags);
        addExpenseCtrl.setExpense(expense);
        addExpenseCtrl.setExpenses(expenses);
    }

    /**
     * Shows the open debts scene.
     *
     * @param event        Current event.
     * @param participants Available participants.
     * @param debts        Debts.
     */
    public void showOpenDebts(EventDTO event, ObservableList<ParticipantDTO> participants, ObservableList<Debt> debts) {
        primaryStage.setTitle(resources.getString("settle_debts_label"));
        primaryStage.setScene(debtOverviewScreen);
        primaryStage.setResizable(false);
        debtOverviewCtrl.setEvent(event);
        debtOverviewCtrl.setParticipants(participants);
        debtOverviewCtrl.setDebts(debts);
    }


    /**
     * Shows the open statistics scene.
     *
     * @param expenses Available expenses.
     * @param tags     Available tags.
     */
    public void showStatisticsPage(ObservableList<ExpenseDTO> expenses, ObservableList<TagDTO> tags) {
        primaryStage.setTitle(resources.getString("statistics"));
        primaryStage.setScene(statisticsScreen);
        primaryStage.setResizable(false);
        statisticsScreenCtrl.setUpStatistics(expenses, tags);
    }

    /**
     * Shows the invite screen.
     *
     * @param event to populate the invite screen
     */
    public void showInviteScreen(EventDTO event) {
        primaryStage.setTitle(resources.getString("invite"));
        primaryStage.setScene(inviteScreen);
        primaryStage.setResizable(false);
        inviteScreenCtrl.setEvent(event);
    }

    /**
     * Shows the invite screen.
     *
     * @param onBack callback function so the button returns to the correct screen
     */
    public void showSettingsScreen(Function<Void, Void> onBack) {
        primaryStage.setTitle(resources.getString("settings"));
        primaryStage.setScene(settingsScreen);
        primaryStage.setResizable(false);
        settingsScreenCtrl.onShowSettings(onBack);
    }

    /**
     * Shows the login screen.
     */
    public void showLoginScreen() {
        primaryStage.setTitle(resources.getString("log_in_to_admin_panel"));
        primaryStage.setScene(loginScreen);
        primaryStage.setResizable(false);
    }

    /**
     * Shows the admin screen.
     */
    public void showAdminScreen() {
        primaryStage.setTitle(resources.getString("admin_panel"));
        primaryStage.setScene(adminPanel);
        primaryStage.setResizable(false);
    }

    /**
     * It displays the add new tag page
     *
     * @param event            event to be set
     * @param isEditingExpense if previous page is edit expense or not
     */
    public void showAddTagScreen(EventDTO event, boolean isEditingExpense) {
        primaryStage.setTitle(resources.getString("create_tag_label"));
        primaryStage.setScene(addTagScreen);
        primaryStage.setResizable(false);
        addTagCtrl.setEvent(event, isEditingExpense);
    }

    /**
     * Displays add/edit expense screen and sets the appropriate title.
     *
     * @param isEditingExpense if previous page was editing expense
     */
    public void returnToAddExpenseScreen(boolean isEditingExpense) {
        primaryStage.setTitle(resources.getString(isEditingExpense ? "edit_expense" : "add_expense"));
        primaryStage.setScene(addExpense);
        primaryStage.setResizable(false);
    }

    /**
     * It displays the add new tag page
     *
     * @param currentEvent     event to be set
     * @param isEditingExpense if previous page is edit expense or not
     * @param tags             tags of the current event
     * @param expenses         expenses of the current event
     */
    public void showEditTagScreen(EventDTO currentEvent, boolean isEditingExpense,
                                  ObservableList<TagDTO> tags, ObservableList<ExpenseDTO> expenses) {
        primaryStage.setTitle(resources.getString("edit_tag_label"));
        primaryStage.setScene(editTagScreen);
        primaryStage.setResizable(false);
        editTagCtrl.setEvent(currentEvent, isEditingExpense);
        editTagCtrl.setTags(tags);
        editTagCtrl.setExpenses(expenses);
    }
}
