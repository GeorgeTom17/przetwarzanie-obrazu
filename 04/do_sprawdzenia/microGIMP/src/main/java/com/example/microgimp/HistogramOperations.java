package com.example.microgimp;

public class HistogramOperations {


    public static float[][] stretchHist(int height, int width, float[][] bits, String type){
        float[][] result = new float[height][width];
        float valR;
        float valG;
        float valB;
        float minR = 1;
        float maxR = 0;
        float minG = 1;
        float maxG = 0;
        float minB = 1;
        float maxB = 0;
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width / 3; y++) {
                valR =  bits[x][y * 3];
                valG =  bits[x][y * 3 + 1];
                valB =  bits[x][y * 3 + 2];
                if (valR < minR){
                    minR = valR;
                }
                if (valR > maxR){
                    maxR = valR;
                }
                if (valG < minG){
                    minG = valG;
                }
                if (valG > maxG){
                    maxG = valG;
                }
                if (valB < minB){
                    minB = valB;
                }
                if (valB > maxB){
                    maxB = valB;
                }
            }
        }
        if (type.equals("gray")) {
            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width / 3; y++) {
                    result[x][y*3] = (bits[x][y*3] - minR)/(maxR - minR);
                    result[x][y*3 + 1] = (bits[x][y*3 + 1] - minG)/(maxG - minG);
                    result[x][y*3 + 2] = (bits[x][y*3 + 2] - minB)/(maxB - minB);
                }
            }
        }
        if (type.equals("red")){
            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width / 3; y++) {
                    result[x][y*3] = (bits[x][y*3] - minR)/(maxR - minR);
                    result[x][y*3 + 1] = bits[x][y*3 + 1];
                    result[x][y*3 + 2] = bits[x][y*3 + 2];
                }
            }
        }
        if (type.equals("green")){
            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width / 3; y++) {
                    result[x][y*3] = bits[x][y*3];
                    result[x][y*3 + 1] = (bits[x][y*3 + 1] - minG)/(maxG - minG);
                    result[x][y*3 + 2] = bits[x][y*3 + 2];
                }
            }
        }
        if (type.equals("blue")){
            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width / 3; y++) {
                    result[x][y*3] = bits[x][y*3];
                    result[x][y*3 + 1] = bits[x][y*3 + 1];
                    result[x][y*3 + 2] = (bits[x][y*3 + 2] - minB)/(maxB - minB);
                }
            }
        }
        return result;
    }
    public static float[][] equalizeHist(int height, int width, float[][] bits, String type) {
        float[][] result = new float[height][width];
        int[][] temp = new int[height][width];
        float[] histR = new float[256];
        float[] histG = new float[256];
        float[] histB = new float[256];
        float[] cHistR = new float[256];
        float[] cHistG = new float[256];
        float[] cHistB = new float[256];
        float[] arrR = new float[256];
        float[] arrG = new float[256];
        float[] arrB = new float[256];
        int totPix = width*height/3;
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width / 3; y++) {
                temp[x][y*3] = Math.round(bits[x][y*3]*255);
                temp[x][y*3 + 1] = Math.round(bits[x][y*3 + 1]*255);
                temp[x][y*3 + 2] = Math.round(bits[x][y*3 + 2]*255);
            }
        }
        for (int x = 0; x < height; x++){
            for (int y = 0; y < width / 3; y++) {
                histR[temp[x][y*3]]++;
                histG[temp[x][y*3+1]]++;
                histB[temp[x][y*3+2]]++;
            }
        }
        cHistR[0] = histR[0];
        cHistG[0] = histG[0];
        cHistB[0] = histB[0];
        for(int i=1;i<256;i++){
            cHistR[i] = cHistR[i-1] + histR[i];
            cHistG[i] = cHistG[i-1] + histG[i];
            cHistB[i] = cHistB[i-1] + histB[i];
        }

        for(int i=0;i<256;i++){
            arrR[i] =  (float)((cHistR[i]*255.0)/(float)totPix);
            arrG[i] =  (float)((cHistG[i]*255.0)/(float)totPix);
            arrB[i] =  (float)((cHistB[i]*255.0)/(float)totPix);
        }

        if (type.equals("gray")){
            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width/3; y++) {
                    result[x][y*3] = arrR[temp[x][y*3]]/255;
                    result[x][y*3+1] = arrG[temp[x][y*3+1]]/255;
                    result[x][y*3+2] = arrB[temp[x][y*3+2]]/255;
                }
            }
        }
        if (type.equals("red")){
            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width/3; y++) {
                    result[x][y*3] = arrR[temp[x][y*3]]/255;
                    result[x][y*3+1] = bits[x][y*3+1];
                    result[x][y*3+2] = bits[x][y*3+2];
                }
            }
        }
        if (type.equals("green")){
            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width/3; y++) {
                    result[x][y*3] = bits[x][y*3];
                    result[x][y*3+1] = arrG[temp[x][y*3+1]]/255;
                    result[x][y*3+2] = bits[x][y*3+2];
                }
            }
        }
        if (type.equals("blue")){
            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width/3; y++) {
                    result[x][y*3] = bits[x][y*3];
                    result[x][y*3+1] = bits[x][y*3+1];
                    result[x][y*3+2] = arrB[temp[x][y*3+2]]/255;
                }
            }
        }
        return result;
    }


}
