package com.example.microgimp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javafx.scene.Scene;

import java.util.Objects;

public class Display extends Application {
    @Override
    public void start(Stage mainStage) {
        try{
            //settings are located in displayView.fxml
            Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("displayView.fxml"))));
            //scene dimensions and name
            mainStage.setTitle("Micro GIMP");
            mainStage.setScene(new Scene(root));
            mainStage.show();



            //load CSS
            String css = Objects.requireNonNull(this.getClass().getResource("application.css")).toExternalForm();
            root.getStylesheets().add(css);
            mainStage.setResizable(false);
            Image icon = new Image("/logoDiag.png");
            mainStage.getIcons().add(icon);


        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch();
    }
}