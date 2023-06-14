package com.example.microgimp;

import javafx.stage.Stage;

import java.io.*;
import java.util.Objects;


public class ReadFile {


    public static float[][] main(String filename) {
        int rows = 0, columns = 0;
        int mbv = 255;
        BufferedReader reader = null;
        float[][] bits = new float[rows][columns];

        try {
            File file = new File(filename);

            reader = new BufferedReader(new FileReader(file));

            String type = reader.readLine(); //P1-6
            reader.readLine(); //pomijam komentarz
            String[] rozmiar = reader.readLine().split(" "); //wymiary
            columns = Integer.parseInt(rozmiar[0]);
            rows = Integer.parseInt(rozmiar[1]);
            if (!Objects.equals(type, "P1") && !Objects.equals(type, "P4")) {
                mbv = Integer.parseInt(reader.readLine()); //odcienie szarości i kolorowe mają podaną najwyższą wartość jaka będzie w dokumencie
            }
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


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bits;

    }

    private static float[][] readP1(BufferedReader reader, int rows, int columns) throws IOException {
        float[][] temp = new float[rows][columns];
        float[][] bits = new float[rows][columns * 3];
        String line;
        StringBuilder fileContent = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            fileContent.append(line);
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int next = fileContent.charAt(i * columns + j) - '0';
                temp[i][j] = (float) next; //dzielimy przez najwyższą wartość (normalizacja jakby)
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

    private static float[][] readP2(BufferedReader reader, int rows, int columns, int mbv) throws IOException {
        float[][] temp = new float[rows][columns];
        float[][] bits = new float[rows][columns * 3];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int nextChar = Integer.parseInt(reader.readLine());
                while (nextChar == ' ' || nextChar == '\n') {
                    nextChar = Integer.parseInt(reader.readLine());
                }
                temp[i][j] = (float) nextChar / mbv; //dzielimy przez najwyższą wartość (normalizacja jakby)
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

    private static float[][] readP3(BufferedReader reader, int rows, int columns, int mbv) throws IOException {
        float[][] bits = new float[rows][columns * 3];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns * 3; j++) {
                int nextChar = Integer.parseInt(reader.readLine());
                while (nextChar == ' ' || nextChar == '\n') {
                    nextChar = Integer.parseInt(reader.readLine());
                }
                bits[i][j] = (float) nextChar / mbv; //normalizacja
            }
        }
        return bits;
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
        FileInputStream fileInputStream = new FileInputStream(filepath);
        DataInputStream dis = new DataInputStream(fileInputStream);
        for (int i = 0; i < 4; i++) {
            char c;
            do {
                c = (char) (dis.readUnsignedByte());
            } while (c != '\n');
        }
        float[][] temp = new float[rows][columns];
        float[][] bits = new float[rows][columns * 3];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int next = dis.readUnsignedByte();
                temp[i][j] = (float) next / mbv;
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


    private static float[][] readP6(int rows, int columns, String filepath, int mbv) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(filepath);
        DataInputStream dis = new DataInputStream(fileInputStream);
        for (int i = 0; i < 4; i++) {
            char c;
            do {
                c = (char) (dis.readUnsignedByte());
            } while (c != '\n');
        }
        float[][] bits = new float[rows][columns * 3];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns * 3; j++) {
                int next = dis.readUnsignedByte();
                bits[i][j] = (float) next / mbv;
            }
        }
        return bits;
    }
}