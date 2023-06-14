package com.example.microgimp;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class DisplayController implements Initializable {
    @FXML
    private Label welcomeText;
    @FXML
    private ComboBox<String> anymapComboBox;
    @FXML
    private ComboBox<String> imageOperations;
    @FXML
    private Slider operationsSlider;
    @FXML
    private CheckBox logCheckBox;
    @FXML
    private CheckBox powerCheckBox;
    @FXML
    private Label sliderValue;

    double defaultWidth = 591.0;
    double messageWidth = 175.0;

    int sliderDefaultY = 162;
    int sliderValueDefaultY = 155;

    @FXML
    protected void onOkButtonClick() throws IOException {
        String checkBoxValue = "";
        String ext = anymapComboBox.getValue().toLowerCase();
        String operacja = imageOperations.getValue().toLowerCase();
        float[][] bits;
        float[][] bits2;
        float desaturationSliderVal = (float) operationsSlider.getValue() / 100;
        float contrastSliderVal = (float) operationsSlider.getValue() / 20;
        boolean powerCheckboxValue = powerCheckBox.isSelected(); // true zaznaczony; false nie zaznaczony
        boolean logCheckboxValue = logCheckBox.isSelected();

        if (!ext.equals("brak") && !operacja.equals("brak")) {
            File selectedFile = OpenFile.getFile(ext);
            if (selectedFile != null) {
                welcomeText.setText("Otwarto: " + selectedFile.getName());
                welcomeText.setLayoutX(defaultWidth - messageWidth / 4);
            } else {
                welcomeText.setText("Nie udało się załadować pliku.");
                welcomeText.setLayoutX(defaultWidth - messageWidth / 2);
            }
            assert selectedFile != null;

            Stage imageStage = new Stage(); //nowe okno
            bits = ReadFile.main(selectedFile.getAbsolutePath());
            if (operacja.equals("suma obrazów") || operacja.equals("różnica obrazów") || operacja.equals("iloczyn obrazów")) {
                selectedFile = OpenFile.getFile("p*m");
                bits2 = ReadFile.main(selectedFile.getAbsolutePath());
                Display.showImage(imageStage, bits, bits2, operacja, 0, 0, checkBoxValue);
            } else if (operacja.equals("kontrast")) {
                if (powerCheckboxValue && !logCheckboxValue){
                    checkBoxValue = "pow";
                    Display.showImage(imageStage, bits, new float[0][0], operacja, desaturationSliderVal, contrastSliderVal, checkBoxValue);
                } else if (!powerCheckboxValue && logCheckboxValue) {
                    checkBoxValue = "log";
                    Display.showImage(imageStage, bits, new float[0][0], operacja, desaturationSliderVal, contrastSliderVal, checkBoxValue);
                } else {
                    welcomeText.setText("WYBIERZ JEDNĄ FUNKCJĘ");
                    welcomeText.setLayoutX(defaultWidth - messageWidth / 4);
                }
            } else{
                Display.showImage(imageStage, bits, new float[0][0], operacja, desaturationSliderVal, contrastSliderVal, checkBoxValue);
            }


        }
        else {
            if(operacja.equals("brak") && !ext.equals("brak")) {
                welcomeText.setText("WYBIERZ OPERACJĘ");
                welcomeText.setLayoutX(defaultWidth - messageWidth / 8);
            } else if (!operacja.equals("brak")) {
                welcomeText.setText("WYBIERZ FORMAT PLIKU");
                welcomeText.setLayoutX(defaultWidth - messageWidth / 4);
            }
            else{
                welcomeText.setText("WYBIERZ AKCJĘ");
                welcomeText.setLayoutX(defaultWidth);
            }
        }
    }
    @FXML
    protected void guiVisibility() {
        String operacja = imageOperations.getValue().toLowerCase();
        if(operacja.equals("desaturacja") || operacja.equals("jasność") || operacja.equals("nasycenie")){
            operationsSlider.setVisible(true);
            sliderValue.setVisible(true);
            operationsSlider.setLayoutY(sliderDefaultY + 19);
            sliderValue.setLayoutY(sliderValueDefaultY + 19);
            logCheckBox.setVisible(false);
            powerCheckBox.setVisible(false);
        } else if (operacja.equals("kontrast")) {
            operationsSlider.setVisible(true);
            sliderValue.setVisible(true);
            powerCheckBox.setVisible(true);
            logCheckBox.setVisible(true);
            operationsSlider.setLayoutY(sliderDefaultY);
            sliderValue.setLayoutY(sliderValueDefaultY);
        }
        else{
            sliderValue.setVisible(false);
            operationsSlider.setVisible(false);
            logCheckBox.setVisible(false);
            powerCheckBox.setVisible(false);
            operationsSlider.setLayoutY(sliderDefaultY);
            sliderValue.setLayoutY(sliderValueDefaultY);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sliderValue.setVisible(false);
        operationsSlider.setVisible(false);
        logCheckBox.setVisible(false);
        powerCheckBox.setVisible(false);
        sliderValue.textProperty().bind(Bindings.format("%.0f", operationsSlider.valueProperty()));
        anymapComboBox.getItems().addAll("brak", "PBM", "PGM", "PPM");
        imageOperations.getItems().addAll("brak", "wyświetl", "desaturacja", "negatyw", "kontrast", "jasność", "nasycenie", "suma obrazów", "różnica obrazów", "iloczyn obrazów", "przekształcenie mono", "histogram");
    }

}