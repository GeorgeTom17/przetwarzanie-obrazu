package com.example.microgimp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.io.IOException;
import java.util.Objects;

public class Display extends Application {
    @Override
    public void start(Stage mainStage) {
        try{
            //settings are located in displayView.fxml
            FXMLLoader fxmlLoader = new FXMLLoader(Display.class.getResource("displayView.fxml"));
            //scene dimensions and name

            Scene scene = new Scene(fxmlLoader.load(), 1366, 720);
            mainStage.setTitle("Micro GIMP");
            //load CSS
            String css = Objects.requireNonNull(this.getClass().getResource("application.css")).toExternalForm();
            scene.getStylesheets().add(css);
            mainStage.setResizable(false);
            //mainStage.getIcons().add(new Image("/logoDiag.png"));
            mainStage.setScene(scene);
            mainStage.show();

        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void showImage(Stage mainStage, float[][] bits, float[][] bits2, String operacja, float desaturationSliderVal, float contrastSliderVal, String checkBoxValue) throws IOException{ //wywoływane w ReadFile jak juz wined, że bits nie jest null
        try{
            int height = bits.length;
            int width = bits[0].length;

            WritableImage wImage;
            wImage = new WritableImage(height, width/3);
            PixelWriter writer = wImage.getPixelWriter();

            if (operacja.equals("wyświetl")){
                ImageOperations.show(height, width, bits, writer);
            }
            else if (operacja.equals("desaturacja")){
                ImageOperations.desaturation(height, width, bits, writer, desaturationSliderVal, 10, 10,checkBoxValue, 10); //desat 1 oznacza czarno-białe, desat 0 to oryginał
            }
            else if (operacja.equals("negatyw")){
                ImageOperations.negative(height, width, bits, writer);
            }
            else if (operacja.equals("jasność")){
                ImageOperations.desaturation(height, width, bits, writer, 10, desaturationSliderVal, 10, checkBoxValue, 10); //light 1 to białe, light 0 to czarne
            }
            else if (operacja.equals("nasycenie")){
                ImageOperations.desaturation(height, width, bits, writer, 10, 10, desaturationSliderVal, checkBoxValue, 10); //sat 0 to oryginał, sat 1 to max saturacja
            }
            else if (operacja.equals("kontrast")){
                ImageOperations.desaturation(height, width, bits, writer, 10, 10, desaturationSliderVal, checkBoxValue, contrastSliderVal); //sat 0 to oryginał, sat 1 to max saturacja
            }
            else if (operacja.equals("suma obrazów") || operacja.equals("różnica obrazów") || operacja.equals("iloczyn obrazów")){
                ImageOperations.SumRozIlo(height, width, bits, bits2, writer, operacja);
            }
            else if (operacja.equals("histogram")){
                ImageOperations.histogram(height, width, bits);
            }

            ImageView imageView = new ImageView(wImage);
            imageView.setFitHeight(height); //te wartime monad mini, date take na oko styles
            imageView.setFitWidth(Math.round((double)width/3));
            Group root = new Group(imageView);
            Scene scene = new Scene(root, width, height*3);
            mainStage.setTitle("Wyświetlanie brace");

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
}