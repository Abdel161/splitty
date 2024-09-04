package client.scenes;

import com.google.inject.Inject;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import client.utils.ConfigManager;
import client.utils.ExchangeManager;

import commons.dtos.ExpenseDTO;
import commons.dtos.TagDTO;

public class StatisticsScreenCtrl {

    private final MainCtrl mainCtrl;
    private final ExchangeManager exchange;
    private final ConfigManager config;
    private ResourceBundle resources;

    @FXML
    private Label totalCostLabel;
    @FXML
    private PieChart tagsPieChart;
    @FXML
    private Pane parent;

    private ObservableList<ExpenseDTO> currentExpenses;
    private ObservableList<TagDTO> currentTags;

    /**
     * Creates a Statistics Screen Controller
     *
     * @param mainCtrl  MainCtrl instance
     * @param exchange  ExchangeManager instance
     * @param config    ConfigManager instance
     * @param resources ResourceBundle instance
     */
    @Inject
    public StatisticsScreenCtrl(MainCtrl mainCtrl, ExchangeManager exchange, ConfigManager config, ResourceBundle resources) {
        this.mainCtrl = mainCtrl;
        this.exchange = exchange;
        this.config = config;
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
     * Sets up the statistics pie chart
     *
     * @param expenses list of expenses in the event
     * @param tags     list of tags in the event
     */
    public void setUpStatistics(ObservableList<ExpenseDTO> expenses, ObservableList<TagDTO> tags) {
        currentExpenses = expenses;
        currentTags = tags;
        createPieChart();

        expenses.addListener((ListChangeListener<ExpenseDTO>) change -> {
            if (change.next()) {
                createPieChart();
            }
        });

        tags.addListener((ListChangeListener<TagDTO>) change -> {
            if (change.next()) {
                createPieChart();
            }
        });
    }

    private void createPieChart() {
        tagsPieChart.getData().clear();
        HashMap<TagDTO, BigDecimal> statistics = new HashMap<>();
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (ExpenseDTO expense : currentExpenses.stream().filter((expense) -> !expense.isDebt()).toList()) {
            BigDecimal convertedAmount = exchange.exchangeTo(expense.date(), expense.amountInEUR(), config.getCurrency());
            totalExpenses = totalExpenses.add(convertedAmount);
            if (expense.tagId() != 0) {
                TagDTO currentTag = currentTags.stream().filter((tag) -> tag.id() == expense.tagId()).findFirst().get();
                statistics.put(currentTag, statistics.containsKey(currentTag) ?
                        statistics.get(currentTag).add(convertedAmount) : convertedAmount);
            }
        }

        for (Map.Entry<TagDTO, BigDecimal> statistic : statistics.entrySet()) {
            PieChart.Data slice = new PieChart.Data(
                    String.format("%s: %s %s (%s%%)", statistic.getKey().name(),
                            statistic.getValue().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString(),
                            config.getCurrency(),
                            statistic.getValue().divide(totalExpenses, 8, RoundingMode.HALF_UP)
                                    .multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
                    ),
                    statistic.getValue().doubleValue());

            tagsPieChart.getData().add(slice);
            String color = statistic.getKey().color().startsWith("#") ?
                    statistic.getKey().color() : String.format("#%08x", Long.parseLong(statistic.getKey().color().substring(2), 16));
            slice.getNode().setStyle("-fx-pie-color: " + color + ";");
        }

        tagsPieChart.setLegendVisible(false);

        totalCostLabel.setText(String.format("%s: %s %s", resources.getString("total_cost_of_all_expenses"),
                totalExpenses.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString(), config.getCurrency()));
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

    /**
     * Redirects back to the event overview screen
     *
     * @param actionEvent of the click
     */
    public void onBack(ActionEvent actionEvent) {
        mainCtrl.showEventOverview();
        tagsPieChart.getData().clear();
    }
}
