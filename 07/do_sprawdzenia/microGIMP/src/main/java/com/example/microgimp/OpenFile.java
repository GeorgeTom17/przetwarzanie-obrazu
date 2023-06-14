package com.example.microgimp;

import javafx.stage.FileChooser;
import java.io.File;


public class OpenFile {
    public static File getFile(String ext){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("chosen file type", "*." + ext); /*pokazuje tylko pliki z tym rozszerzeniem*/
        fileChooser.getExtensionFilters().add(extFilter);

        return fileChooser.showOpenDialog(null);
    }
}
