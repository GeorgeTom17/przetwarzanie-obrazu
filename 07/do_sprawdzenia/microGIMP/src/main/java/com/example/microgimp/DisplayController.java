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
    private Slider binarySlider;

    @FXML
    private ComboBox<String> contrastType;
    @FXML
    private Slider contrastSlider;
    @FXML
    private Label contrastSliderVal;

    @FXML
    private Slider cannyGaussSigma;
    @FXML
    private Label cannyGaussSigmaVal;
    @FXML
    private Slider cannyLowerThreshold;
    @FXML
    private Label cannyLowerThresholdVal;
    @FXML
    private Slider cannyUpperThreshold;
    @FXML
    private Label cannyUpperThresholdVal;
    @FXML
    private Button cannyButton;


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
    @FXML
    private Button rescaleImage;

    @FXML
    private Button kswButton;

    @FXML
    private Button rcButton;

    @FXML
    private Button otsuButton;
    @FXML
    private TextField scaleWidth;
    @FXML
    private TextField scaleHeight;

    @FXML
    private MenuBar myMenuBar;

    @FXML
    private CheckBox showHoughResults;
    @FXML
    private Slider houghSlider;
    @FXML
    private Label houghSliderVal;

    public float[][] originalBits = new float[0][0];
    public float[][] prevBits = new float[0][0];
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
                stdBlurButton, sharpenImageButton, rescaleImage, sobelButton, prewittButton, robertsButton, laplaceButton, laplacianOfGaussianFilterButton,
                stretchingButton, equalizationButton,  otsuButton, kswButton, rcButton,
                cannyButton};

        for (Button button : buttons) {
            button.setEffect(dropShadow);
        }

        negativeButton.setEffect(dropShadow);

        desatSlider.setValue(0.51);
        desatSliderVal.textProperty().bind(Bindings.format("%.2f", desatSlider.valueProperty().subtract(0.01)));
        brightnessSlider.setValue(0.0);
        brightnessSliderVal.textProperty().bind(Bindings.format("%.2f", brightnessSlider.valueProperty()));
        contrastType.setValue("");
        contrastSlider.setValue(1.0);
        contrastSliderVal.textProperty().bind(Bindings.format("%.2f", contrastSlider.valueProperty()));
        gaussSlider.setValue(1);
        gaussSliderVal.textProperty().bind(Bindings.format("%.2f", gaussSlider.valueProperty()));
        cannyGaussSigma.setValue(0.5);
        cannyGaussSigmaVal.textProperty().bind(Bindings.format("%.2f", cannyGaussSigma.valueProperty()));
        cannyLowerThreshold.setValue(20);
        cannyLowerThresholdVal.textProperty().bind(Bindings.format("%.0f", cannyLowerThreshold.valueProperty()));
        cannyUpperThreshold.setValue(50);
        cannyUpperThresholdVal.textProperty().bind(Bindings.format("%.0f", cannyUpperThreshold.valueProperty()));
        houghSlider.setValue(0);
        houghSliderVal.textProperty().bind(Bindings.format("%.0f", houghSlider.valueProperty()));
        houghSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                houghSlider.setValue(Math.round(newVal.doubleValue())));
        cannyLowerThreshold.valueProperty().addListener((obs, oldVal, newVal) ->
                cannyLowerThreshold.setValue(Math.round(newVal.doubleValue())));
        cannyUpperThreshold.valueProperty().addListener((obs, oldVal, newVal) ->
                cannyUpperThreshold.setValue(Math.round(newVal.doubleValue())));


        desatSlider.valueProperty().addListener((observable) -> desaturation());

        desatSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                // User has stopped dragging the slider
                updatePrevBits();
            }
        });

        brightnessSlider.valueProperty().addListener((observable) -> brightness());

        brightnessSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                // User has stopped dragging the slider
                updatePrevBits();
            }
        });

        contrastSlider.valueProperty().addListener((observable) -> contrast());

        contrastSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                // User has stopped dragging the slider
                updatePrevBits();
            }
        });

        binarySlider.valueProperty().addListener((observable) -> manualBinary());

        binarySlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                // User has stopped dragging the slider
                updatePrevBits();
            }
        });

        contrastType.getItems().removeAll(contrastType.getItems());
        contrastType.getItems().addAll("brak", "logarytmiczny", "potęgowy", "liniowy");
        contrastType.getSelectionModel().select("brak");

    }

    public void rollbackChanges(ActionEvent event) {
        bits = originalBits;
        updatePrevBits();
        update(event);
    }

    public void loadMainFile(ActionEvent event) {
        bits = ReadFile.main();
        originalBits = bits;
        prevBits = bits;
        update(event);
    }

    public void saveFile(){
        SaveFile.save(bits);
    }

    public void update(ActionEvent event) {
        Stage stage = (Stage) myMenuBar.getScene().getWindow();
        WritableImage wImage;
        wImage = new WritableImage(bits[0].length/3, bits.length);
        PixelWriter writer = wImage.getPixelWriter();
        ImageOperations.show(bits.length, bits[0].length, bits, writer);
        displayedImage.setX(10);
        displayedImage.setY(10);
        displayedImage.setImage(wImage);
        /*displayedImage.setPreserveRatio(true);*/
        updateHist(event);
        stage.show();
    }

    public void updateHist(ActionEvent event) {
        Stage stage = (Stage) myMenuBar.getScene().getWindow();
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
        updatePrevBits();
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
        updatePrevBits();
        update(event);

    }

    public void negative() {
        bits = ImageOperations.negative(bits.length, bits[0].length, bits);
        updatePrevBits();
        applyChanges.fire();
    }

    public void desaturation() {
        bits = ConverterRGB.RGBtoHSL(bits.length, bits[0].length, prevBits);
        bits = ImageOperations.desaturation(bits, (float) desatSlider.getValue());
        bits = ConverterRGB.HSLtoRGB(bits.length, bits[0].length, bits);
        applyChanges.fire();
    }

    public void brightness(){
        bits = ImageOperations.brightness(prevBits, (float) brightnessSlider.getValue());
        applyChanges.fire();
    }

    public void contrast(){
        contrastType.getValue();
        bits = ConverterRGB.RGBtoHSL(bits.length, bits[0].length, prevBits);
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
        assert bits2 != null;
        bits2 = ImageOperations.resizeImage(bits2, bits[0].length/3, bits.length);
        bits = ImageOperations.imageSum(bits.length, bits[0].length, bits, bits2);
        updatePrevBits();
        update(event);
    }

    public void roznObr(ActionEvent event) {
        bits2 = ReadFile.main();
        assert bits2 != null;
        bits2 = ImageOperations.resizeImage(bits2, bits[0].length/3, bits.length);
        bits = ImageOperations.imageSubtraction(bits.length, bits[0].length, bits, bits2);
        updatePrevBits();
        update(event);
    }

    public void iloObr(ActionEvent event) {
        bits2 = ReadFile.main();
        assert bits2 != null;
        bits2 = ImageOperations.resizeImage(bits2, bits[0].length/3, bits.length);
        bits = ImageOperations.imageProduct(bits.length, bits[0].length, bits, bits2);
        updatePrevBits();
        update(event);
    }

    public void standardBlur(){
        bits = Filters.standardBlur(9, bits);
        updatePrevBits();
        applyChanges.fire();
    }

    public void gaussianBlur(){
        bits = Filters.gaussianBlur(9, (float) gaussSlider.getValue(), bits);
        updatePrevBits();
        applyChanges.fire();
    }

    public void imageSharpening(){
        bits = Filters.imageSharpening(bits);
        updatePrevBits();
        applyChanges.fire();
    }

    public void sobelFiltering(){
        bits = Filters.sobelFiltering(bits);
        updatePrevBits();
        applyChanges.fire();
    }

    public void prewittFiltering(){
        bits = Filters.prewittFiltering(bits);
        updatePrevBits();
        applyChanges.fire();
    }

    public void robertsFiltering(){
        bits = Filters.robertsFiltering(bits);
        updatePrevBits();
        applyChanges.fire();
    }

    public void laplaceFiltering(){
        bits = Filters.laplaceFiltering(bits);
        updatePrevBits();
        applyChanges.fire();
    }

    public void laplacianOfGaussianFiltering(){
        bits = Filters.laplacianOfGaussianFiltering(bits);
        updatePrevBits();
        applyChanges.fire();
    }

    public void rescaleImage(){
        bits = ImageOperations.resizeImage(bits, Integer.parseInt(scaleWidth.getCharacters(), 0, scaleWidth.getCharacters().length(), 10), Integer.parseInt(scaleHeight.getCharacters(), 0, scaleHeight.getCharacters().length(), 10));
        updatePrevBits();
        applyChanges.fire();
    }

    public void binaryOtsu(){
        bits = QuantizationOperations.binaryOtsu(bits);
        updatePrevBits();
        applyChanges.fire();
    }

    public void binaryKSW(){
        bits = QuantizationOperations.binaryKSW(bits);
        updatePrevBits();
        applyChanges.fire();
    }

    public void binaryRC(){
        bits = QuantizationOperations.binaryRC(bits);
        updatePrevBits();
        applyChanges.fire();
    }

    public void manualBinary(){
        bits = QuantizationOperations.manual((float) binarySlider.getValue(), prevBits);
        applyChanges.fire();
    }

    public void cannyEdgeDetection(){
        bits = EdgeDetection.cannyAlgorithm(prevBits, (float) cannyGaussSigma.getValue(), (float) (cannyLowerThreshold.getValue()/100), (float) cannyUpperThreshold.getValue()/100);
        applyChanges.fire();
    }

    public void houghTransform(){
        bits = HoughTransform.houghTransform(prevBits, showHoughResults.isSelected(), (int) houghSlider.getValue());
        applyChanges.fire();
    }

    public void updatePrevBits(){
        prevBits = bits;
    }

}