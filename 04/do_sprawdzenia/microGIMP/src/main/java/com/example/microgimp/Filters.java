package com.example.microgimp;

public class Filters {


    private static float[][] blurring(float[][] image, int height, int width, float[][] blurred, float[][] kernel, int starterPoint) {
        for (int y = starterPoint; y < height-starterPoint; y++) {
            for (int x = starterPoint; x < width-starterPoint; x++) {
                float[] pixel = new float[3];
                for (int i = -starterPoint; i <= starterPoint; i++) {
                    for (int j = -starterPoint; j <= starterPoint; j++) {
                        float[] neighbor = getPixel(image, x+i, y+j);
                        for (int k = 0; k < 3; k++) {
                            pixel[k] += neighbor[k] * kernel[i+starterPoint][j+starterPoint];
                        }
                    }
                }
                setPixel(blurred, x, y, pixel);
            }
        }
        return blurred;
    }

    public static float[][] standardBlur(int size, float[][] image) {
        int height = image.length;
        int width = image[0].length / 3;


        float[][] blurred = new float[height][width * 3];

        // Tu trzeba zmienić, żeby kernel był wymiarów size*size
        float[][] kernel = new float[size][size];

        for (int a = 0; a < size; a++){
            for (int b = 0; b < size; b++){
                kernel[a][b] = (float) (1.0/(size*size));
            }
        }

        // Tu startowe y, x, i, j muszą się zaczynać od Math.floorDiv(size, 2) i -Math.floorDiv(size, 2)
        int starterPoint = Math.floorDiv(size, 2);

        // zrefaktorowane do jednej metody, bo tego samego się w gaussie uzywa tylko dla innego kernela
        return blurring(image, height, width, blurred, kernel, starterPoint);
    }



