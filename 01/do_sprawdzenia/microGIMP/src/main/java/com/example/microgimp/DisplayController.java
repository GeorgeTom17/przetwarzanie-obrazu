package com.example.microgimp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class DisplayController implements Initializable {
    @FXML
    private Label welcomeText;
    @FXML
    private ComboBox anymapComboBox;

    double defaultWidth = 591.0;
    double messageWidth = 175.0;
    float[][] bits;

    @FXML
    protected void onOkButtonClick() throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("chosen file type", "*." + anymapComboBox.getValue().toString().toLowerCase()); /*pokazuje tylko pliki z tym rozszerzeniem*/
        if (!anymapComboBox.getValue().toString().equals("brak")) {
            fileChooser.getExtensionFilters().add(extFilter);
            File selectedFile = fileChooser.showOpenDialog(null); /*otwiera fileChooser*/
            if (selectedFile != null) {

                welcomeText.setText("Otwieranie: " + selectedFile.getName());
                welcomeText.setLayoutX(defaultWidth - messageWidth / 2);
            } else {
                welcomeText.setText("Nie udało się załadować pliku.");
                welcomeText.setLayoutX(defaultWidth - messageWidth / 2);
            }
            assert selectedFile != null;
            ReadFile.main(selectedFile.getAbsolutePath()); /*wczytuje plik*/

        }
        else {
            welcomeText.setText("WYBIERZ AKCJĘ");
            welcomeText.setLayoutX(defaultWidth);
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        anymapComboBox.getItems().addAll("brak", "PBM", "PGM", "PPM");
    }
}