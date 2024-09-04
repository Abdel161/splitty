package client.scenes;

import com.google.inject.Inject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.util.Optional;
import java.util.ResourceBundle;

import client.utils.ServerUtils;

import commons.dtos.EventDTO;
import commons.dtos.ExpenseDTO;
import commons.dtos.TagDTO;

public class EditTagCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private ResourceBundle resources;

    @FXML
    private Pane parent;
    @FXML
    private Button addButton;
    @FXML
    private Button backButton;
    @FXML
    private Button deleteButton;
    @FXML
    private ColorPicker tagColor;
    @FXML
    private TextField tagName;
    @FXML
    private ComboBox<TagDTO> tagDropdown;

    private EventDTO currentEvent;
    private ObservableList<TagDTO> currentTags;
    private ObservableList<ExpenseDTO> currentExpenses;
    private boolean isEditingExpense;

    /**
     * Constructs an EditTagCtrl instance.
     *
     * @param server    ServerUtils instance.
     * @param mainCtrl  MainCtrl instance.
     * @param resources ResourceBundle resources.
     */
    @Inject
    public EditTagCtrl(ServerUtils server, MainCtrl mainCtrl, ResourceBundle resources) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.resources = resources;
    }

    /**
     * Initializes some components of the editTags screen.
     */
    @FXML
    private void initialize() {
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

        tagDropdown.valueProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends TagDTO> observable, TagDTO oldValue, TagDTO newValue) {
                if (newValue != null) {
                    if (newValue.id() != 0) {
                        tagName.setText(newValue.name());
                        tagColor.setValue(Color.web(newValue.color()));
                    } else clearFields();
                }
            }
        });
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
     * Sets the Event to which the Expenses belong.
     *
     * @param event            The event to set as the current event.
     * @param isEditingExpense if the previous screen was edit expense
     */
    public void setEvent(EventDTO event, boolean isEditingExpense) {
        currentEvent = event;
        this.isEditingExpense = isEditingExpense;
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
     * goes back to the previous screen and keeps the info on that screen
     *
     * @param event action
     */
    public void goBack(ActionEvent event) {
        mainCtrl.returnToAddExpenseScreen(isEditingExpense);
        clearFields();
    }

    private void clearFields() {
        tagName.clear();
        tagColor.setValue(Color.WHITE);
        tagDropdown.valueProperty().set(null);
    }

    /**
     * Sets the list of Tags and updates the dropdown with their names.
     *
     * @param tags The list of TagDTO representing the created tags for the current event.
     */
    public void setTags(ObservableList<TagDTO> tags) {
        currentTags = tags;
        tagDropdown.setItems(currentTags);
    }

    /**
     * Updates the selected tag in the DB
     */
    public void saveTag() {
        if (tagName.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, resources.getString("select_tag_please"), ButtonType.OK);
            alert.setHeaderText(resources.getString("no_select_tag"));
            alert.showAndWait();
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, resources.getString("save_tag_confirmation_message"), ButtonType.OK);
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            TagDTO tagDTO = new TagDTO(tagName.getText(), tagColor.getValue().toString(), tagDropdown.getValue().id());
            server.updateTag(currentEvent.id(), tagDTO);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, resources.getString("update_tag_successful"), ButtonType.OK);
            alert.showAndWait();
            clearFields();
        }
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
     * Deletes the selected Tag from
     */
    public void deleteTag() {
        if (tagName.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, resources.getString("select_tag_please"), ButtonType.OK);
            alert.setHeaderText(resources.getString("no_select_tag"));
            alert.showAndWait();
            return;
        }

        if (currentExpenses.stream().anyMatch((expense) -> expense.tagId() == tagDropdown.getValue().id())) {
            Alert alert = new Alert(Alert.AlertType.WARNING, resources.getString("delete_tag_warning"), ButtonType.OK);
            alert.setHeaderText(resources.getString("error"));
            alert.showAndWait();
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, resources.getString("delete_tag_confirmation_message"), ButtonType.OK);
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            server.deleteTag(currentEvent.id(), tagDropdown.getValue().id());

            Alert alert = new Alert(Alert.AlertType.INFORMATION, resources.getString("delete_tag_successful"), ButtonType.OK);
            alert.showAndWait();
            clearFields();
        }
    }
}
