package client.scenes;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

import org.springframework.messaging.simp.stomp.StompSession;

import client.utils.ConfigManager;
import client.utils.ExchangeManager;
import client.utils.ServerUtils;

import commons.dtos.*;
import commons.messages.*;
import commons.Debt;

public class EventOverviewCtrl {

    private enum ExpenseFilter {
        ALL, FROM, INCLUDING
    }

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final ExchangeManager exchange;
    private final ConfigManager configManager;
    private ResourceBundle resources;

    private boolean isLight;

    @FXML
    private Label inviteCodeLabel;
    @FXML
    private Button copyButton;
    @FXML
    private ImageView flag;
    @FXML
    private GridPane expensesGrid;
    @FXML
    private TextField editEventNameField;
    @FXML
    private GridPane eventName;
    @FXML
    private Button backButton;
    @FXML
    private Button editEventNameButton;
    @FXML
    private GridPane participantsGrid;
    @FXML
    private Button sendInvitesButton;
    @FXML
    private Button allExpensesButton;
    @FXML
    private Button fromSelectedButton;
    @FXML
    private Button includingSelectedButton;
    @FXML
    private ComboBox<ParticipantDTO> participantDropdown;
    @FXML
    private Button settleDebtsButton;
    @FXML
    private Label eventNameLabel;
    @FXML
    private VBox parent;

    private EventDTO currentEvent;
    private ObservableList<ExpenseDTO> currentExpenses;
    private StompSession.Subscription currentExpensesSubscription;
    private ObservableList<ParticipantDTO> currentParticipants;
    private StompSession.Subscription currentParticipantsSubscription;
    private ObservableList<TagDTO> currentTags;
    private StompSession.Subscription currentTagsSubscription;
    private ObservableList<Debt> currentDebts;
    private StompSession.Subscription currentDebtsSubscription;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<ParticipantDTO> lastFilterParticipant = Optional.empty();
    private ExpenseFilter lastFilterMode = ExpenseFilter.ALL;

    private boolean isNameEditing;
    private String backScreen = "start";

