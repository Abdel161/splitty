package client.scenes;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Stream;

import client.utils.ConfigManager;
import client.utils.EmailManager;
import client.utils.ExchangeManager;
import client.utils.ServerUtils;

import commons.Debt;
import commons.dtos.EventDTO;
import commons.dtos.ExpenseDTO;
import commons.dtos.ParticipantDTO;

public class DebtOverviewCtrl {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private ResourceBundle resources;
    private final EmailManager emailManager;
    private final ExchangeManager exchange;
    private final ConfigManager config;

    @FXML
    private Button filterDebts;
    @FXML
    private Text selectParticipant;
    @FXML
    private Button backButton;
    @FXML
    private VBox debtList;
    @FXML
    private ComboBox<ParticipantDTO> participantDropdown;
    @FXML
    private Pane parent;

    private EventDTO currentEvent;
    private ObservableList<ParticipantDTO> currentParticipants;
    private List<Debt> currentDebts;

    /**
     * Creates a DebtOverviewCtrl
     *
     * @param serverUtils  server utilities
     * @param mainCtrl     main controller
     * @param resources    ResourceBundle resources.
     * @param emailManager EmailManager instance
     * @param exchange     ExchangeManager instance
     * @param config       ConfigManager instance
     */
    @Inject
    public DebtOverviewCtrl(ServerUtils serverUtils, MainCtrl mainCtrl, ResourceBundle resources,
                            EmailManager emailManager, ExchangeManager exchange, ConfigManager config) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;
        this.resources = resources;
        this.emailManager = emailManager;
        this.exchange = exchange;
        this.config = config;
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

