package com.example.microgimp;

public class CornerDetection {
    public static float[][] detectCorner(float[][] img, int minDist, int threshold){
        int height = img.length;
        int width = img[0].length;
        float[][] result = new float[height][width];
        for (int i = 0; i < result.length; i++) {
            System.arraycopy(img[i], 0, result[i], 0, result[0].length);
        }

        float[][] gray = EdgeDetection.reduceMatrix(EdgeDetection.turnGrayscale(result));

        float[][] sobelXX = EdgeDetection.sobelFilteringGetX(EdgeDetection.sobelFilteringGetX(gray));
        float[][] sobelXY = EdgeDetection.sobelFilteringGetY(EdgeDetection.sobelFilteringGetX(gray));
        float[][] sobelYY = EdgeDetection.sobelFilteringGetY(EdgeDetection.sobelFilteringGetY(gray));

        float[][] det = new float[sobelXX.length][sobelXX[0].length];
        float[][] trace = new float[sobelXX.length][sobelXX[0].length];
        float[][] harris = new float[sobelXX.length][sobelXX[0].length];
        for (int i = minDist; i < sobelXX.length - minDist; i++) {
            for (int j = minDist; j < sobelXX[0].length - minDist; j++) {
                for (int x = -minDist; x < minDist; x++){
                    for (int y = -minDist; y < minDist; y++){
                        det[i][j] += (sobelXX[i+x][j+y] * sobelYY[i+x][j+y]) - (sobelXY[i+x][j+y] * sobelXY[i+x][j+y]);
                        trace[i][j] += sobelXX[i+x][j+y] + sobelYY[i+x][j+y];
                        harris[i][j] += (float) (det[i+x][j+y] - 0.2 * trace[i+x][j+y]);
                    }
                }
            }
        }
        normaliseMatrix(harris);
        int posX;
        int posY1;
        int posY2;
        int posY3;
        for (int i = 0; i < sobelXX.length; i++) {
            for (int j = 0; j < sobelXX[0].length; j++) {
                if (harris[i][j] > threshold){
                    for (int x = -5; x < 5; x++){
                        for (int y = -5; y < 5; y++){
                            posX = i+x;
                            posY1 = ((j+y)*3);
                            posY2 = ((j+y)*3) + 1;
                            posY3 = ((j+y)*3) + 2;
                            if ((posX >=0 && posX < result.length) && (posY1 >= 0 && posY3 < result[0].length)) {
                                result[posX][posY1] = 1;
                                result[posX][posY2] = 0.34f;
                                result[posX][posY3] = 0.2f;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }


   static void normaliseMatrix(float[][] matrix){
        float max = 0;
       for (float[] floats : matrix) {
           for (int j = 0; j < matrix[0].length; j++) {
               if (Math.abs(floats[j]) > max) {
                   max = Math.abs(floats[j]);
               }
           }
       }
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] /= max;
                matrix[i][j] *= 100;
            }
        }
    }
}
