package com.example.microgimp;

import javafx.application.Application;
import javafx.scene.chart.*;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;

import java.security.KeyStore;

public class DrawChart extends Application {
    @Override
    public void start(Stage stage) throws Exception {
    }

    public static void showChart(Stage newStage, float[] hist){
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("grayscale value");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("amount");
        yAxis.setLowerBound(0.0);
        yAxis.setUpperBound(1.0);
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Histogram");
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        for (int j = 0; j < hist.length; j ++) {
            series1.getData().add(new XYChart.Data<>(Integer.toString(j), hist[j]));
        }
        barChart.getData().add(0, series1);
        Group root = new Group(barChart);
        Scene scene = new Scene(root, 600, 400);
        newStage.setTitle("Bar Chart");
        newStage.setScene(scene);
        newStage.show();
    }
}
