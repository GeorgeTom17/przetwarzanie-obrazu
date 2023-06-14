package com.example.microgimp;

import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class DrawChart {
    public static BarChart<String, Number> showChart(float[] histGray, float[] histRed, float[] histGreen, float[] histBlue) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("grayscale value");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("amount");
        yAxis.setLowerBound(0.0);
        yAxis.setUpperBound(1.0);
        BarChart<String, Number> myHist = new BarChart<>(xAxis, yAxis);
        myHist.setTitle("Histogram");
        myHist.getXAxis().lookup(".axis-label").setStyle("-fx-label-padding: 0 45 0 0;");

        myHist.getData().addAll(
                createSeries("Red", histRed, "-fx-bar-fill: red;"),
                createSeries("Gray", histGray, "-fx-bar-fill: gray;"),
                createSeries("Green", histGreen, "-fx-bar-fill: green;"),
                createSeries("Blue", histBlue, "-fx-bar-fill: blue;")

        );

        return myHist;
    }

    private static XYChart.Series<String, Number> createSeries(String colorName, float[] histColor, String colorStyle) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(colorName);
        for (int j = 0; j < histColor.length; j++) {
            series.getData().add(new XYChart.Data<>(Integer.toString(j), histColor[j]));

            Node seriesNode = series.getNode();
            if (seriesNode != null) {
                series.getData().get(j).getNode().setStyle(colorStyle);
            }
        }

        return series;
    }
}