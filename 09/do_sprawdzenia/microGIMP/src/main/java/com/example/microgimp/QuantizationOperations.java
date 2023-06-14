package com.example.microgimp;

public class QuantizationOperations {

    public static float[][] binaryOtsu(float[][] bits){
        float[] grayHist = ImageOperations.getHistVals(bits.length, bits[0].length, bits, "gray");
        float[][] result = new float[bits.length][bits[0].length];
        int split = suppBinaryOtsu(grayHist);
        return splitting(bits, result, split);
    }

    private static float[][] splitting(float[][] bits, float[][] result, int split) {
        float temp;
        for (int x = 0; x < bits.length; x++){
            for (int y = 0; y < bits[0].length/3; y++){
                temp = Math.round((float) (0.299 * (bits[x][y * 3] * 255) + 0.587 * (bits[x][y * 3 + 1] * 255) + 0.114 * (bits[x][y * 3 + 2] * 255)));
                if (temp <= split){
                    result[x][y*3] = 0;
                    result[x][y*3+1] = 0;
                    result[x][y*3+2] = 0;
                }
                else{
                    result[x][y*3] = 1;
                    result[x][y*3+1] = 1;
                    result[x][y*3+2] = 1;
                }
            }
        }

        return result;
    }

    public static int suppBinaryOtsu(float[] hist){
        int pos = 0;

        float thetaBg;
        float thetaFg;
        float tempBg;
        float mi;
        float miT;
        float var;
        float maxVar = 0.f;
        int threshold;

        for (int i = 0; i < hist.length; i++){
            thetaBg = 0.f;
            tempBg = 0.f;
            mi = 0.f;
            threshold = i;


            for (int j = 0; j < threshold; j++){
                thetaBg += hist[j];
            }
            thetaFg = 1 - thetaBg;

            for (int j = 0; j < threshold; j++){
                tempBg += j * hist[j];
            }
            miT = tempBg;
            for (int j = 0; j < hist.length; j++){
                mi += j * hist[j];
            }
            var = ((miT - mi*thetaBg) * (miT - mi*thetaBg)) / (thetaBg * thetaFg);
            if (var > maxVar){
                maxVar = var;
                pos = threshold;
            }

        }

        return pos;
    }

    public static float[][] binaryKSW(float[][] bits){
        float[] grayHist = ImageOperations.getHistVals(bits.length, bits[0].length, bits, "gray");
        float[][] result = new float[bits.length][bits[0].length];
        int split = suppBinaryKSW(grayHist, bits.length, bits[0].length);
        return splitting(bits, result, split);
    }

    private static int suppBinaryKSW(float[] hist, int height, int width){
        int pos = 0;

        float thetaBg;
        float thetaFg;
        float[] distrBg;
        float[] distrFg;
        float entrBg;
        float entrFg;
        float maxEntr = 0.f;
        int threshold;

        for (int i = 0; i < hist.length; i++) {
            hist[i] *= height * width;
        }
        for (int i = 0; i < hist.length; i++) {
            thetaBg = 0.f;

            threshold = i;
            distrBg = new float[threshold];
            distrFg = new float[256-threshold];

            entrBg = 0.f;
            entrFg = 0.f;


            for (int j = 0; j < threshold; j++) {
                thetaBg += hist[j];
            }
            thetaFg = (width*height) - thetaBg;

            for (int j = 0; j < threshold; j++){
                distrBg[j] = hist[j]/thetaBg;
            }
            for (int j = threshold; j < hist.length; j++){
                distrFg[j - threshold] = hist[j]/thetaFg;
            }

            for (int j = 0; j < threshold; j++){
                if (distrBg[j] > 0) {
                    entrBg += (hist[j] / thetaBg) * Math.log(distrBg[j]);
                }
                else {
                    entrBg += 0;
                }
            }
            entrBg *= -1;
            for (int j = threshold; j < hist.length; j++){
                if (distrFg[j - threshold] > 0) {
                    entrFg += (hist[j] / thetaFg) * Math.log(distrFg[j - threshold]);
                }
                else {
                    entrFg += 0;
                }
            }
            entrFg *= -1;
            if (entrBg + entrFg > maxEntr){
                maxEntr = entrBg + entrFg;
                pos = threshold;
            }

        }

        return pos;
    }

    public static float[][] binaryRC(float[][] bits){
        float[] grayHist = ImageOperations.getHistVals(bits.length, bits[0].length, bits, "gray");
        float[][] result = new float[bits.length][bits[0].length];
        int split = suppBinaryRC(grayHist, bits.length, bits[0].length);
        return splitting(bits, result, split);
    }

    public static int suppBinaryRC(float[] hist, int height, int width) {
        // Compute the initial threshold as the average intensity value
        float sum = 0;
        int numPixels = 0;
        int totalPix = height * width;
        for (int i = 0; i < hist.length; i++) {
            sum += i * hist[i] * totalPix;
            numPixels += hist[i] * totalPix;
        }
        float threshold = sum / numPixels;

        // Iterate until the threshold value stabilizes
        float prevThreshold = -1;
        while (Math.abs(threshold - prevThreshold) > 0.05) {
            // Compute the mean intensity values of the foreground and background classes
            float foregroundSum = 0, backgroundSum = 0;
            int foregroundCount = 0, backgroundCount = 0;
            for (int i = 0; i < hist.length; i++) {
                if (i < threshold) {
                    backgroundSum += i * hist[i] * totalPix;
                    backgroundCount += hist[i] * totalPix;
                } else {
                    foregroundSum += i * hist[i] * totalPix;
                    foregroundCount += hist[i] * totalPix;
                }
            }
            float backgroundMean = (backgroundCount == 0) ? 0 : (backgroundSum / backgroundCount);
            float foregroundMean = (foregroundCount == 0) ? 0 : (foregroundSum / foregroundCount);

            // Update the threshold value
            prevThreshold = threshold;
            threshold = (backgroundMean + foregroundMean) / 2;
        }

        // Return the final threshold value
        return Math.round(threshold);
    }


    public static float[][] manual(float pos, float[][] bits){
        float[][] changed = new float[bits.length][bits[0].length];
        float temp;
        for (int x = 0; x < bits.length; x++){
            for (int y = 0; y < bits[0].length/3; y++){
                temp = (float) (0.299 * (bits[x][y * 3]) + 0.587 * (bits[x][y * 3 + 1]) + 0.114 * (bits[x][y * 3 + 2]));
                if (temp <= pos){
                    changed[x][y*3] = 0;
                    changed[x][y*3+1] = 0;
                    changed[x][y*3+2] = 0;
                }
                else{
                    changed[x][y*3] = 1;
                    changed[x][y*3+1] = 1;
                    changed[x][y*3+2] = 1;
                }
            }
        }

        return changed;
    }
}
