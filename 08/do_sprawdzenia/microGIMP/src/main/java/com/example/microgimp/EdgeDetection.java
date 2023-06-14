package com.example.microgimp;

import java.util.stream.IntStream;

public class EdgeDetection {

    public static float[][] cannyAlgorithm(float[][] bits, float sigma, float thresholdLow, float thresholdHigh) {
        // Step 1: Gaussian smoothing
        float[][] inGrayscale = turnGrayscale(bits);
        float[][] smoothedImage = Filters.gaussianBlur(5, sigma, inGrayscale);
        float[][] reduced = reduceMatrix(smoothedImage);
        // Step 2: Sobel filtering
        float[][] sobelX = sobelFilteringGetX(reduced);
        float[][] sobelY = sobelFilteringGetY(reduced);

        float[][] magnitudeMatrix = getMagnitudeMatrix(reduced, sobelX, sobelY);
        float[][] angleMatrix = getAngleMatrix(reduced, sobelX, sobelY);
        float magMin = 100.f;
        float magMax = -100.f;
        for (int i = 0; i < angleMatrix.length; i++){
            for (int j = 0; j < angleMatrix[0].length; j++){
                if (magnitudeMatrix[i][j] < magMin){
                    magMin = magnitudeMatrix[i][j];
                }
                if (magnitudeMatrix[i][j] > magMax){
                    magMax = magnitudeMatrix[i][j];
                }
            }
        }
        for (int i = 0; i < magnitudeMatrix.length; i++) {
            for (int j = 0; j < magnitudeMatrix[0].length; j++){
                magnitudeMatrix[i][j] /= magMax;
            }
        }


        float[][] nms = nonMaxSuppression(magnitudeMatrix, angleMatrix);
        float[][] thresholded = doubleThreshold(nms, thresholdLow, thresholdHigh);




        return expandMatrix(thresholded);
    }


