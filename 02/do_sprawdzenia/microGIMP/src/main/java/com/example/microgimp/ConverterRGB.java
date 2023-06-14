package com.example.microgimp;

public class ConverterRGB {
    public static float[][] rgbToHSL(int height, int width, float[][] bits, float desat, float light, float sat, String contrast, float contrVal){
        float red;
        float green;
        float blue;
        float[][] desaturated = new float [height][width];
        float Cmax;
        float Cmin;
        float delta;
        float H = 0;
        float H1;
        float Sl;
        float L;
        float m;
        float chroma;
        float X;
        //konwersja do hsl
        for (int x = 0; x < height; x++){
            for (int y = 0; y < width / 3; y++){
                red = bits[x][y * 3];
                green = bits[x][y * 3 + 1];
                blue = bits[x][y * 3 + 2];
                Cmax = Math.max(Math.max(red, green), blue);
                Cmin = Math.min(Math.min(red, green), blue);
                delta = Cmax - Cmin;
                L = (Cmax + Cmin) / 2;

                if (delta == 0){
                    H = 0;
                }
                else if (Cmax == red){
                    H = 60 * (((green - blue) / delta) % 6);
                }
                else if (Cmax == green){
                    H = 60 * (((blue - red) / delta) + 2);
                } else if (Cmax == blue) {
                    H = 60 * ((red - green) / delta + 4);
                }

                if (L == 1 || L == 0){
                    Sl = 0;
                }
                else {
                    Sl = (Cmax - L) / (Math.min(L, 1 - L)); //tutaj ustawiasz jak bardzo desaturować (Przez ile dzielić ????)
                }

                if (desat >= 0 && desat <= 1){
                    Sl -= Sl * desat;
                }
                if (sat >= 0 && sat <= 1){
                    float diff = 1 - Sl;
                    Sl += diff * sat;
                }
                if (contrVal >= 0 && contrVal <= 1){
                    if (contrast.equals("pow")){
                        L = (float) Math.pow(L, contrVal);
                    }
                    else if(contrast.equals("log")){
                        L = (float) Math.pow(L, 1 / contrVal);
                    }
                }
                else {
                    if (light >= 0 && light <= 1) {
                        L = light; //tutaj ustawiasz lightness (Przez ile dzielić ????)
                    }
                }

                chroma = (1 - Math.abs(2*L - 1)) * Sl;
                H1 = H/60;
                X = chroma * (1 - Math.abs(H1 % 2 - 1));
                m = L - chroma/2;
                if (H1 >= 0 && H1 < 1){
                    desaturated[x][y * 3] = chroma + m;
                    desaturated[x][y * 3 + 1] = X + m;
                    desaturated[x][y * 3 + 2] = m;
                }
                if (H1 >= 1 && H1 < 2){
                    desaturated[x][y * 3] = X + m;
                    desaturated[x][y * 3 + 1] = chroma + m;
                    desaturated[x][y * 3 + 2] = m;
                }
                if (H1 >= 2 && H1 < 3){
                    desaturated[x][y * 3] = m;
                    desaturated[x][y * 3 + 1] = chroma + m;
                    desaturated[x][y * 3 + 2] = X + m;
                }
                if (H1 >= 3 && H1 < 4){
                    desaturated[x][y * 3] = m;
                    desaturated[x][y * 3 + 1] = X + m;
                    desaturated[x][y * 3 + 2] = chroma + m;
                }
                if (H1 >= 4 && H1 < 5){
                    desaturated[x][y * 3] = X + m;
                    desaturated[x][y * 3 + 1] = m;
                    desaturated[x][y * 3 + 2] = chroma + m;
                }
                if (H1 >= 5 && H1 < 6){
                    desaturated[x][y * 3] = chroma + m;
                    desaturated[x][y * 3 + 1] = m;
                    desaturated[x][y * 3 + 2] = X + m;
                }
            }
        }
        return desaturated;

    }
}
