package com.example.microgimp;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;


public class DisplayController implements Initializable{

    public Accordion myAccordion;
    public CategoryAxis histXAxis;
    public NumberAxis histYAxis;
    @FXML
    private Slider desatSlider;
    @FXML
    private Label desatSliderVal;

    @FXML
    private Slider gaussSlider;
    @FXML
    private Label gaussSliderVal;

    @FXML
    private Slider brightnessSlider;
    @FXML
    private Label brightnessSliderVal;


    @FXML
    private ComboBox<String> contrastType;
    @FXML
    private Slider contrastSlider;
    @FXML
    private Label contrastSliderVal;

    @FXML
    private Button negativeButton;

    @FXML
    private Button sumButton;
    @FXML
    private Button subtractionButton;
    @FXML
    private Button productButton;
    @FXML
    private ImageView displayedImage;

    @FXML
    private Button applyChanges;
    @FXML
    private Button loadImgButton;
    @FXML
    private Button saveImgButton;

    @FXML
    private VBox histContainer;
    @FXML
    public BarChart<String, Number> myHist;
    @FXML
    private CheckBox redHistCheck;
    @FXML
    private CheckBox greenHistCheck;
    @FXML
    private CheckBox blueHistCheck;
    @FXML
    private CheckBox grayHistCheck;
    @FXML
    private Button revertChanges;
    @FXML
    private Button stretchingButton;
    @FXML
    private Button equalizationButton;
    @FXML
    private Button stdBlurButton;
    @FXML
    private Button gaussianBlurButton;
    @FXML
    private Button sharpenImageButton;
    @FXML
    private Button sobelButton;
    @FXML
    private Button prewittButton;
    @FXML
    private Button robertsButton;
    @FXML
    private Button laplaceButton;
    @FXML
    private Button laplacianOfGaussianFilterButton;

    public float[][] originalBits = new float[0][0];
    public float[][] bits = new float[0][0];
    public float[][] bits2 = new float[0][0];
    DropShadow dropShadow = new DropShadow();

    public float[] histGray = new float[0];
    public float[] histRed = new float[0];
    public float[] histGreen = new float[0];
    public float[] histBlue = new float[0];


    public DisplayController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Button[] buttons = {loadImgButton, saveImgButton, revertChanges,
                negativeButton, sumButton, subtractionButton, productButton,
                stdBlurButton, gaussianBlurButton, sharpenImageButton,
                sobelButton, prewittButton, robertsButton, laplaceButton, laplacianOfGaussianFilterButton,
                stretchingButton, equalizationButton};

        for (Button button : buttons) {
            button.setEffect(dropShadow);
        }

        negativeButton.setEffect(dropShadow);

        desatSlider.setValue(0.5);
        desatSliderVal.textProperty().bind(Bindings.format("%.2f", desatSlider.valueProperty().subtract(0.01)));
        brightnessSlider.setValue(0.0);
        brightnessSliderVal.textProperty().bind(Bindings.format("%.2f", brightnessSlider.valueProperty()));
        contrastType.setValue("");
        contrastSlider.setValue(1.0);
        contrastSliderVal.textProperty().bind(Bindings.format("%.2f", contrastSlider.valueProperty()));
        gaussSlider.setValue(1);
        gaussSliderVal.textProperty().bind(Bindings.format("%.0f", gaussSlider.valueProperty()));


        desatSlider.valueProperty().addListener((observable) -> desaturation());

        brightnessSlider.valueProperty().addListener((observable) -> brightness());

        contrastSlider.valueProperty().addListener((observable) -> contrast());

