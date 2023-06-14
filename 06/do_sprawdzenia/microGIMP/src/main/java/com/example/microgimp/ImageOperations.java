package com.example.microgimp;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

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

    public static float[][] resizeImage(float[][] originalImage, int outputWidth, int outputHeight) {
        float[][] resizedImage = new float[outputHeight][outputWidth * 3];
        float[][] channelR = new float[originalImage.length][originalImage[0].length / 3];
        float[][] channelG = new float[originalImage.length][originalImage[0].length / 3];
        float[][] channelB = new float[originalImage.length][originalImage[0].length / 3];
        for (int x = 0; x < originalImage.length; x++) {
            for (int y = 0; y < originalImage[0].length / 3; y++) {
                channelR[x][y] = originalImage[x][y * 3];
                channelG[x][y] = originalImage[x][y * 3 + 1];
                channelB[x][y] = originalImage[x][y * 3 + 2];
            }
        }

        for (int x = 0; x < outputHeight; x++) {
            for (int y = 0; y < outputWidth; y++) {
                float pixelR;
                float pixelG;
                float pixelB;
                // Calculate the corresponding pixel coordinates in the original image
                float srcY = (float) y * channelR[0].length / outputWidth;
                float srcX = (float) x * originalImage.length / outputHeight;
                pixelR = nearestNeighbour(channelR, srcX, srcY);
                pixelG = nearestNeighbour(channelG, srcX, srcY);
                pixelB = nearestNeighbour(channelB, srcX, srcY);
                resizedImage[x][y*3] = pixelR;
                resizedImage[x][y*3+1] = pixelG;
                resizedImage[x][y*3+2] = pixelB;
            }
        }
        return resizedImage;
    }

    private static float nearestNeighbour(float[][] channel, float srcX, float srcY){
        int x1 = (int) Math.floor(srcX);
        int x2 = (int) Math.ceil(srcX);
        int y1 = (int) Math.floor(srcY);
        int y2 = (int) Math.ceil(srcY);
        float result = channel[x1][y1];
        float minDist;
        float distLG = (float) Math.sqrt((srcX - x1) * (srcX - x1) + (srcY - y1) * (srcY - y1));
        float distLD = (float) Math.sqrt((srcX - x2) * (srcX - x2) + (srcY - y1) * (srcY - y1));
        float distPG = (float) Math.sqrt((srcX - x1) * (srcX - x1) + (srcY - y2) * (srcY - y2));
        float distPD = (float) Math.sqrt((srcX - x2) * (srcX - x2) + (srcY - y2) * (srcY - y2));
        minDist = Math.min(distLG, Math.min(distLD, Math.min(distPG, distPD)));
        if (distLG == minDist){
            result = channel[x1][y1];
        }
        if (distLD == minDist){
            if (x2 > channel.length - 1){
                x2 = channel.length - 1;
            }
            result = channel[x2][y1];
        }
        if (distPG == minDist){
            if (y2 > channel.length - 1){
                y2 = channel.length - 1;
            }
            result = channel[x1][y2];
        }
        if (distPD == minDist){
            if (x2 > channel.length - 1){
                x2 = channel.length - 1;
            }
            if (y2 > channel.length - 1){
                y2 = channel.length - 1;
            }
            result = channel[x2][y2];
        }

        return result;
    }
}