    public static float[][] sobelFilteringGetX(float[][] image) {
        int height = image.length;
        int width = image[0].length;
        float[][] filteredImage = new float[height][width];

        // pre-compute pixel weights for each neighbor
        float[][] sobelX = {
                {-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}
        };
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                float pixel = 0.f;
                float neighbor1 = image[y-1][x-1];
                float neighbor2 = image[y-1][x];
                float neighbor3 = image[y-1][x+1];
                float neighbor4 = image[y][x-1];
                float neighbor6 = image[y][x+1];
                float neighbor7 = image[y+1][x-1];
                float neighbor8 = image[y+1][x];
                float neighbor9 = image[y+1][x+1];
                pixel += neighbor1 * sobelX[0][0];
                pixel += neighbor2 * sobelX[0][1];
                pixel += neighbor3 * sobelX[0][2];
                pixel += neighbor4 * sobelX[1][0];
                pixel += neighbor6 * sobelX[1][2];
                pixel += neighbor7 * sobelX[2][0];
                pixel += neighbor8 * sobelX[2][1];
                pixel += neighbor9 * sobelX[2][2];
                filteredImage[y][x] = pixel;
            }
        }
        return filteredImage;
    }



    public static float[][] sobelFilteringGetY(float[][] image) {
        int height = image.length;
        int width = image[0].length;

        float[][] filteredImage = new float[height][width];

        // macierze
        float[][] sobelY = {
                {-1, -2, -1},
                {0, 0, 0},
                {1, 2, 1}
        };

        // starterPoint is always 1 for Sobel
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                float pixel = 0.f;
                float neighbor1 = image[y-1][x-1];
                float neighbor2 = image[y-1][x];
                float neighbor3 = image[y-1][x+1];
                float neighbor4 = image[y][x-1];
                float neighbor6 = image[y][x+1];
                float neighbor7 = image[y+1][x-1];
                float neighbor8 = image[y+1][x];
                float neighbor9 = image[y+1][x+1];
                pixel += neighbor1 * sobelY[0][0];
                pixel += neighbor2 * sobelY[0][1];
                pixel += neighbor3 * sobelY[0][2];
                pixel += neighbor4 * sobelY[1][0];
                pixel += neighbor6 * sobelY[1][2];
                pixel += neighbor7 * sobelY[2][0];
                pixel += neighbor8 * sobelY[2][1];
                pixel += neighbor9 * sobelY[2][2];
                filteredImage[y][x] = pixel;
            }
        }

        return filteredImage;
    }


    public static float[][] turnGrayscale(float[][] image) {
        float[][] result = new float[image.length][image[0].length];
        for (int x = 0; x < image.length; x++) {
            for (int y = 0; y < image[0].length / 3; y++) {
                float r = image[x][y * 3];
                float g = image[x][y * 3 + 1];
                float b = image[x][y * 3 + 2];
                float avg = (float) (0.299 * r + 0.587 * g + 0.114 * b);
                result[x][y*3] = avg;
                result[x][y*3 + 1] = avg;
                result[x][y*3 + 2] = avg;
            }
        }
        return result;
    }


    public static float[][] reduceMatrix(float[][] image) {
        int height = image.length;
        int width = image[0].length / 3;

        float[][] result = new float[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                result[i][j] = image[i][j*3];
            }
        }

        return result;
    }

    public static float[][] expandMatrix(float[][] image) {
        int height = image.length;
        int width = image[0].length * 3;

        float[][] result = new float[height][width];

        IntStream.range(0, height).parallel().forEach(i -> {
            for (int j = 0; j < width / 3; j++) {
                float pixel = image[i][j];
                result[i][j*3] = pixel;
                result[i][j*3+1] = pixel;
                result[i][j*3+2] = pixel;
            }
        });

        return result;
    }


    private static float[][] getMagnitudeMatrix(float[][] image, float[][] sobelX, float[][] sobelY){
        int height = image.length;
        int width = image[0].length;
        float[][] result = new float[height][width];
        IntStream.range(0, height).parallel().forEach(i -> {
            IntStream.range(0, width).forEach(j -> {
                result[i][j] = (float) Math.sqrt(sobelX[i][j] * sobelX[i][j] + sobelY[i][j] * sobelY[i][j]);
            });
        });

        return  result;
    }

    private static float[][] getAngleMatrix(float[][] image, float[][] sobelX, float[][] sobelY){
        int height = image.length;
        int width = image[0].length;
        float[][] result = new float[height][width];
        IntStream.range(0, height).parallel().forEach(i -> {
            IntStream.range(0, width).forEach(j -> {
                result[i][j] = (float) Math.atan2(sobelY[i][j], sobelX[i][j]);
            });
        });

        return  result;
    }

    private static float[][] nonMaxSuppression(float[][] image, float[][] dirMatrix){
        int height = image.length;
        int width = image[0].length;
        float[][] result = new float[height][width];
        float neighbOne, neighbTwo;
        IntStream.range(0, height).parallel().forEach(i -> {
            IntStream.range(0, width).forEach(j -> {
                result[i][j] = 0.f;
                dirMatrix[i][j] = (float) (dirMatrix[i][j] * 180 / Math.PI);
                if (dirMatrix[i][j] < 0){
                    dirMatrix[i][j] += 180;
                }
            });
        });

        for (int i = 1; i < height - 1; i++){
            for (int j = 1; j < width - 1; j++){
                neighbOne = 0.f;
                neighbTwo = 0.f;
                float curr = dirMatrix[i][j];
                if ((curr >= 0 && curr < 22.5) || (curr >= 157.5 && curr <= 180)){
                    neighbOne = image[i][j+1];
                    neighbTwo = image[i][j-1];
                }
                else if (curr >= 22.5 && curr < 67.5){
                    neighbOne = image[i+1][j-1];
                    neighbTwo = image[i-1][j+1];
                }
                else if (curr >= 67.5 && curr < 112.5){
                    neighbOne = image[i+1][j];
                    neighbTwo = image[i-1][j];
                }
                else if (curr >= 112.5 && curr < 157.5){
                    neighbOne = image[i-1][j-1];
                    neighbTwo = image[i+1][j+1];
                }
                if (image[i][j] >= neighbOne && image[i][j] >= neighbTwo){
                    result[i][j] = image[i][j];
                }
            }
        }
        return result;
    }

    private static float[][] doubleThreshold(float[][] image, float lowThresholdRatio, float highThresholdRatio){
        int height = image.length;
        int width = image[0].length;
        float[][] result = new float[height][width];
        float maxVal = 1.f;
        for (float[] floats : image) {
            for (int j = 0; j < width; j++) {
                if (floats[j] > maxVal) {
                    maxVal = floats[j];
                }
            }
        }

        float highThreshold = maxVal * highThresholdRatio;
        float lowThreshold = highThreshold * lowThresholdRatio;

        IntStream.range(0, height).parallel().forEach(i -> {
            IntStream.range(0, width).forEach(j -> {
                if (image[i][j] <= lowThreshold || image[i][j] >= highThreshold){
                    result[i][j] = 0.f;
                }
                else{
                    result[i][j] = 1.f;
                }
            });
        });
        return result;
    }
}