    @FXML
    private void initialize() {
        participantDropdown.setCellFactory(new Callback<>() {
            @Override
            public ListCell<ParticipantDTO> call(ListView<ParticipantDTO> param) {
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
    }

    /**
     * Go back to the event overview
     *
     * @param event event that happens
     */
    public void onBack(ActionEvent event) {
        mainCtrl.showEventOverview();
    }

    /**
     * sets event and all details needed for debt screen
     *
     * @param event event to be set
     */
    public void setEvent(EventDTO event) {
        try {
            filterDebts.setText(resources.getString("filter_label"));
            currentEvent = event;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, resources.getString("error_getting_event_details"), ButtonType.OK);
            alert.showAndWait();
        }
    }

    /**
     * Sets the available participants for the dropdown.
     *
     * @param participants Observable list of participants.
     */
    public void setParticipants(ObservableList<ParticipantDTO> participants) {
        currentParticipants = FXCollections.observableArrayList(participants);
        currentParticipants.addFirst(new ParticipantDTO(0L, resources.getString("all_debts"), null, null, null));
        participantDropdown.setItems(currentParticipants);
        participantDropdown.getSelectionModel().select(0);

        participants.addListener((ListChangeListener<ParticipantDTO>) change -> {
            if (change.next()) {
                currentParticipants.setAll(participants);
                currentParticipants.addFirst(new ParticipantDTO(0L, resources.getString("all_debts"), null, null, null));
                participantDropdown.getSelectionModel().select(0);
            }
        });
    }

    /**
     * Sets the list of debts.
     *
     * @param debts Observable list of debts.
     */
    public void setDebts(ObservableList<Debt> debts) {
        currentDebts = debts;
        populateDebts(Optional.empty());

        debts.addListener((ListChangeListener<Debt>) change -> {
            if (change.next()) {
                filterDebts(new ActionEvent());
            }
        });
    }

    /**
     * Gets the selected participant in the dropdown menu
     *
     * @param event event that happens
     */
    public void filterDebts(ActionEvent event) {
        ParticipantDTO selectedParticipant = participantDropdown.getSelectionModel().getSelectedItem();
        populateDebts(selectedParticipant.name().equals(resources.getString("all_debts")) ? Optional.empty() : Optional.of(selectedParticipant));
    }

    private SVGPath getArrowRightSvg() {
        SVGPath arrowShape = new SVGPath();
        arrowShape.setContent("M7.33 24l-2.83-2.829 9.339-9.175-9.339-9.167 2.83-2.829 12.17 11.996z");
        arrowShape.setFill(Color.BLACK);
        return arrowShape;
    }

    private SVGPath getArrowDownSvg() {
        SVGPath arrowShape = new SVGPath();
        arrowShape.setContent("M0 7.33l2.829-2.83 9.175 9.339 9.167-9.339 2.829 2.83-11.996 12.17z");
        arrowShape.setFill(Color.BLACK);
        return arrowShape;
    }

    private Button createDetailsButton() {
        Button showDetails = new Button();
        showDetails.setGraphic(getArrowRightSvg());
        showDetails.setStyle("-fx-background-color: transparent");
        return showDetails;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void populateDebts(Optional<ParticipantDTO> filterParticipant) {
        debtList.getChildren().clear();

        List<Debt> filteredDebts = getFilteredDebts(filterParticipant);
        for (Debt debt : filteredDebts) {
            HBox detailsLine = getDetailsBox(debt);
            HBox line = getDebtBox();
            addDetailAndDebt(line, detailsLine);

            Button markReceivedButton = new Button(resources.getString("mark_received"));
            markReceivedButton.setMinWidth(150);
            markReceivedButton.setPadding(new Insets(5, 5, 5, 5));
            markReceivedButton.setOnAction((ActionEvent ev) -> {
                settleDebt(debt);
            });

            Button sendReminderButton = new Button(resources.getString("send_reminder"));
            sendReminderButton.setMinWidth(150);
            sendReminderButton.setPadding(new Insets(5, 5, 5, 5));
            sendReminderButton.setOnAction((ActionEvent ev) -> {
                onSendReminder(debt);
            });

            Button showDetailsButton = createDetailsButton();
            showDetailsButton.setOnAction((ActionEvent event) -> {
                boolean isVisible = detailsLine.isVisible();
                detailsLine.setVisible(!isVisible);
                detailsLine.setManaged(!isVisible);
                if (!isVisible) {
                    VBox.setMargin(line, new Insets(0, 0, 0, 0));
                    showDetailsButton.setGraphic(getArrowDownSvg());
                    showDetailsButton.setStyle("-fx-background-color: transparent");
                    HBox.setMargin(showDetailsButton, new Insets(0, 0, 0, 0));
                    HBox.setMargin(markReceivedButton, new Insets(0, 0, 0, 15));
                    HBox.setMargin(sendReminderButton, new Insets(0, 0, 0, 15));
                } else {
                    VBox.setMargin(line, new Insets(0, 0, 12, 0));
                    showDetailsButton.setGraphic(getArrowRightSvg());
                    showDetailsButton.setStyle("-fx-background-color: transparent");
                    HBox.setMargin(showDetailsButton, new Insets(0, 4, 0, 0));
                    HBox.setMargin(markReceivedButton, new Insets(0, 0, 0, 20));
                    HBox.setMargin(sendReminderButton, new Insets(0, 0, 0, 15));
                }
            });

            BigDecimal amount = exchange.exchangeTo(Calendar.getInstance(), debt.getAmountInEUR(), config.getCurrency());
            Text debtText = new Text(getParticipantById(debt.getFrom()).name() + " " + resources.getString("owes") + " "
                    + getParticipantById(debt.getTo()).name() +
                    ": " + amount.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + " " +
                    config.getCurrency());
            debtText.setFont(new Font(15));

            line.getChildren().add(showDetailsButton);
            line.getChildren().add(debtText);
            line.getChildren().add(markReceivedButton);
            line.getChildren().add(sendReminderButton);

            HBox.setMargin(showDetailsButton, new Insets(0, 4, 0, 0));
            HBox.setMargin(markReceivedButton, new Insets(0, 0, 0, 20));
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private List<Debt> getFilteredDebts(Optional<ParticipantDTO> filterParticipant) {
        Stream<Debt> debtsStream = currentDebts.stream();

        if (filterParticipant.isPresent()) {
            long participantId = filterParticipant.get().id();
            debtsStream = debtsStream.filter(debt -> debt.getFrom() == participantId);
        }

        return debtsStream.toList();
    }

    private void onSendReminder(Debt debt) {
        ParticipantDTO from = getParticipantById(debt.getFrom());
        Text text = (Text) getDetailsBox(debt).getChildren().getFirst();

        if (!emailManager.checkCredentials()) {
            return;
        }

        if (from.email() == null || from.email().isEmpty()) {
            Alert errorAlert = new Alert(Alert.AlertType.INFORMATION, resources.getString("returner_email_not_set"), ButtonType.OK);
            errorAlert.setTitle(resources.getString("confirmation_title"));
            errorAlert.showAndWait();
            return;
        }

        emailManager.sendEmail(from.email(), "Splitty", text.getText());
    }

    private void settleDebt(Debt debt) {
        Calendar date = GregorianCalendar.from(LocalDate.now().atStartOfDay(ZoneOffset.UTC));
        ExpenseDTO payment = new ExpenseDTO(debt.getAmountInEUR(), config.getCurrency(), date, "Debt Settlement",
                Calendar.getInstance(), Calendar.getInstance(), 0, debt.getFrom(), Set.of(debt.getTo()), 0, true);
        serverUtils.addExpense(currentEvent.id(), payment);
    }

    private HBox getDetailsBox(Debt debt) {
        ParticipantDTO to = getParticipantById(debt.getTo());

        HBox detailsLine = new HBox();
        Text detailsText = new Text();
        detailsText.setText(resources.getString("send_money_to") + " " + to.name() + "\n");

        String text;
        if (to.iban() != null && to.bic() != null) {
            text = resources.getString("bank_info") + "\n" +
                    resources.getString("account_holder") + " " + to.name() + "\nIBAN: " + to.iban() + "\nBIC: " + to.bic() + "\n\n";
            detailsText.setText(detailsText.getText() + text);
        } else {
            text = resources.getString("no_bank_info") + "\n\n";
            detailsText.setText(detailsText.getText() + text);
        }
        if (to.email() != null) {
            text = resources.getString("email_conf") + " " + to.email();
        } else {
            text = resources.getString("no_email_conf");
        }
        detailsText.setText(detailsText.getText() + text);

        detailsLine.getChildren().add(detailsText);
        HBox.setMargin(detailsText, new Insets(0, 0, 0, 25));
        detailsLine.setVisible(false);
        detailsLine.setManaged(false);

        return detailsLine;
    }

    private HBox getDebtBox() {
        HBox line = new HBox();
        line.setPrefWidth(debtList.getWidth());
        line.setPadding(new Insets(3, 5, 0, 5));
        return line;
    }

    private void addDetailAndDebt(HBox line, HBox detailsLine) {
        debtList.getChildren().add(line);
        VBox.setMargin(line, new Insets(0, 0, 12, 0));
        debtList.getChildren().add(detailsLine);
        VBox.setMargin(detailsLine, new Insets(0, 0, 12, 0));
    }

    private ParticipantDTO getParticipantById(long id) {
        for (ParticipantDTO participant : currentParticipants) {
            if (participant.id() == id)
                return participant;
        }

        return null;
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