    /**
     * Constructs an EventOverviewCtrl instance.
     *
     * @param server        ServerUtils instance.
     * @param mainCtrl      MainCtrl instance.
     * @param exchange      ExchangeManager instance.
     * @param configManager ConfigManager instance.
     * @param resources     ResourceBundle resources.
     */
    @Inject
    public EventOverviewCtrl(ServerUtils server, MainCtrl mainCtrl, ExchangeManager exchange, ConfigManager configManager, ResourceBundle resources) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.exchange = exchange;
        this.configManager = configManager;
        this.resources = resources;
        this.isLight = true;
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
        participantDropdown.setCellFactory(new Callback<>() {
            @Override
            public ListCell<ParticipantDTO> call(ListView<ParticipantDTO> participants) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(ParticipantDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? "" : item.name());
                    }
                };
            }
        });

        participantDropdown.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ParticipantDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.name());
            }
        });

        participantDropdown.getSelectionModel().selectedItemProperty().addListener((names, previousName, newName) -> {
            boolean isParticipantSelected = newName != null;
            if (isParticipantSelected) {
                String selectedName = newName.name();
                fromSelectedButton.setText(resources.getString("from_label") + " " + selectedName);
                includingSelectedButton.setText(resources.getString("including_label") + " " + selectedName);
            }

            fromSelectedButton.setDisable(!isParticipantSelected);
            includingSelectedButton.setDisable(!isParticipantSelected);
        });

        allExpensesButton.setOnAction(actionEvent -> populateExpensesGrid(Optional.empty(), ExpenseFilter.ALL));

        fromSelectedButton.setOnAction(actionEvent -> {
            ParticipantDTO selectedParticipant = participantDropdown.getSelectionModel().getSelectedItem();
            if (selectedParticipant != null) {
                populateExpensesGrid(Optional.of(selectedParticipant), ExpenseFilter.FROM);
            }
        });

        includingSelectedButton.setOnAction(actionEvent -> {
            ParticipantDTO selectedParticipant = participantDropdown.getSelectionModel().getSelectedItem();
            if (selectedParticipant != null) {
                populateExpensesGrid(Optional.of(selectedParticipant), ExpenseFilter.INCLUDING);
            }
        });
    }

    /**
     * Shows the add participant screen.
     */
    public void addParticipant() {
        mainCtrl.showAddParticipant(currentEvent);
    }

    /**
     * Shows the add expense screen.
     */
    public void addExpense() {
        mainCtrl.showAddExpense(currentEvent, currentParticipants, currentTags, currentExpenses);
    }

    /**
     * Shows the debt screen
     */
    public void showOpenDebts() {
        mainCtrl.showOpenDebts(currentEvent, currentParticipants, currentDebts);
    }

    /**
     * Navigate to the invite screen when the send invites
     *
     * @param actionEvent of the button press
     */
    public void onSendInvites(ActionEvent actionEvent) {
        mainCtrl.showInviteScreen(currentEvent);
    }

    /**
     * Sets a flag to indicate where the back button of the event overview screen should redirect to
     *
     * @param backScreen flag to be set
     */
    public void setBackScreen(String backScreen) {
        this.backScreen = backScreen;
    }

    /**
     * Sets the event data, including the involved Participants and corresponding Expenses.
     *
     * @param event Event data.
     */
    public void setEvent(EventDTO event) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/flags/" + configManager.getLanguage() + ".png")));
        flag.setImage(image);
        mainCtrl.showEventOverview();
        currentEvent = event;

        if (currentParticipantsSubscription != null) currentParticipantsSubscription.unsubscribe();
        currentParticipants = FXCollections.observableArrayList(server.getParticipants(event.id()));
        currentParticipantsSubscription = server.registerForMessages("/topic/events/" + event.id() + "/participants",
                ParticipantsMessage.class, message -> Platform.runLater(() -> {
                    currentParticipants.setAll(message.participants());
                    populateParticipantsGrid();
                    clearSelectionAndResetButtonTexts();
                    populateExpensesGrid(Optional.empty(), ExpenseFilter.ALL);
                }));

        if (currentExpensesSubscription != null) currentExpensesSubscription.unsubscribe();
        currentExpenses = FXCollections.observableArrayList(server.getExpenses(event.id()));
        currentExpensesSubscription = server.registerForMessages("/topic/events/" + event.id() + "/expenses", ExpensesMessage.class,
                message -> Platform.runLater(() -> {
                    currentExpenses.setAll(message.expenses());
                    populateExpensesGrid(lastFilterParticipant, lastFilterMode);
                }));

        if (currentTagsSubscription != null) currentTagsSubscription.unsubscribe();
        currentTags = FXCollections.observableArrayList(server.getAllTags(event.id()));
        currentTagsSubscription = server.registerForMessages("/topic/events/" + event.id() + "/tags", TagsMessage.class, message -> {
            Platform.runLater(() -> {
                currentTags.setAll(message.tags());
            });
        });

        if (currentDebtsSubscription != null) currentDebtsSubscription.unsubscribe();
        currentDebts = FXCollections.observableArrayList(server.getDebts(event.id()));
        currentDebtsSubscription = server.registerForMessages("/topic/events/" + event.id() + "/debts", DebtsMessage.class, message -> {
            Platform.runLater(() -> {
                currentDebts.setAll(message.debts());
            });
        });

        eventNameLabel.setText(event.title());
        inviteCodeLabel.setText(resources.getString("invite_code") + " " + event.inviteCode());

        populateParticipantsGrid();
        participantDropdown.setItems(currentParticipants);
        clearSelectionAndResetButtonTexts();
        populateExpensesGrid(Optional.empty(), ExpenseFilter.ALL);
    }

    /**
     * Generates a button with the corresponding image.
     *
     * @param resourcePath The path to the image.
     * @return The generated button with the corresponding image.
     */
    private Button generateButtonWithIcon(String resourcePath) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(resourcePath)));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(21);
        imageView.setFitWidth(21);

        if (!isLight) {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(1);
            colorAdjust.setContrast(0);
            colorAdjust.setSaturation(0);
            colorAdjust.setHue(0);

            imageView.setEffect(colorAdjust);
        }

        Button button = new Button();
        button.setGraphic(imageView);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        return button;
    }

    /**
     * Populates 'participantsGrid' with the data of the Participants  related to the current Event.
     */
    private void populateParticipantsGrid() {
        participantsGrid.getChildren().clear();

        int row = 0;
        for (ParticipantDTO participant : currentParticipants) {
            Label participantLabel = new Label(participant.name());
            if (!isLight) {
                participantLabel.setTextFill(Color.rgb(255, 255, 255));
            }
            Button editParticipantButton = generateButtonWithIcon("/client/scenes/icons/editIcon.png");
            Button deleteParticipantButton = generateButtonWithIcon("/client/scenes/icons/deleteIcon.png");

            editParticipantButton.setOnAction(actionEvent -> mainCtrl.showEditParticipant(currentEvent, participant));
            deleteParticipantButton.setOnAction(actionEvent -> deleteParticipantConfirmation(participant));

            participantsGrid.addRow(row++, participantLabel, editParticipantButton, deleteParticipantButton);
        }
    }

    /**
     * Shows a confirmation dialog for deleting the Participant.
     * If the user wants to delete the Participant, it is checked if the Participant is involved in any Expenses.
     * If so, the deletion is being stopped.
     *
     * @param participant The Participant to be deleted.
     */
    private void deleteParticipantConfirmation(ParticipantDTO participant) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, resources.getString("confirmation_delete_participant"),
                new ButtonType(resources.getString("yes_button"), ButtonBar.ButtonData.YES),
                new ButtonType(resources.getString("no_button"), ButtonBar.ButtonData.NO));

        confirmationAlert.setTitle(resources.getString("confirmation_title"));
        confirmationAlert.setHeaderText(resources.getString("confirm_deletion"));

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.YES) {
            if (isInvolvedInAnyExpense(participant)) {
                Alert warningDeletion = new Alert(Alert.AlertType.INFORMATION,
                        resources.getString("delete_participant_warning"), ButtonType.CLOSE);
                warningDeletion.setHeaderText(resources.getString("delete_warning"));
                warningDeletion.showAndWait();
                return;
            }

            boolean isDeleted = server.deleteParticipant(currentEvent.id(), participant.id());
            if (isDeleted) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION,
                        resources.getString("confirm_deletion_success_participant"), ButtonType.OK);
                successAlert.setTitle(resources.getString("success"));
                successAlert.showAndWait();
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, resources.getString("something_went_wrong_participant"), ButtonType.OK);
                errorAlert.setTitle(resources.getString("error_title"));
                errorAlert.showAndWait();
            }
        }
    }

    /**
     * Checks if the participant is involved in any form in an expense.
     *
     * @param participant the participant that we need to check
     * @return true if participant is part of an expense, else false
     */
    private boolean isInvolvedInAnyExpense(ParticipantDTO participant) {
        for (ExpenseDTO expense : currentExpenses) {
            if (expense.payerId() == participant.id() || expense.returnerIds().contains(participant.id())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Populates expensesGrid with data of the Expenses corresponding to the Event.
     *
     * @param filterParticipant filter for a specific participant.
     * @param filterMode        The type of filter applied to expensesGrid.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void populateExpensesGrid(Optional<ParticipantDTO> filterParticipant, ExpenseFilter filterMode) {
        lastFilterParticipant = filterParticipant;
        lastFilterMode = filterMode;

        expensesGrid.getChildren().clear();

        List<ExpenseDTO> filteredExpenses = getFilteredExpenses(filterParticipant, filterMode);
        int row = 0;
        for (ExpenseDTO expense : filteredExpenses) {
            Label tagLabel = generateTagLabel(expense);
            Label expenseLabel = generateExpenseLabel(expense);
            if (!isLight) {
                expenseLabel.setTextFill(Color.rgb(255, 255, 255));
            }
            Button editExpenseButton = generateButtonWithIcon("/client/scenes/icons/editIcon.png");
            Button deleteExpenseButton = generateButtonWithIcon("/client/scenes/icons/deleteIcon.png");

            editExpenseButton.setOnAction(actionEvent ->
                    mainCtrl.showEditExpense(currentEvent, currentParticipants, currentTags, expense, currentExpenses));
            deleteExpenseButton.setOnAction(actionEvent -> deleteExpenseConfirmation(expense));

            expensesGrid.addRow(row++, tagLabel, expenseLabel, editExpenseButton, deleteExpenseButton);
        }
    }

    /**
     * Filters the current expenses with the specified participant and filter mode.
     *
     * @param filterParticipant Participant to filter with.
     * @param filterMode        Filter mode.
     * @return List of filtered expenses.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private List<ExpenseDTO> getFilteredExpenses(Optional<ParticipantDTO> filterParticipant, ExpenseFilter filterMode) {
        Stream<ExpenseDTO> expensesStream = currentExpenses.stream();

        if (filterParticipant.isPresent()) {
            long participantId = filterParticipant.get().id();
            switch (filterMode) {
                case FROM:
                    expensesStream = expensesStream.filter(expense -> expense.payerId() == participantId);
                    break;
                case INCLUDING:
                    expensesStream = expensesStream.filter(expense -> expense.payerId() == participantId
                            || expense.returnerIds().contains(participantId));
                    break;
                case ALL:
                default:
                    break;
            }
        }

        return expensesStream.toList();
    }

    /**
     * Handles the deletion process for an expense, which includes the confirmation of the user.
     *
     * @param expense The expense to be deleted.
     */
    private void deleteExpenseConfirmation(ExpenseDTO expense) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, resources.getString("confirmation_delete_expense"),
                new ButtonType(resources.getString("yes_button"), ButtonBar.ButtonData.YES),
                new ButtonType(resources.getString("no_button"), ButtonBar.ButtonData.NO));

        confirmationAlert.setTitle(resources.getString("confirmation_title"));
        confirmationAlert.setHeaderText(resources.getString("confirm_deletion"));

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.YES) {
            boolean isDeleted = server.deleteExpense(currentEvent.id(), expense.id());
            if (isDeleted) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION, resources.getString("delete_success_message"), ButtonType.OK);
                successAlert.setTitle(resources.getString("success"));
                successAlert.showAndWait();
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, resources.getString("delete_error_message"), ButtonType.OK);
                errorAlert.setTitle(resources.getString("error_title"));
                errorAlert.showAndWait();
            }
        }
    }

    /**
     * Sets the text on the buttons back to their default values and clears
     * the selected participant in participantDropdown.
     */
    private void clearSelectionAndResetButtonTexts() {
        fromSelectedButton.setText(resources.getString("from_label"));
        includingSelectedButton.setText(resources.getString("including_label"));
        participantDropdown.getSelectionModel().clearSelection();
    }

    /**
     * Sets up a label containing the data of the Expense
     *
     * @param expense the Expense to which the Label belongs.
     * @return A Label including information about the expense.
     */
    private Label generateTagLabel(ExpenseDTO expense) {
        if (expense.isDebt()) {
            Label label = new Label("(" + resources.getString("debt") + ")");
            if (!isLight) {
                label.setStyle("-fx-text-fill: white");
            }
            return label;
        }
        if (expense.tagId() == 0) {
            Label label = new Label("(" + resources.getString("no_tag") + ")");
            if (!isLight) {
                label.setStyle("-fx-text-fill: white");
            }
            return label;
        }

        TagDTO tag = currentTags.stream().filter((tagDto) -> tagDto.id() == expense.tagId()).findFirst().get();
        Label label = new Label(tag.name());
        String color = tag.color().startsWith("#") ? tag.color() : String.format("#%08x", Long.parseLong(tag.color().substring(2), 16));
        label.setStyle("-fx-background-color:" + color + "; -fx-padding: 3px; -fx-background-radius: 5px");

        return label;
    }

    /**
     * Sets up a label containing the data of the Expense
     *
     * @param expense the Expense to which the Label belongs.
     * @return A Label including information about the expense.
     */
    private Label generateExpenseLabel(ExpenseDTO expense) {
        String date = toLocalDate(expense.date()).format(DateTimeFormatter.ofPattern("dd.MM.yy"));
        String payerName = getParticipantNameById(expense.payerId());
        BigDecimal convertedAmount = exchange.exchangeTo(expense.date(), expense.amountInEUR(), configManager.getCurrency());
        String amount = convertedAmount.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
        String currency = configManager.getCurrency();
        String purpose = expense.purpose();
        StringBuilder returnersText = new StringBuilder();

        if (expense.returnerIds().size() == currentParticipants.size()) {
            returnersText.append(resources.getString("all_word"));
        } else {
            int count = 0;
            for (long returnerId : expense.returnerIds()) {
                String returnerName = getParticipantNameById(returnerId);
                returnersText.append(returnerName);
                count++;
                if (count == 5 && expense.returnerIds().size() > 5) {
                    returnersText.append(", etc.");
                    break;
                } else if (count < expense.returnerIds().size()) {
                    returnersText.append(", ");
                }
            }
        }

        String expenseText = String.format("%s: %s %s %s %s\n%s %s. (%s)\n",
                date, payerName, resources.getString("paid_word"), amount, currency,
                resources.getString("for_word"), purpose, returnersText.toString().trim());

        return new Label(expenseText);
    }

    /**
     * Converts a Calendar object to a LocalDate object.
     *
     * @param calendar The Calendar object that needs to be converted.
     * @return The corresponding LocalDate object.
     */
    private LocalDate toLocalDate(Calendar calendar) {
        return LocalDate.ofInstant(calendar.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Retrieves the name of a Participant by their ID.
     *
     * @param id The ID of the Participant.
     * @return The name of the Participant, null otherwise.
     */
    private String getParticipantNameById(long id) {
        return currentParticipants.stream()
                .filter(participant -> participant.id() == id)
                .findFirst()
                .map(ParticipantDTO::name)
                .orElse("Unknown Participant");
    }

    /**
     * When the name is not being edited, this method replaces the event name label
     * with a text field, and the edit button is replaced with a save button
     * When the save button is pressed, a check is performed to make sure the new
     * event name is not empty, a PUT request is sent to the backend, the label is updated
     * and the button is renamed back to edit.
     *
     * @param actionEvent of the button press
     */
    public void onEditName(ActionEvent actionEvent) {
        if (isNameEditing) {
            onSave();
        } else {
            editEventNameField.setText(currentEvent.title());
            editEventNameButton.setText(resources.getString("save_label"));
        }

        for (Node child : eventName.getChildren()) {
            child.setVisible(!child.isVisible());
        }
        isNameEditing = !isNameEditing;
    }

    /**
     * Go back to the start screen when the back button is pressed
     *
     * @param actionEvent of the button press
     */
    public void onBack(ActionEvent actionEvent) {
        if (backScreen.equals("start")) {
            mainCtrl.showStartScreen();
        } else if (backScreen.equals("admin")) {
            backScreen = "start";
            mainCtrl.showAdminScreen();
        }
    }

    /**
     * Saves the changed name for the Event.
     */
    private void onSave() {
        try {
            if (editEventNameField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, resources.getString("all_fields_must_be_filled_out_message"), ButtonType.OK);
                alert.showAndWait();
                return;
            }
            EventTitleDTO newTitle = new EventTitleDTO(editEventNameField.getText());
            EventDTO newEvent = server.updateEvent(newTitle, currentEvent.id());
            setEvent(newEvent);
        } catch (Exception exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR, resources.getString("error_saving_new_name"), ButtonType.OK);
            alert.showAndWait();
        } finally {
            editEventNameButton.setText(resources.getString("edit_label"));
        }
    }

    /**
     * Triggered when the flag icon is clicked
     *
     * @param mouseEvent event of the click
     */
    public void onLanguageClick(MouseEvent mouseEvent) {
        mainCtrl.showSettingsScreen((Void t) -> {
            mainCtrl.showEventOverview(currentEvent);
            return Void.TYPE.cast(null);
        });
    }

    /**
     * Triggered when the copy code button is pressed
     *
     * @param actionEvent event of the click
     */
    public void onCopyCode(ActionEvent actionEvent) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(currentEvent.inviteCode());
        clipboard.setContent(content);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, resources.getString("copied_code"), ButtonType.OK);
        alert.showAndWait();
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

        isLight = isLightMode;
    }


    /**
     * Triggered when the statistics button is pressed
     *
     * @param actionEvent event of the click
     */
    public void onStatisticsPress(ActionEvent actionEvent) {
        mainCtrl.showStatisticsPage(currentExpenses, currentTags);
    }
}
