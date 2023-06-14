package com.example.microgimp;

public class ConverterRGB {
    public static float[][] RGBtoHSL(int height, int width, float[][] bits) {
        float[][] inHSL = new float[height][width];

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y += 3) {
                float red = bits[x][y];
                float green = bits[x][y + 1];
                float blue = bits[x][y + 2];
                float max = Math.max(Math.max(red, green), blue);
                float min = Math.min(Math.min(red, green), blue);
                float c = max - min;

                float h = 0.f;

                if (c != 0) {
                    if (max == red) {
                        h = (green - blue) / c;
                        if (h < 0) h += 6.f;
                    } else if (max == green) {
                        h = (blue - red) / c + 2.f;
                    } else if (max == blue) {
                        h = (red - green) / c + 4.f;
                    }
                    h *= 60.f;
                }

                float l = (max + min) * 0.5f;

                float s = c == 0 ? 0.f : c / (1 - Math.abs(2.f * l - 1.f));

                inHSL[x][y] = h;
                inHSL[x][y + 1] = s;
                inHSL[x][y + 2] = l;
            }
        }
        return inHSL;
    }


    public static float[][] HSLtoRGB(int height, int width, float[][] bits){
        float[][] inRGB = new float[height][width];
        for (int x = 0; x < height; x++) {
            int baseIndex = x * width;
            for (int y = 0; y < width; y += 3) {
                float H = bits[x][y];
                float S = bits[x][y + 1];
                float L = bits[x][y + 2];
                if (L < 0.05f){
                    L = 0.05f;
                }
                inRGB[x][y] = HSLtoRGBsupport(0, H, S, L);
                inRGB[x][y + 1] = HSLtoRGBsupport(8, H, S, L);
                inRGB[x][y + 2] = HSLtoRGBsupport(4, H, S, L);
            }
        }
        return inRGB;
    }

    public static float HSLtoRGBsupport(int number, float H, float Sl, float L){
        float k = (number + H/30.0f) % 12.0f;
        float a = Sl * Math.min(L, 1.0f - L);
        float result = L - a * Math.max(-1.0f, Math.min(k - 3.0f, Math.min(9.0f - k, 1.0f)));
        return Math.max(result, 0.0f);
    }

}
