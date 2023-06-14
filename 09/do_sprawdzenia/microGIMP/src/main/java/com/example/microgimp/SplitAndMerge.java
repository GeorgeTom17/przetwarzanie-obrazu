package com.example.microgimp;

import java.util.Arrays;

public class SplitAndMerge {
    public static float[][] splitAndMerge(float[][] img, float variance, int minSize, float mergeThreshold){
        float[][] gray;
        gray = EdgeDetection.turnGrayscale(EdgeDetection.reduceMatrix(img));

        Node root = new Node(0, 0, gray.length, gray[0].length, gray, getAvg(gray));
        root = startRecursive(gray, minSize, variance, root);
        return drawRegions(img, root, mergeThreshold);
    }

    private static Node startRecursive(float[][] gray, int minSize, float varianceThreshold, Node node){
        return addRecursive(node, gray, minSize, varianceThreshold, gray);
    }

    public static Node addRecursive(Node current, float[][] quadrant, int minSize, float varianceThreshold, float[][] originalImage) {
        float variance = getVar(quadrant);
        if (current.quadrant.length < minSize*2 || current.quadrant[0].length < minSize*2 || variance <= varianceThreshold) {
            return current;
        }
        else {
            int[] posUL = {current.x1, current.x1 + (int) Math.ceil((float) (current.x2 - current.x1) / 2), current.y1, current.y1 + (int) Math.ceil((float) (current.y2 - current.y1) / 2)};
            int[] posUR = {current.x1, current.x1 + (int) Math.ceil((float) (current.x2 - current.x1) / 2), current.y1 + (int) Math.ceil((float) (current.y2 - current.y1) / 2), current.y2};
            int[] posDL = {current.x1 + (int) Math.ceil((float) (current.x2 - current.x1) / 2), current.x2, current.y1, current.y1 + (int) Math.ceil((float) (current.y2 - current.y1) / 2)};
            int[] posDR = {current.x1 + (int) Math.ceil((float) (current.x2 - current.x1) / 2), current.x2, current.y1 + (int) Math.ceil((float) (current.y2 - current.y1) / 2), current.y2};
            float[][] quadrantUL = getQuadrant(originalImage, posUL);
            float[][] quadrantUR = getQuadrant(originalImage, posUR);
            float[][] quadrantDL = getQuadrant(originalImage, posDL);
            float[][] quadrantDR = getQuadrant(originalImage, posDR);
            current.UL = new Node(posUL[0], posUL[2], posUL[1], posUL[3], quadrantUL, getAvg(quadrantUL));
            current.UR = new Node(posUR[0], posUR[2], posUR[1], posUR[3], quadrantUR, getAvg(quadrantUR));
            current.DL = new Node(posDL[0], posDL[2], posDL[1], posDL[3], quadrantDL, getAvg(quadrantDL));
            current.DR = new Node(posDR[0], posDR[2], posDR[1], posDR[3], quadrantDR, getAvg(quadrantDR));
            addRecursive(current.UL, quadrantUL, minSize, varianceThreshold, originalImage);
            addRecursive(current.UR, quadrantUR, minSize, varianceThreshold, originalImage);
            addRecursive(current.DL, quadrantDL, minSize, varianceThreshold, originalImage);
            addRecursive(current.DR, quadrantDR, minSize, varianceThreshold, originalImage);

        }
        return current;
    }


    public static class Node {
        int x1, y1, x2, y2;
        float[][] quadrant;
        float avgInQuadrant;
        Node UL;
        Node UR;
        Node DL;
        Node DR;

        Node(int x1, int y1, int x2, int y2, float[][] quadrant, float avgInQuadrant) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.quadrant = quadrant;
            this.avgInQuadrant = avgInQuadrant;
            UL = null;
            UR = null;
            DL = null;
            DR = null;
        }
    }

    private static float[][] getQuadrant(float[][] img, int[] positions){
        int height = positions[1] - positions[0];
        int width = positions[3] - positions[2];
        float[][] result = new float[height][width];
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                result[i][j] = img[i + positions[0]][j + positions[2]];
            }
        }
        return result;
    }

    private static float getAvg(float[][] img){
        float result = 0.f;
        for (float[] floats : img) {
            for (int j = 0; j < img[0].length; j++) {
                result += floats[j];
            }
        }
        result /= (img.length * img[0].length);
        return result;
    }

    private static float getVar(float[][] img) {
        float var = 0;
        float mean = getAvg(img);
        for (float[] floats : img) {
            for (int j = 0; j < img[0].length; j++) {
                var += (floats[j] - mean) * (floats[j] - mean);
            }
        }
        var /= (img.length * img[0].length);
        return var;
    }

    private static float[][] drawRegions(float[][] img, Node tree, float mergeThreshold){
        float[][] result = new float[img.length][img[0].length];
        for (int i =0; i < img.length; i++){
            for (int j = 0; j < img[0].length; j++){
                result[i][j] = img[i][j];
            }
        }
        inorderDraw(tree, result, mergeThreshold, true);
        return result;
    }

    static void inorderDraw(Node temp, float[][] img, float mergeThreshold, boolean drawNext)
    {
        if (temp == null)
            return;
        if (drawNext) {
            for (int i = temp.x1; i < temp.x2; i++) {
                for (int j = temp.y1; j < temp.y2; j++) {
                    if (i == temp.x1 || i == temp.x2 - 1 || j == temp.y1 || j == temp.y2 - 1) {
                        img[i][j * 3] = 1.0f;
                        img[i][j * 3 + 1] = 0.0f;
                        img[i][j * 3 + 2] = 0.0f;
                    }
                }
            }
        }
        inorderDraw(temp.UL, img, mergeThreshold, temp.UL == null || (Math.abs(temp.avgInQuadrant - temp.UL.avgInQuadrant) > mergeThreshold));
        inorderDraw(temp.UR, img, mergeThreshold, temp.UR == null || (Math.abs(temp.avgInQuadrant - temp.UR.avgInQuadrant) > mergeThreshold));
        inorderDraw(temp.DL, img, mergeThreshold, temp.DL == null || (Math.abs(temp.avgInQuadrant - temp.DL.avgInQuadrant) > mergeThreshold));
        inorderDraw(temp.DR, img, mergeThreshold, temp.DR == null || (Math.abs(temp.avgInQuadrant - temp.DR.avgInQuadrant) > mergeThreshold));
    }

    public static float[] findMinMaxVar(float[][] img) {
        float min = 1.f;
        float max = 0.f;
        float currVar;
        for (int i = 0; i < img.length - 10; i++){
            for (int j = 0; j < img[0].length - 10; j ++){
                currVar = getVar(getQuadrant(img, new int[]{i, i + 10, j, j + 10}));
                if (currVar > max){
                    max = currVar;
                }
            }
        }
        for (int i = 0; i < img.length - 2; i++){
            for (int j = 0; j < img[0].length - 2; j ++){
                currVar = getVar(getQuadrant(img, new int[]{i, i + 2, j, j + 2}));
                if (currVar < min){
                    min = currVar;
                }
            }
        }
        return new float[]{min, max};
    }
}