        contrastType.getItems().removeAll(contrastType.getItems());
        contrastType.getItems().addAll("brak", "logarytmiczny", "potęgowy", "liniowy");
        contrastType.getSelectionModel().select("brak");

    }

    public void rollbackChanges(ActionEvent event) {
        bits = originalBits;
        update(event);
    }

    public void loadMainFile(ActionEvent event) {
        bits = ReadFile.main();
        originalBits = bits;
        update(event);
    }

    public void saveFile(){
        SaveFile.save(bits);
    }

    public void update(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        WritableImage wImage;
        wImage = new WritableImage(bits[0].length/3, bits.length);
        PixelWriter writer = wImage.getPixelWriter();
        ImageOperations.show(bits.length, bits[0].length, bits, writer);
        displayedImage.setX(10);
        displayedImage.setY(10);
        displayedImage.setImage(wImage);
        displayedImage.setPreserveRatio(true);
        updateHist(event);
        stage.show();
    }

    public void updateHist(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        histGray = new float[0];
        histRed = new float[0];
        histGreen = new float[0];
        histBlue = new float[0];
        histogramCheckboxSetup(event);
        if (grayHistCheck.isSelected()) {
            histGray = ImageOperations.getHistVals(bits.length, bits[0].length, bits, "gray");
        }
        if (redHistCheck.isSelected()) {
            histRed = ImageOperations.getHistVals(bits.length, bits[0].length, bits, "red");
        }
        if (greenHistCheck.isSelected()) {
            histGreen = ImageOperations.getHistVals(bits.length, bits[0].length, bits, "green");
        }
        if (blueHistCheck.isSelected()) {
            histBlue = ImageOperations.getHistVals(bits.length, bits[0].length, bits, "blue");
        }

        myHist = DrawChart.showChart(histGray, histRed, histGreen, histBlue);
        histContainer.getChildren().add(myHist);
        histContainer.getChildren().remove(0);
        stage.show();
    }

    private void histogramCheckboxSetup(ActionEvent event) {
        if (((Node) event.getSource()).getId().equals("greyHistCheck")){
            redHistCheck.setSelected(false);
            greenHistCheck.setSelected(false);
            blueHistCheck.setSelected(false);
        }
        if (((Node) event.getSource()).getId().equals("redHistCheck")){
            grayHistCheck.setSelected(false);
            greenHistCheck.setSelected(false);
            blueHistCheck.setSelected(false);
        }
        if (((Node) event.getSource()).getId().equals("greenHistCheck")){
            grayHistCheck.setSelected(false);
            redHistCheck.setSelected(false);
            blueHistCheck.setSelected(false);
        }
        if (((Node) event.getSource()).getId().equals("blueHistCheck")){
            grayHistCheck.setSelected(false);
            redHistCheck.setSelected(false);
            greenHistCheck.setSelected(false);
        }
    }

    public void stretchHistogram(ActionEvent event) {
        if (grayHistCheck.isSelected()) {
            bits = HistogramOperations.stretchHist(bits.length, bits[0].length, bits, "gray");
        }
        if (redHistCheck.isSelected()) {
            bits = HistogramOperations.stretchHist(bits.length, bits[0].length, bits, "red");
        }
        if (greenHistCheck.isSelected()) {
            bits = HistogramOperations.stretchHist(bits.length, bits[0].length, bits, "green");
        }
        if (blueHistCheck.isSelected()) {
            bits = HistogramOperations.stretchHist(bits.length, bits[0].length, bits, "blue");
        }
        update(event);

    }

    public void equalizeHistogram(ActionEvent event) {
        if (grayHistCheck.isSelected()) {
            bits = HistogramOperations.equalizeHist(bits.length, bits[0].length, bits, "gray");
        }
        if (redHistCheck.isSelected()) {
            bits = HistogramOperations.equalizeHist(bits.length, bits[0].length, bits, "red");
        }
        if (greenHistCheck.isSelected()) {
            bits = HistogramOperations.equalizeHist(bits.length, bits[0].length, bits, "green");
        }
        if (blueHistCheck.isSelected()) {
            bits = HistogramOperations.equalizeHist(bits.length, bits[0].length, bits, "blue");
        }
        update(event);

    }

    public void negative(ActionEvent event) {
        bits = ImageOperations.negative(bits.length, bits[0].length, bits);
        update(event);
    }

    public void desaturation() {
        bits = ConverterRGB.RGBtoHSL(bits.length, bits[0].length, bits);
        bits = ImageOperations.desaturation(bits, (float) desatSlider.getValue());
        bits = ConverterRGB.HSLtoRGB(bits.length, bits[0].length, bits);
        applyChanges.fire();
    }

    public void brightness(){
        bits = ImageOperations.brightness(originalBits, (float) brightnessSlider.getValue());
        applyChanges.fire();
    }


    public void contrast(){
        contrastType.getValue();
        bits = ConverterRGB.RGBtoHSL(bits.length, bits[0].length, bits);
        if (contrastType.getValue().equals("logarytmiczny")) {
            bits = ImageOperations.contrast(bits, (float) contrastSlider.getValue(), "log");
        }
        if (contrastType.getValue().equals("potęgowy")) {
            bits = ImageOperations.contrast(bits, (float) contrastSlider.getValue(), "pow");
        }
        if (contrastType.getValue().equals("liniowy")) {
            bits = ImageOperations.contrast(bits, (float) contrastSlider.getValue(), "lin");
        }
        bits = ConverterRGB.HSLtoRGB(bits.length, bits[0].length, bits);
        applyChanges.fire();
    }

    public void sumaObr(ActionEvent event) {
        bits2 = ReadFile.main();
        bits = ImageOperations.imageSum(bits.length, bits[0].length, bits, bits2);
        update(event);
    }

    public void roznObr(ActionEvent event) {
        bits2 = ReadFile.main();
        bits = ImageOperations.imageSubtraction(bits.length, bits[0].length, bits, bits2);
        update(event);
    }

    public void iloObr(ActionEvent event) {
        bits2 = ReadFile.main();
        bits = ImageOperations.imageProduct(bits.length, bits[0].length, bits, bits2);
        update(event);
    }

    public void standardBlur(){
        bits = Filters.standardBlur(9, bits);
        applyChanges.fire();
    }
    public void gaussianBlur(){
        bits = Filters.gaussianBlur(9, (float) gaussSlider.getValue(), bits);
        applyChanges.fire();
    }
    public void imageSharpening(){
        bits = Filters.imageSharpening(bits);
        applyChanges.fire();
    }
    public void sobelFiltering(){
        bits = Filters.sobelFiltering(bits);
        applyChanges.fire();
    }
    public void prewittFiltering(){
        bits = Filters.prewittFiltering(bits);
        applyChanges.fire();
    }
    public void robertsFiltering(){
        bits = Filters.robertsFiltering(bits);
        applyChanges.fire();
    }
    public void laplaceFiltering(){
        bits = Filters.laplaceFiltering(bits);
        applyChanges.fire();
    }
    public void laplacianOfGaussianFiltering(){
        bits = Filters.laplacianOfGaussianFiltering(bits);
        applyChanges.fire();
    }

}