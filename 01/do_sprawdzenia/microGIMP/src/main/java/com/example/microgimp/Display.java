package com.example.microgimp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.paint.Color;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.io.FileInputStream;
import java.io.IOException;

public class Display extends Application {
    @Override
    public void start(Stage mainStage) throws IOException {
        try{
            //settings are located in displayView.fxml
            FXMLLoader fxmlLoader = new FXMLLoader(Display.class.getResource("displayView.fxml"));
            //scene dimensions and name

            Scene scene = new Scene(fxmlLoader.load(), 1366, 720);
            mainStage.setTitle("Micro GIMP");
            //load CSS
            String css = this.getClass().getResource("application.css").toExternalForm();
            scene.getStylesheets().add(css);
            mainStage.setScene(scene);
            mainStage.show();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void showImage(Stage mainStage, float[][] bits, int type) throws IOException{ //wywoływane w ReadFile jak juz wiemy, że bits nie jest null
        try{
            int width = bits[0].length;
            int height = bits.length;
            Color color;
            WritableImage wImage;
            if (type == 3 || type == 6){
                wImage = new WritableImage(height, width/3); //w kolorowych każdy pixel zajmuje 3 komórki w bits
            }
            else {
                wImage = new WritableImage(height, width);
            }
            PixelWriter writer = wImage.getPixelWriter();

            switch (type) {
                case 1, 2, 4, 5 -> showMono(height, width, bits, writer);
                case 3, 6 -> showRGB(height, width, bits, writer);
                default -> {
                }
            }

            ImageView imageView = new ImageView(wImage);
            imageView.setFitHeight(300); //te wartości można zmienić, dałem takie na oko tylko
            imageView.setFitWidth(300);
            Group root = new Group(imageView);
            Scene scene = new Scene(root, 300, 300);
            mainStage.setTitle("Wyświetlanie obrazu");

            mainStage.setScene(scene);
            mainStage.show();

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch();
    }



    private static void showMono(int height, int width, float[][] bits, PixelWriter writer){
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                //Retrieving the color of the pixel of the loaded image
                Color color = new Color(bits[x][y], bits[x][y], bits[x][y], 1); //każda wartość rgb jest taka sama
                //Setting the color to the writable image
                writer.setColor(x, y, color);
            }
        }
    }
    private static void showRGB(int height, int width, float[][] bits, PixelWriter writer){
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width/3 ; y++) { //znowu 3 wartości na pixel
                //Retrieving the color of the pixel of the loaded image
                Color color = new Color(bits[x][y*3], bits[x][y*3 + 1], bits[x][y*3 + 2], 1); //przypisuje rgb
                //Setting the color to the writable image
                writer.setColor(x, y, color);
            }
        }
    }
}