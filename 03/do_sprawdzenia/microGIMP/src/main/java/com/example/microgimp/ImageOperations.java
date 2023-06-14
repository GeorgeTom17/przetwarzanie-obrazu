package com.example.microgimp;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.util.Arrays;


public class ImageOperations {
    public static void show(int height, int width, float[][] bits, PixelWriter writer) {
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width / 3; y++) { //znowu 3 wartości na pixel
                Color color = new Color(bits[x][3 * y], bits[x][3 * y + 1], bits[x][3 * y + 2], 1);
                writer.setColor(y, x, color);
            }
        }
    }

    public static float[][] negative(int height, int width, float[][] bits) {
        float[][] retBits = new float[height][width];
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width / 3; y++) { //znowu 3 wartości na pixel
                //Retrieving the color of the pixel of the loaded image
                retBits[x][y * 3] = 1 - bits[x][y * 3];
                retBits[x][y * 3 + 1] = 1 - bits[x][y * 3 + 1];
                retBits[x][y * 3 + 2] = 1 - bits[x][y * 3 + 2];
                //Setting the color to the writable image
            }
        }
        return retBits;
    }

    public static float[][] desaturation(float[][] bits, float desatVal) {
        int width = bits[0].length;
        int numColors = width / 3;
        for (int x = 0; x < bits.length; x++) {
            for (int y = 0; y < numColors; y++) {
                int index = y * 3 + 1;
                bits[x][index] = desatVal;
            }
        }
        return bits;
    }

    public static float[][] brightness(float[][] bits, float jasnoscVal) {
        float[][] changed = new float[bits.length][bits[0].length];
        for (int x = 0; x < bits.length; x++) {
            for (int y = 0; y < bits[0].length / 3; y++) {
                if (jasnoscVal > 0){
                    changed[x][y*3] = bits[x][y*3] + (1 - bits[x][y*3]) * jasnoscVal;
                    changed[x][y*3+1] = bits[x][y*3+1] + (1 - bits[x][y*3+1]) * jasnoscVal;
                    changed[x][y*3+2] = bits[x][y*3+2] + (1 - bits[x][y*3+2]) *jasnoscVal;
                }
                else if (jasnoscVal < 0){
                    changed[x][y*3] = bits[x][y*3] - bits[x][y*3] * Math.abs(jasnoscVal);
                    changed[x][y*3+1] = bits[x][y*3+1] - bits[x][y*3+1] * Math.abs(jasnoscVal);
                    changed[x][y*3+2] = bits[x][y*3+2] - bits[x][y*3+2] * Math.abs(jasnoscVal);
                }
                else{
                    changed[x][y*3] = bits[x][y*3];
                    changed[x][y*3+1] = bits[x][y*3+1];
                    changed[x][y*3+2] = bits[x][y*3+2];
                }
            }
        }
        return changed;
    }

    public static float[][] contrast(float[][] bits, float kontrastVal, String type){
        float[][] changed = new float[bits.length][bits[0].length];
        for (int x = 0; x < bits.length; x++) {
            for (int y = 0; y < bits[0].length / 3; y++) {
                if (type.equals("pow")){
                    changed[x][y*3] = bits[x][y*3];
                    changed[x][y*3+1] = bits[x][y*3+1];
                    if ((float) Math.pow(bits[x][y*3+2], kontrastVal) < 0.05){
                        changed[x][y*3+2] = (float) 0.05;
                    }
                    else {
                        changed[x][y * 3 + 2] = (float) Math.pow(bits[x][y * 3 + 2], kontrastVal);
                    }
                }
                if (type.equals("log")){
                    changed[x][y*3] = bits[x][y*3];
                    changed[x][y*3+1] = bits[x][y*3+1];
                    if ((float) Math.pow(bits[x][y*3+2], 1/kontrastVal) < 0.05){
                        changed[x][y*3+2] = (float) 0.05;
                    }
                    else {
                        changed[x][y * 3 + 2] = (float) Math.pow(bits[x][y * 3 + 2], 1 / kontrastVal);
                    }
                }
                if (type.equals("lin")){
                    changed[x][y*3] = bits[x][y*3];
                    changed[x][y*3+1] = bits[x][y*3+1];
                    if (kontrastVal == 5.0){
                        if (bits[x][y*3+2] <= 0.5){
                            changed[x][y*3+2] = (float) 0.05;
                        }
                        else{
                            changed[x][y*3+2] = (float) 0.95;
                        }
                        continue;
                    }
                    if (bits[x][y*3+2] <= kontrastVal/10.0){ //dzielimy przez maxVal*2 kontrastSlider trzeba o tym pamiętać
                        changed[x][y*3+2] = (float) 0.05;
                    }
                    if (bits[x][y*3+2] >= (1 - kontrastVal/10.0)){ //dzielimy przez maxVal*2 kontrastSlider trzeba o tym pamiętać
                        changed[x][y*3+2] = (float) 0.95;
                    }
                    else {
                        changed[x][y * 3 + 2] = (float) ((5.0/(5.0 - kontrastVal))*(bits[x][y*3+2] - kontrastVal/10));
                    }
                }
            }
        }

        return changed;
    }

    public static float[] getHistVals(int height, int width, float[][] bits, String type){
        float[] hist = new float[256];
        int val;
        if (type.equals("gray")) {
            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width / 3; y++) {
                    val = Math.round((float) (0.299 * (bits[x][y * 3] * 255) + 0.587 * (bits[x][y * 3 + 1] * 255) + 0.114 * (bits[x][y * 3 + 2] * 255)));
                    hist[val] += 1;
                }
            }
        }
        if (type.equals("red")){
            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width / 3; y++) {
                    val = Math.round(bits[x][y * 3] * 255);
                    hist[val] += 1;
                }
            }
        }
        if (type.equals("green")){
            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width / 3; y++) {
                    val = Math.round(bits[x][y * 3 + 1] * 255);
                    hist[val] += 1;
                }
            }
        }
        if (type.equals("blue")){
            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width / 3; y++) {
                    val = Math.round(bits[x][y * 3 + 2] * 255);
                    hist[val] += 1;
                }
            }
        }

        for (int j = 0; j < hist.length; j++) {
            hist[j] /= ((double) height * width / 3);
        }
        return hist;
    }

    public static float[][] imageSum(int height, int width, float[][] bits, float[][] bits2) {
        float[][] wynik = new float[bits.length][bits[0].length];
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width / 3; y++) {
                wynik[x][y*3] = (bits[x][y * 3] + bits2[x][y * 3]) % 1;
                wynik[x][y*3+1] = (bits[x][y * 3 + 1] + bits2[x][y * 3 + 1]) % 1;
                wynik[x][y*3+2] = (bits[x][y * 3 + 2] + bits2[x][y * 3 + 2]) % 1;
            }
        }
        return wynik;
    }

    public static float[][] imageSubtraction(int height, int width, float[][] bits, float[][] bits2) {
        float[][] wynik = new float[bits.length][bits[0].length];
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width / 3; y++) {
                wynik[x][y*3] = Math.abs(bits[x][y * 3] - bits2[x][y * 3]);
                wynik[x][y*3+1] = Math.abs(bits[x][y * 3 + 1] - bits2[x][y * 3 + 1]);
                wynik[x][y*3+2] = Math.abs(bits[x][y * 3 + 2] - bits2[x][y * 3 + 2]);
            }
        }
        return wynik;
    }

    public static float[][] imageProduct(int height, int width, float[][] bits, float[][] bits2) {
        float[][] wynik = new float[bits.length][bits[0].length];
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width / 3; y++) {
                wynik[x][y*3] = bits[x][y * 3] * bits2[x][y * 3];
                wynik[x][y*3+1] = bits[x][y * 3 + 1] * bits2[x][y * 3 + 1];
                wynik[x][y*3+2] = bits[x][y * 3 + 2] * bits2[x][y * 3 + 2];
            }
        }
        return wynik;
    }
}
