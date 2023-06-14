package com.example.microgimp;

import java.io.*;
import java.util.Objects;


public class ReadFile {

    public static float[][] main() {
        File readFile = OpenFile.getFile("p*m");
        String filename = readFile.getAbsolutePath();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            int rows, columns, mbv = 255;
            String type = reader.readLine(); //P1-6
            reader.readLine(); //pomijam komentarz
            String[] size = reader.readLine().split(" "); //wymiary
            columns = Integer.parseInt(size[0]);
            rows = Integer.parseInt(size[1]);
            if (!Objects.equals(type, "P1") && !Objects.equals(type, "P4")) {
                mbv = Integer.parseInt(reader.readLine()); //odcienie szarości i kolorowe mają podaną najwyższą wartość, jaka będzie w dokumencie
            }
            float[][] bits = new float[rows][columns * 3];
            if (type.equals("P1")){
                bits = readP1(reader, rows, columns);
            }
            if (type.equals("P2")){
                bits = readP2(reader, rows, columns, mbv);
            }
            if (type.equals("P3")){
                bits = readP3(reader, rows, columns, mbv);
            }
            if (type.equals("P4")){
                bits = readP4(rows, columns, filename);
            }
            if (type.equals("P5")){
                bits = readP5(rows, columns, filename, mbv);
            }
            if (type.equals("P6")){
                bits = readP6(rows, columns, filename, mbv);
            }
            return bits;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private static float[][] readP1(BufferedReader reader, int rows, int columns) throws IOException {
        float[][] bits = new float[rows][columns * 3];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int nextChar = reader.read();
                while (nextChar == '\n' || nextChar == '\r') { // skip newline characters
                    nextChar = reader.read();
                }
                int next = nextChar - '0';
                bits[i][j * 3] = (float) next;
                bits[i][j * 3 + 1] = (float) next;
                bits[i][j * 3 + 2] = (float) next;
            }
        }
        return bits;
    }

    private static float[][] readP2(BufferedReader reader, int rows, int columns, int mbv) throws IOException {
        float[][] bits = new float[rows][columns * 3];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int nextChar = Integer.parseInt(reader.readLine());
                bits[i][j * 3] = (float) nextChar/mbv;
                bits[i][j * 3 + 1] = (float) nextChar/mbv;
                bits[i][j * 3 + 2] = (float) nextChar/mbv;
            }
        }
        return bits;
    }

    private static float[][] readP3(BufferedReader reader, int rows, int columns, int mbv) throws IOException {
        float[][] pixels = new float[rows][columns * 3];
        int totalPixels = rows * columns * 3;
        int[] pixelValues = new int[totalPixels];
        int pixelIndex = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#")) {
                continue; //skip comments
            }
            String[] tokens = line.split("\\s+");
            for (String token : tokens) {
                pixelValues[pixelIndex++] = Integer.parseInt(token);
                if (pixelIndex == totalPixels) {
                    break; //stop reading after all pixels have been read
                }
            }
            if (pixelIndex == totalPixels) {
                break; //stop reading after all pixels have been read
            }
        }
        for (int i = 0; i < totalPixels; i++) {
            pixels[i / (columns * 3)][i % (columns * 3)] = (float) pixelValues[i] / mbv;
        }
        return pixels;
    }

    private static float[][] readP4(int rows, int columns, String filepath) throws IOException {
        FileInputStream fis = new FileInputStream(filepath);
        DataInputStream dis = new DataInputStream(fis);
        for (int i = 0; i < 3; i++) {
            char c;
            do {
                c = (char) (dis.readUnsignedByte());
            } while (c != '\n');
        }
        float[][] temp = new float[rows][columns];
        float[][] bits = new float[rows][columns * 3];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns / 8; j++) {
                int next = dis.readUnsignedByte();
                if (next < 127) {
                    next = 0;
                } else {
                    next = 1;
                }
                for (int k = 0; k < 8; k++) {
                    temp[i][8 * j + k] = next;
                }
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                bits[i][j * 3] = temp[i][j];
                bits[i][j * 3 + 1] = temp[i][j];
                bits[i][j * 3 + 2] = temp[i][j];
            }
        }

        return bits;
    }


    private static float[][] readP5(int rows, int columns, String filepath, int mbv) throws IOException {
        FileInputStream fis = new FileInputStream(filepath);
        DataInputStream dis = new DataInputStream(fis);
        dis.skipBytes(4);
        float[][] bits = new float[rows][columns * 3];
        byte[] buffer = new byte[columns];
        for (int i = 0; i < rows; i++) {
            dis.readFully(buffer);
            for (int j = 0; j < columns; j++) {
                float next = (float) (buffer[j] & 0xFF) / mbv;
                bits[i][j * 3] = next;
                bits[i][j * 3 + 1] = next;
                bits[i][j * 3 + 2] = next;
            }
        }
        return bits;
    }


    private static float[][] readP6(int rows, int columns, String filepath, int mbv) throws IOException {
        float[][] bits = new float[rows][columns * 3];
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filepath));
             DataInputStream dis = new DataInputStream(bis)) {
            for (int i = 0; i < 4; i++) {
                char c;
                do {
                    c = (char) (dis.readUnsignedByte());
                } while (c != '\n');
            }
            int totalPixels = rows * columns * 3;
            byte[] pixelBytes = new byte[totalPixels];
            dis.readFully(pixelBytes);
            for (int i = 0; i < totalPixels; i++) {
                bits[i / (columns * 3)][i % (columns * 3)] = (float) (pixelBytes[i] & 0xff) / mbv;
            }
        }
        return bits;
    }
}