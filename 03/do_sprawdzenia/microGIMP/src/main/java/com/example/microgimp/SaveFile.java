package com.example.microgimp;

import javafx.stage.FileChooser;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveFile {
    public static void save(float[][] pixels) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PBM file", "*.pbm"),
                new FileChooser.ExtensionFilter("PGM file", "*.pgm"),
                new FileChooser.ExtensionFilter("PPM file", "*.ppm")
        );

        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            String fileName = selectedFile.toString();
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            int rows = pixels.length;
            int columns = pixels[0].length / 3;
            int mbv = 255;

            try (BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(selectedFile))) {
                if (extension.equals("pbm")) {
                    writeP1orP4(writer, pixels, rows, columns, getFileType(pixels));
                } else if (extension.equals("pgm")) {
                    writeP2orP5(writer, pixels, rows, columns, mbv, getFileType(pixels));
                } else if (extension.equals("ppm")) {
                    writeP3orP6(writer, pixels, rows, columns, mbv, getFileType(pixels));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getFileType(float[][] pixels) {
        if (pixels[0].length == 1) {
            return "P1";
        } else if (pixels[0].length == 3) {
            return "P3";
        } else if (pixels[0].length == 4) {
            return "P4";
        } else {
            return "P6";
        }
    }

    private static void writeP1orP4(BufferedOutputStream writer, float[][] pixels, int rows, int columns, String type) throws IOException {
        writer.write((type + "\n").getBytes());
        writer.write((columns + " " + rows + "\n").getBytes());

        if (type.equals("P1")) {
            // For P1 files, write 1s and 0s instead of bits
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (pixels[i][j * 3] >= 0.5) {
                        writer.write("1 ".getBytes());
                    } else {
                        writer.write("0 ".getBytes());
                    }
                }
                writer.write("\n".getBytes());
            }
        } else {
            // For P4 files, write bits as before
            int bitIndex = 0;
            byte bit = 0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (pixels[i][j * 3] >= 0.5) {
                        bit |= (1 << (7 - (bitIndex % 8)));
                    }
                    bitIndex++;

                    if (bitIndex % 8 == 0) {
                        writer.write(bit);
                        bit = 0;
                    }
                }
            }
            if (bitIndex % 8 != 0) {
                writer.write(bit);
            }
        }
    }
    private static void writeP2orP5(BufferedOutputStream writer, float[][] pixels, int rows, int columns, int mbv, String type) throws IOException {
        writer.write((type + "\n").getBytes());
        writer.write((columns + " " + rows + "\n").getBytes());
        writer.write((mbv + "\n").getBytes());

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int pixelValue = (int) (pixels[i][j * 3] * mbv);
                writer.write((pixelValue + "\n").getBytes());
            }
        }
    }

    private static void writeP3orP6(BufferedOutputStream writer, float[][] pixels, int rows, int columns, int mbv, String type) throws IOException {
        writer.write((type + "\n").getBytes());
        writer.write((columns + " " + rows + "\n").getBytes());
        writer.write((mbv + "\n").getBytes());

        if (type.equals("P3")) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    int red = (int) (pixels[i][j * 3] * mbv);
                    int green = (int) (pixels[i][j * 3 + 1] * mbv);
                    int blue = (int) (pixels[i][j * 3 + 2] * mbv);
                    writer.write((red + " " + green + " " + blue + " ").getBytes());
                }
                writer.write("\n".getBytes());
            }
        } else if (type.equals("P6")) {
            byte[] rgbBytes = new byte[3 * columns];
            for (int i = 0; i < rows; i++) {
                int index = 0;
                for (int j = 0; j < columns; j++) {
                    int red = (int) (pixels[i][j * 3] * mbv);
                    int green = (int) (pixels[i][j * 3 + 1] * mbv);
                    int blue = (int) (pixels[i][j * 3 + 2] * mbv);
                    rgbBytes[index++] = (byte) red;
                    rgbBytes[index++] = (byte) green;
                    rgbBytes[index++] = (byte) blue;
                }
                writer.write(rgbBytes);
            }
        } else {
            throw new IllegalArgumentException("Invalid PPM image type: " + type);
        }
    }

}