
package GUI;

import BUS.EmployeeBUS;
import BUS.RoleBUS;
import DAL.StatisticDAL;
import DTO.StatisticRevenue;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.util.ArrayList;

public class StatisticController {
    @FXML
    private LineChart<String, Number> chart1; // S�+�a ki�+�u r+� r+�ng

    @FXML
    private CategoryAxis x1;

    @FXML
    private NumberAxis y1;

    @FXML
    public void initialize() {
//        Platform.runLater(() -> {
//            ArrayList<StatisticRevenue> list = StatisticDAL.getInstance().getStatisticRevenue(
//                    LocalDate.parse("2025-03-17"),
//                    LocalDate.parse("2025-03-18")
//            );
//            drawRevenueCostProfitChart(list);
//        });
    }


    public void drawRevenueCostProfitChart(ArrayList<StatisticRevenue> data) {
        chart1.setTitle("T�+�ng thu, T�+�ng chi v+� L�+�i nhuߦ�n");

        XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
        revenueSeries.setName("T�+�ng thu");

        XYChart.Series<String, Number> costSeries = new XYChart.Series<>();
        costSeries.setName("T�+�ng chi");

        XYChart.Series<String, Number> profitSeries = new XYChart.Series<>();
        profitSeries.setName("L�+�i nhuߦ�n");

        for (StatisticRevenue stat : data) {
            String time = stat.getTime();
            revenueSeries.getData().add(new XYChart.Data<>(time, stat.getTotalRevenue()));
            costSeries.getData().add(new XYChart.Data<>(time, stat.getTotalCost()));
            profitSeries.getData().add(new XYChart.Data<>(time, stat.getTotalProfit()));
        }

        chart1.getData().clear();
        chart1.getData().addAll(revenueSeries, costSeries, profitSeries);
    }

}