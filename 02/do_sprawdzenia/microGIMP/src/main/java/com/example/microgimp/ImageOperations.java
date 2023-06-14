package com.example.microgimp;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class ImageOperations {
    public static void show(int height, int width, float[][] bits, PixelWriter writer) {
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width / 3; y++) { //znowu 3 wartości na pixel
                //Retrieving the color of the pixel of the loaded image
                Color color = new Color(bits[x][3 * y], bits[x][3 * y + 1], bits[x][3 * y + 2], 1); //przypisuje rgb
                //Setting the color to the writable image
                writer.setColor(x, y, color);
            }
        }
    }

    public static void negative(int height, int width, float[][] bits, PixelWriter writer) {
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width / 3; y++) { //znowu 3 wartości na pixel
                //Retrieving the color of the pixel of the loaded image
                Color color = new Color(1 - bits[x][y * 3], 1 - bits[x][y * 3 + 1], 1 - bits[x][y * 3 + 2], 1); //przypisuje rgb
                //Setting the color to the writable image
                writer.setColor(x, y, color);
            }
        }
    }

    public static void desaturation(int height, int width, float[][] bits, PixelWriter writer, float desat, float light, float sat, String contrast, float contrVal) {
        float[][] desaturated;
        desaturated = ConverterRGB.rgbToHSL(height, width, bits, desat, light, sat, contrast, contrVal);
        show(height, width, desaturated, writer);
    }

    public static void histogram(int height, int width, float[][] bits){
        float[] hist = new float[256];
        float sum = 0;
        int val;
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width / 3; y++) {
                val = Math.round((float) (0.299*(bits[x][y * 3]*255) + 0.587*(bits[x][y * 3 + 1]*255) + 0.114*(bits[x][y * 3 + 2]*255)));
                hist[val] += 1;
            }
        }
        for (int j = 0; j < hist.length; j++) {
            hist[j] /= ((double) height * width / 3);
        }
        Stage stage = new Stage();
        DrawChart.showChart(stage, hist);
    }

    public static void SumRozIlo(int height, int width, float[][] bits, float[][] bits2, PixelWriter writer, String type) {
        float newvalR = 0;
        float newvalG = 0;
        float newvalB = 0;
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width / 3; y++) {
                if (type.equals("suma obrazów")) {
                    newvalR = (bits[x][y * 3] + bits2[x][y * 3]) % 1;
                    newvalG = (bits[x][y * 3 + 1] + bits2[x][y * 3 + 1]) % 1;
                    newvalB = (bits[x][y * 3 + 2] + bits2[x][y * 3 + 2]) % 1;
                } else if (type.equals("różnica obrazów")) {
                    newvalR = Math.abs(bits[x][y * 3] - bits2[x][y * 3]);
                    newvalG = Math.abs(bits[x][y * 3 + 1] - bits2[x][y * 3 + 1]);
                    newvalB = Math.abs(bits[x][y * 3 + 2] - bits2[x][y * 3 + 2]);
                } else if (type.equals("iloczyn obrazów")) {
                    newvalR = bits[x][y * 3] * bits2[x][y * 3];
                    newvalG = bits[x][y * 3 + 1] * bits2[x][y * 3 + 1];
                    newvalB = bits[x][y * 3 + 2] * bits2[x][y * 3 + 2];
                }
                Color color = new Color(newvalR, newvalG, newvalB, 1);
                writer.setColor(x, y, color);
            }
        }


    }
}