    public static float[][] gaussianBlur(int size, float sigma, float[][] image){
        // sigma (odchylenie standardowe) kontroluje moc blura - im większa, tym bardziej rozmazany
        int height = image.length;
        int width = image[0].length / 3;

        // gaussowy kernel jest liczony tutaj
        float[][] kernel = new float[size][size];
        float sum = 0;
        int r = size / 2;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int x = i - r;
                int y = j - r;
                kernel[i][j] = (float) (Math.exp(-(x * x + y * y) / (2 * sigma * sigma)) / (2 * Math.PI * sigma * sigma));
                sum += kernel[i][j];
            }
        }

        // normalizacja kernela
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                kernel[i][j] /= sum;
            }
        }

        float[][] blurred = new float[height][width * 3];

        int starterPoint = size / 2;
        return blurring(image, height, width, blurred, kernel, starterPoint);
    }

    public static float[][] imageSharpening(float[][]image) {

        int height = image.length;
        int width = image[0].length / 3;

        float[][] sharpenedImage;
        // najpierw potrzeba filtru laplace'a żeby wiedzieć jak wyostrzyć
        sharpenedImage = laplaceFiltering(image);

        // tutaj się wyostrza ten obrazek
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float[] pixel = new float[3];
                float[] originalPixel = getPixel(image, x, y);
                float[] highPassPixel = getPixel(sharpenedImage, x, y);
                for (int k = 0; k < 3; k++) {
                    pixel[k] = originalPixel[k] + highPassPixel[k];
                    pixel[k] = Math.min(1, Math.max(0, pixel[k]));
                }
                setPixel(sharpenedImage, x, y, pixel);
            }
        }

        return sharpenedImage;
    }

    public static float[][] sobelFiltering(float[][] image) {
        // rozmycie gaussa, żeby zredukować szumy i niepotrzebne detale
        float[][] blurredImage = gaussianBlur(3, 1, image);

        int height = image.length;
        int width = image[0].length / 3;

        float[][] filteredImage = new float[height][width * 3];

        // macierze
        float[][] sobelX = {
                {-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}
        };
        float[][] sobelY = {
                {-1, -2, -1},
                {0, 0, 0},
                {1, 2, 1}
        };

        // przez to, że Sobel ma dwie macierze i operacje musimy robić piksel po pikselu to inaczej to trochę wygląda przy tworzeniu
        int starterPoint = 1;
        for (int y = starterPoint; y < height - starterPoint; y++) {
            for (int x = starterPoint; x < width - starterPoint; x++) {
                float[] pixel = new float[3];
                for (int i = -starterPoint; i <= starterPoint; i++) {
                    for (int j = -starterPoint; j <= starterPoint; j++) {
                        float[] neighbor = getPixelWithValidation(blurredImage, x + i, y + j);
                        pixel[0] += neighbor[0] * sobelX[i + starterPoint][j + starterPoint];
                        pixel[1] += neighbor[1] * sobelX[i + starterPoint][j + starterPoint];
                        pixel[2] += neighbor[2] * sobelX[i + starterPoint][j + starterPoint];

                        pixel[0] += neighbor[0] * sobelY[i + starterPoint][j + starterPoint];
                        pixel[1] += neighbor[1] * sobelY[i + starterPoint][j + starterPoint];
                        pixel[2] += neighbor[2] * sobelY[i + starterPoint][j + starterPoint];
                    }
                }
                setPixelWithValidation(filteredImage, x, y, pixel);
            }
        }

        return filteredImage;
    }

    public static float[][] prewittFiltering(float[][] image) {
        // rozmycie gaussa, żeby zredukować szumy i niepotrzebne detale
        float[][] blurredImage = gaussianBlur(3, 1, image);

        int height = image.length;
        int width = image[0].length / 3;

        float[][] filteredImage = new float[height][width * 3];

        // filtr Prewitta dla poziomej krawędzi
        float[][] horizontalFilter = {
                {-1, 0, 1},
                {-1, 0, 1},
                {-1, 0, 1}
        };

        // filtr Prewitta dla pionowej krawędzi
        float[][] verticalFilter = {
                {-1, -1, -1},
                {0, 0, 0},
                {1, 1, 1}
        };

        //tutaj jest robione filtrowanie Prewitta
        int starterPoint = 1;
        for (int y = starterPoint; y < height - starterPoint; y++) {
            for (int x = starterPoint; x < width - starterPoint; x++) {
                float[] horizontalPixel = new float[3];
                float[] verticalPixel = new float[3];
                for (int i = -starterPoint; i <= starterPoint; i++) {
                    for (int j = -starterPoint; j <= starterPoint; j++) {
                        float[] neighbor = getPixelWithValidation(blurredImage, x + i, y + j);
                        for (int k = 0; k < 3; k++) {
                            horizontalPixel[k] += neighbor[k] * horizontalFilter[i + starterPoint][j + starterPoint];
                            verticalPixel[k] += neighbor[k] * verticalFilter[i + starterPoint][j + starterPoint];
                        }
                    }
                }
                float[] pixel = new float[3];
                for (int k = 0; k < 3; k++) {
                    pixel[k] = (float) Math.sqrt(Math.pow(horizontalPixel[k], 2) + Math.pow(verticalPixel[k], 2));
                }
                setPixelWithValidation(filteredImage, x, y, pixel);
            }
        }
        return filteredImage;
    }

    public static float[][] robertsFiltering(float[][] image) {
        // rozmycie gaussa, żeby zredukować szumy i niepotrzebne detale
        float[][] blurredImage = gaussianBlur(3, 1, image);

        int height = image.length;
        int width = image[0].length / 3;

        float[][] filteredImage = new float[height][width * 3];

        // macierze do robertsa
        float[][] horizontalFilter = {{1, 0}, {0, -1}};
        float[][] verticalFilter = {{0, 1}, {-1, 0}};

        // i tutaj też więcej pętli, bo więcej macierzy (osobno pion i poziom)
        int starterPoint = 1;
        for (int y = starterPoint; y < height - starterPoint; y++) {
            for (int x = starterPoint; x < width - starterPoint; x++) {
                float[] pixel = new float[3];
                // tutaj jest poziom
                float[] horizontalResult = new float[3];
                calculateHorizontalOrVerticalFilter(blurredImage, horizontalFilter, y, x, horizontalResult);
                // tutaj jest pion
                float[] verticalResult = new float[3];
                calculateHorizontalOrVerticalFilter(blurredImage, verticalFilter, y, x, verticalResult);
                // tutaj łączymy pion z poziomem
                for (int k = 0; k < 3; k++) {
                    pixel[k] = (float) Math.sqrt(Math.pow(horizontalResult[k], 2) + Math.pow(verticalResult[k], 2));
                }
                setPixelWithValidation(filteredImage, x, y, pixel);
            }
        }
        return filteredImage;
    }




    public static float[][] laplaceFiltering(float[][]image) {
        // rozmycie gaussa, żeby zredukować szumy i niepotrzebne detale

        float[][] blurredImage = gaussianBlur(3, 1, image);

        int height = image.length;
        int width = image[0].length / 3;

        float[][] filteredImage = new float[height][width * 3];

        // filtr laplace'a
        float[][] laplacianFilter = {
                {0, -1, 0},
                {-1, 4, -1},
                {0, -1, 0}
        };

        //tutaj jest robione filtrowanie laplace'a
        int starterPoint = 1;
        return calculateLaplacianFilter(blurredImage, height, width, filteredImage, laplacianFilter, starterPoint);
    }




    public static float[][] laplacianOfGaussianFiltering(float[][] image) {
        // rozmycie gaussa, żeby zredukować szumy i niepotrzebne detale
        float[][] blurredImage = gaussianBlur(3, 1, image);

        int height = image.length;
        int width = image[0].length / 3;

        float[][] filteredImage = new float[height][width * 3];

        // macierz Laplacian of Gaussian
        float[][] logFilter = {
                {0, 0, -1, 0, 0},
                {0, -1, -2, -1, 0},
                {-1, -2, 16, -2, -1},
                {0, -1, -2, -1, 0},
                {0, 0, -1, 0, 0}
        };

        int starterPoint = logFilter.length / 2;

        return calculateLaplacianFilter(blurredImage, height, width, filteredImage, logFilter, starterPoint);
    }

    private static void calculateHorizontalOrVerticalFilter(float[][] blurredImage, float[][] horizontalFilter, int y, int x, float[] horizontalResult) {
        for (int i = -1; i <= 0; i++) {
            for (int j = -1; j <= 0; j++) {
                float[] neighbor = getPixelWithValidation(blurredImage, x + j, y + i);
                for (int k = 0; k < 3; k++) {
                    horizontalResult[k] += neighbor[k] * horizontalFilter[i + 1][j + 1];
                }
            }
        }
    }

    private static float[][] calculateLaplacianFilter(float[][] blurredImage, int height, int width, float[][] filteredImage, float[][] laplacianFilter, int starterPoint) {
        for (int y = starterPoint; y < height - starterPoint; y++) {
            for (int x = starterPoint; x < width - starterPoint; x++) {
                float[] pixel = new float[3];
                for (int i = -starterPoint; i <= starterPoint; i++) {
                    for (int j = -starterPoint; j <= starterPoint; j++) {
                        float[] neighbor = getPixelWithValidation(blurredImage, x + i, y + j);
                        for (int k = 0; k < 3; k++) {
                            pixel[k] += neighbor[k] * laplacianFilter[i + starterPoint][j + starterPoint];
                        }
                    }
                }
                setPixelWithValidation(filteredImage, x, y, pixel);
            }
        }
        return filteredImage;
    }

    private static float[] getPixelWithValidation(float[][] image, int x, int y) {
        int width = image[0].length / 3;
        int height = image.length;
        if (x < 0 || x >= width || y < 0 || y >= height) {
            // return black pixel if outside of image boundaries
            return new float[] {0.0f, 0.0f, 0.0f};
        }
        float r = Math.max(0.0f, Math.min(1.0f, image[y][(x * 3)]));
        float g = Math.max(0.0f, Math.min(1.0f, image[y][(x * 3) + 1]));
        float b = Math.max(0.0f, Math.min(1.0f, image[y][(x * 3) + 2]));
        return new float[] {r, g, b};
    }


    private static void setPixelWithValidation(float[][] image, int x, int y, float[] pixel) {
        int width = image[0].length / 3;
        int height = image.length;
        if (x < 0 || x >= width || y < 0 || y >= height) {
            // do nothing if outside of image boundaries
            return;
        }
        image[y][(x * 3)] = Math.max(0.0f, Math.min(1.0f, pixel[0]));
        image[y][(x * 3) + 1] = Math.max(0.0f, Math.min(1.0f, pixel[1]));
        image[y][(x * 3) + 2] = Math.max(0.0f, Math.min(1.0f, pixel[2]));
    }





    // tutaj też intelliJ podpowiedział, że można uprościć
    private static float[] getPixel(float[][] image, int x, int y) {
        float[] pixel = new float[3];
        System.arraycopy(image[y], x * 3, pixel, 0, 3);
        return pixel;
    }

    private static void setPixel(float[][] image, int x, int y, float[] pixel) {
        System.arraycopy(pixel, 0, image[y], x * 3, 3);
    }
}
