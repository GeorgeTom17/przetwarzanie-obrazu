package com.example.microgimp;

public class HoughTransform {
    public static float[][] houghTransform(float[][] bits, boolean showHoughSpace, int threshold) {
        float[][] result = new float[bits.length][bits[0].length];
        for (int x = 0; x < bits.length; x++) {
            System.arraycopy(bits[x], 0, result[x], 0, bits[0].length);
        }
        int height = bits.length;
        int width = bits[0].length / 3;
        float[][] img = EdgeDetection.cannyAlgorithm(bits, 5, 0.05f, 0.95f);
        float[][] reduced = EdgeDetection.reduceMatrix(img);
        int thetaMax = 180;
        int rMax = (int) Math.ceil(Math.hypot(width, height));
        float rStep = (float) rMax / height;
        float thetaStep = (float) thetaMax / width;
        float[][] houghSpace = new float[height][width];
        float maxAcc = 0;
        float[][][] linesParams = new float[height][width][1];
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                linesParams[x][y][0] = 500.f;
            }
        }
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                float pixel = reduced[x][y];
                if (pixel == 1.f) { // Only consider white pixels
                    for (int thetaIndex = 0; thetaIndex < width; thetaIndex++) {
                        double theta = thetaIndex * thetaStep;
                        double r = x * Math.cos(Math.toRadians(theta)) + y * Math.sin(Math.toRadians(theta));
                        int rIndex = (int) ((int) (Math.round(r * ((float) (rMax >>> 1) / rMax)) + (rMax >>> 1)) / rStep);
                        if (rIndex >= rMax) {
                            rIndex = rMax - 1;
                        } else if (rIndex < 0) {
                            rIndex = 0;
                        }
                        houghSpace[rIndex][thetaIndex] += 1;
                        if (houghSpace[rIndex][thetaIndex] > maxAcc) {
                            maxAcc = houghSpace[rIndex][thetaIndex];
                        }
                        if (houghSpace[rIndex][thetaIndex] > threshold) {
                            linesParams[x][y][0] = (float) theta;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < houghSpace.length; i++) {
            for (int j = 0; j < houghSpace[0].length; j++) {
                houghSpace[i][j] /= maxAcc;
                houghSpace[i][j] *= 1000;
                if (houghSpace[i][j] <= threshold) {
                    houghSpace[i][j] = 0.f;
                }
            }
        }


        double currTan;
        double currTheta;
        int currFunVal;
        if (showHoughSpace) {
            for (int i = 0; i < houghSpace.length; i++) {
                for (int j = 0; j < houghSpace[0].length; j++) {
                    houghSpace[i][j] /= 1000;
                }
            }
            return EdgeDetection.expandMatrix(houghSpace);
        } else {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (linesParams[i][j][0] != 500.f) {
                        currTheta = linesParams[i][j][0];
                        if (currTheta <= Math.PI/4 || currTheta >= 3*Math.PI/4) {
                            // Draw vertical lines
                            currTan = Math.tan(currTheta);
                            for (int k = 0; k < width; k++) {
                                // y = currTan * k + (j - i * currTan)
                                currFunVal = Math.round((float) (currTan * k + (j - i * currTan)));
                                if (currFunVal >= 0 && currFunVal < height) {
                                    result[currFunVal][k * 3] = 0.f;
                                    result[currFunVal][k * 3 + 1] = 0.f;
                                    result[currFunVal][k * 3 + 2] = 0.f;
                                }
                            }
                        } else {
                            // Draw horizontal lines
                            currTan = Math.tan(currTheta + Math.PI/2);
                            for (int k = 0; k < height; k++) {
                                // x = currTan * k + (i - k * currTan)
                                currFunVal = Math.round((float) (currTan * k + (i - k * currTan)));
                                if (currFunVal >= 0 && currFunVal < width*3) {
                                    result[k][currFunVal * 3] = 0.f;
                                    result[k][currFunVal * 3 + 1] = 0.f;
                                    result[k][currFunVal * 3 + 2] = 0.f;
                                }
                            }
                        }
                    }

                }
            }
            return result;
        }


    }
}