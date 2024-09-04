package client.scenes;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.ZoneId;

import client.utils.ExchangeManager;
import client.utils.ServerUtils;

import commons.dtos.EventDTO;
import commons.dtos.ExpenseDTO;
import commons.dtos.ParticipantDTO;
import commons.dtos.TagDTO;

public class AddExpenseCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final ExchangeManager exchange;
    private ResourceBundle resources;

    @FXML
    private Label title;
    @FXML
    private GridPane returnersGrid;
    @FXML
    private ScrollPane returnersScrollPane;
    @FXML
    private Button saveExpenseButton;
    @FXML
    private Button editTagsButton;
    @FXML
    private ComboBox<ParticipantDTO> participantDropdown;
    @FXML
    private TextField purposeTextField;
    @FXML
    private TextField amountTextField;
    @FXML
    private ComboBox<String> currencyDropdown;
    @FXML
    private DatePicker dateDatePicker;
    @FXML
    private CheckBox equalCheckBox;
    @FXML
    private CheckBox someCheckBox;
    @FXML
    private ComboBox<TagDTO> tagDropdown;
    @FXML
    private Button removeTagButton;
    @FXML
    private Button addTagsButton;
    @FXML
    private VBox parent;

    private EventDTO currentEvent;
    private ExpenseDTO currentExpense;
    private ObservableList<ParticipantDTO> currentParticipants;
    private ObservableList<ExpenseDTO> currentExpenses;
    private ObservableList<TagDTO> currentTags;
    private boolean isEditingExpense;

    /**
     * Constructs an AddExpenseCtrl instance.
     *
     * @param server    ServerUtils instance.
     * @param mainCtrl  MainCtrl instance.
     * @param exchange  ExchangeManager instance.
     * @param resources ResourceBundle resources.
     */
    @Inject
    public AddExpenseCtrl(ServerUtils server, MainCtrl mainCtrl, ExchangeManager exchange, ResourceBundle resources) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.exchange = exchange;
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
     * It displays the add new tag page
     *
     * @param event event to be set
     */
    public void openAddTagPage(ActionEvent event) {
        mainCtrl.showAddTagScreen(currentEvent, currentExpense == null);
    }

    /**
     * It displays the edit tags page
     *
     * @param event event to be set
     */
    public void openEditTagsPage(ActionEvent event) {
        mainCtrl.showEditTagScreen(currentEvent, currentExpense == null, currentTags, currentExpenses);
    }

    /**
     * Initializes some components of the addExpense screen.
     */
    @FXML
    private void initialize() {
        saveExpenseButton.setText(resources.getString("add_label"));
        title.setText(resources.getString("add_expense"));
        ObservableList<String> currencies = FXCollections.observableArrayList("EUR", "USD", "CHF", "GBP");
        currencyDropdown.setItems(currencies);

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

        tagDropdown.setCellFactory(new Callback<>() {
            @Override
            public ListCell<TagDTO> call(ListView<TagDTO> tags) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(TagDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? "" : item.name());
                    }
                };
            }
        });

        tagDropdown.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(TagDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.name());
            }
        });

        returnersGrid.setVisible(false);
        returnersGrid.setManaged(false);
        returnersScrollPane.setVisible(false);
        returnersScrollPane.setManaged(false);
    }

    /**
     * Sets the Event to which the Expenses belong.
     *
     * @param event The event to set as the current event.
     */
    public void setEvent(EventDTO event) {
        currentEvent = event;
    }

    /**
     * Sets the list of participants and updates the dropdown with their names.
     *
     * @param participants The list of ParticipantDto representing the involved participants.
     */
    public void setParticipants(ObservableList<ParticipantDTO> participants) {
        currentParticipants = FXCollections.observableArrayList(participants);
        setupReturnersAndCheckboxes(Set.of());
        participantDropdown.setItems(currentParticipants);

        participants.addListener((ListChangeListener<ParticipantDTO>) change -> {
            if (change.next()) {
                long payerId = participantDropdown.getSelectionModel().getSelectedItem() != null ?
                        participantDropdown.getSelectionModel().getSelectedItem().id() : 0;
                currentParticipants.setAll(participants);
                if (payerId != 0) {
                    ParticipantDTO payer = getParticipantById(payerId);
                    if (payer != null) {
                        participantDropdown.setValue(payer);
                    }
                }

                setupReturnersAndCheckboxes(retrieveReturners());
            }
        });
    }

    /**
     * Sets the list of Expenses
     *
     * @param expenses The list of ExpenseDTO for the current event.
     */
    public void setExpenses(ObservableList<ExpenseDTO> expenses) {
        currentExpenses = expenses;
    }

    /**
     * Sets the list of Tags and updates the dropdown with their names.
     *
     * @param tags The list of TagDTO representing the created tags for the current event.
     */
    public void setTags(ObservableList<TagDTO> tags) {
        currentTags = FXCollections.observableArrayList(tags);
        tagDropdown.setItems(currentTags);

        tags.addListener((ListChangeListener<TagDTO>) change -> {
            if (change.next()) {
                long tagId = tagDropdown.getSelectionModel().getSelectedItem() != null ? tagDropdown.getSelectionModel().getSelectedItem().id() : 0;
                currentTags.setAll(tags);
                if (tagId != 0) {
                    TagDTO tag = getTagById(tagId);
                    if (tag != null) {
                        tagDropdown.setValue(tag);
                    }
                }
            }
        });
    }

    /**
     * Sets up the UI components based on the details of the existing Expense.
     *
     * @param expense The expense to be edited.
     */
    public void setExpense(ExpenseDTO expense) {
        title.setText(resources.getString("edit_expense"));
        saveExpenseButton.setText(resources.getString("edit_label"));
        currentExpense = expense;

        ParticipantDTO payer = getParticipantById(expense.payerId());
        if (payer != null) {
            participantDropdown.setValue(payer);
        }

        purposeTextField.setText(expense.purpose());
        dateDatePicker.setValue(toLocalDate(expense.date()));

        BigDecimal amount = exchange.exchangeTo(expense.date(), expense.amountInEUR(), expense.currency());
        amountTextField.setText(amount.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
        currencyDropdown.setValue(expense.currency());

        Set<Long> allParticipantIds = currentParticipants.stream().map(ParticipantDTO::id).collect(Collectors.toSet());
        Set<Long> returnerIds = expense.returnerIds();

        boolean isSomeSelected = !returnerIds.containsAll(allParticipantIds) || !allParticipantIds.containsAll(returnerIds);
        equalCheckBox.setSelected(!isSomeSelected);
        someCheckBox.setSelected(isSomeSelected);

        returnersGrid.setVisible(isSomeSelected);
        returnersGrid.setManaged(isSomeSelected);
        returnersScrollPane.setVisible(isSomeSelected);
        returnersScrollPane.setManaged(isSomeSelected);

        if (isSomeSelected) {
            setupReturnersAndCheckboxes(returnerIds);
        }

        if (expense.tagId() != 0) {
            TagDTO tag = getTagById(expense.tagId());
            if (tag != null) {
                tagDropdown.setValue(tag);
            }
        }
    }

    /**
     * Sets up checkboxes for participants based on the provided returnerIDs of the current Expense
     * and populates the returnersVBox.
     *
     * @param returnerIds A set containing the IDs of the participants who need to pay for the Expense.
     */
    private void setupReturnersAndCheckboxes(Set<Long> returnerIds) {
        returnersGrid.getChildren().clear();
        int row = 0;
        for (ParticipantDTO participant : currentParticipants) {
            CheckBox checkBox = new CheckBox(participant.name());
            checkBox.setUserData(participant);
            checkBox.setId(String.valueOf(participant.id()));
            checkBox.setSelected(returnerIds.contains(participant.id()));
            returnersGrid.add(checkBox, 0, row++);
        }
    }

    /**
     * Creates an Expense based on the input fields in the UI.
     */
    public void onSave() {
        if (!checkInputValidity()) {
            return;
        }

        ParticipantDTO selectedParticipant = participantDropdown.getValue();
        long payerId = selectedParticipant.id();
        LocalDate localDate = dateDatePicker.getValue();
        ZonedDateTime zdt = localDate.atStartOfDay(ZoneOffset.UTC);
        Calendar date = GregorianCalendar.from(zdt);
        String currency = currencyDropdown.getValue();
        BigDecimal amount = new BigDecimal(amountTextField.getText()).setScale(8, RoundingMode.HALF_UP);
        BigDecimal amountInEUR = exchange.exchangeFrom(date, amount, currency);
        String purpose = purposeTextField.getText();
        Set<Long> returnerIds = retrieveReturners();
        long tagId = tagDropdown.getSelectionModel().getSelectedItem() != null ? tagDropdown.getSelectionModel().getSelectedItem().id() : 0;

        ExpenseDTO expenseDto;
        if (currentExpense == null) {
            expenseDto = new ExpenseDTO(amountInEUR, currency, date, purpose, date, date, 0, payerId, returnerIds, tagId, false);
            server.addExpense(currentEvent.id(), expenseDto);
            showSuccess(resources.getString("expense_success_create_message"));
        } else {
            expenseDto = new ExpenseDTO(amountInEUR, currency, date, purpose, Calendar.getInstance(),
                    currentExpense.createdOn(), currentExpense.id(), payerId, returnerIds, tagId, false);
            server.updateExpense(currentEvent.id(), currentExpense.id(), expenseDto);
            showSuccess(resources.getString("expense_success_update_message"));
        }

        cancel();
    }

    /**
     * Cancels the operation, clears the fields and shows the event overview.
     */
    public void cancel() {
        purposeTextField.clear();
        amountTextField.clear();
        currencyDropdown.setValue(null);
        dateDatePicker.setValue(null);
        participantDropdown.setValue(null);
        tagDropdown.setValue(null);
        equalCheckBox.setSelected(false);
        someCheckBox.setSelected(false);
        returnersGrid.getChildren().clear();
        returnersGrid.setVisible(false);
        returnersGrid.setManaged(false);
        returnersScrollPane.setVisible(false);
        returnersScrollPane.setManaged(false);
        currentExpense = null;

        mainCtrl.showEventOverview();
        saveExpenseButton.setText(resources.getString("add_label"));
        title.setText(resources.getString("add_expense"));
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
     * Retrieves the participant by its ID.
     *
     * @param id The ID of the participant.
     * @return The found participant, null otherwise.
     */
    private ParticipantDTO getParticipantById(long id) {
        for (ParticipantDTO participant : currentParticipants) {
            if (participant.id() == id) {
                return participant;
            }
        }
        return null;
    }

    /**
     * Retrieves the tag by its ID.
     *
     * @param id The ID of the tag.
     * @return The found tag, null otherwise.
     */
    private TagDTO getTagById(long id) {
        for (TagDTO tag : currentTags) {
            if (tag.id() == id) {
                return tag;
            }
        }
        return null;
    }

    /**
     * Returns a list containing the participants who need to pay back for the Expense.
     *
     * @return A list containing the IDs of the returners.
     */
    private Set<Long> retrieveReturners() {
        Set<Long> returners = new HashSet<>();
        if (equalCheckBox.isSelected()) {
            for (ParticipantDTO participant : currentParticipants) {
                returners.add(participant.id());
            }
        } else if (someCheckBox.isSelected()) {
            for (Node node : returnersGrid.getChildren()) {
                if (node instanceof CheckBox checkBox) {
                    if (checkBox.isSelected()) {
                        ParticipantDTO participant = (ParticipantDTO) checkBox.getUserData();
                        returners.add(participant.id());
                    }
                }
            }
        }

        return returners;
    }

    /**
     * Handles some UI components of the screen
     * If the equalCheckbox is selected, someCheckbox is deselected and the returners list becomes invisible.
     * If the someCheckBox is selected, the opposite happens.
     */
    @FXML
    private void handleCheckBoxAction(ActionEvent event) {
        if ((event.getSource() == equalCheckBox && equalCheckBox.isSelected()) || !someCheckBox.isSelected()) {
            someCheckBox.setSelected(false);
            returnersScrollPane.setVisible(false);
            returnersScrollPane.setManaged(false);
            returnersGrid.setVisible(false);
            returnersGrid.setManaged(false);
        } else if (event.getSource() == someCheckBox && someCheckBox.isSelected()) {
            equalCheckBox.setSelected(false);
            returnersScrollPane.setVisible(true);
            returnersScrollPane.setManaged(true);
            returnersGrid.setVisible(true);
            returnersGrid.setManaged(true);
        }
    }

    /**
     * Clears the selection of the tags (it sets the dropdown to NO TAG)
     *
     * @param actionEvent action made
     */
    public void clearTagSelection(ActionEvent actionEvent) {
        tagDropdown.valueProperty().set(null);
    }

    /**
     * Checks the validity of the input by the participant.
     * The method checks whether the format of the price is accurate or not
     * and whether all the fields have been filled out or not.
     *
     * @return true if the input is valid, false otherwise.
     */
    private boolean checkInputValidity() {
        if (amountTextField.getText().isBlank() ||
                currencyDropdown.getValue() == null ||
                dateDatePicker.getValue() == null ||
                purposeTextField.getText().isBlank() ||
                participantDropdown.getValue() == null ||
                (!equalCheckBox.isSelected() && !someCheckBox.isSelected())) {
            showError(resources.getString("all_fields_must_be_filled_out_message"));
            return false;
        }

        LocalDate localDate = dateDatePicker.getValue();
        Calendar date = GregorianCalendar.from(localDate.atStartOfDay(ZoneId.systemDefault()));

        if (date.compareTo(Calendar.getInstance()) > 0) {
            showError(resources.getString("invalid_expense_date"));
            return false;
        }

        try {
            BigDecimal amount = new BigDecimal(amountTextField.getText());
            BigDecimal amountInEUR = exchange.exchangeFrom(date, amount, currencyDropdown.getValue());
            if (amount.scale() > 2) throw new NumberFormatException();
            if (amountInEUR.precision() > 18) throw new NumberFormatException();

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showError(resources.getString("amount_positive_message"));
                return false;
            }
        } catch (NumberFormatException e) {
            showError(resources.getString("invalid_format"));
            return false;
        }

        if (someCheckBox.isSelected()) {
            boolean atLeastOneSelected = returnersGrid.getChildren().stream()
                    .filter(node -> node instanceof CheckBox)
                    .anyMatch(node -> ((CheckBox) node).isSelected());

            if (!atLeastOneSelected) {
                showError(resources.getString("select_at_least_one_returner_message"));
                return false;
            }
        }

        return true;
    }

    /**
     * Displays an error message to the user in an alert dialog.
     *
     * @param message The message that is displayed.
     */
    private void showError(String message) {
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
}
