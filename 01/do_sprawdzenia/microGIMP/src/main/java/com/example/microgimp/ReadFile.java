package com.example.microgimp;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.stage.Stage;
import java.io.FileInputStream;


public class ReadFile {
    public static void main(String filename) throws IOException {
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
            Stage imageStage = new Stage(); //nowe okno
            switch (type) {
                case "P1" -> bits = readP1(reader, rows, columns);
                case "P2" -> bits = readP2(reader, rows, columns, mbv);
                case "P3" -> bits = readP3(reader, rows, columns, mbv);
                case "P4" -> bits = readP4(rows, columns, filename);
                case "P5" -> bits = readP5(rows, columns, filename, mbv);
                case "P6" -> bits = readP6(rows, columns, filename, mbv);
                default -> {
                }
            }


            switch (type) {
                case "P1" -> Display.showImage(imageStage, bits, 1);
                case "P2" -> Display.showImage(imageStage, bits, 2);
                case "P3" -> Display.showImage(imageStage, bits, 3);
                case "P4" -> Display.showImage(imageStage, bits, 4);
                case "P5" -> Display.showImage(imageStage, bits, 5);
                case "P6" -> Display.showImage(imageStage, bits, 6);
                default -> {
                }
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

    }
    private static float[][] readP1(BufferedReader reader, int rows, int columns) throws IOException {
        float[][] bits = new float[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int nextChar = reader.read();
                while (nextChar == ' ' || nextChar == '\n') { //omija newline w dokumencie
                    nextChar = reader.read();
                }
                bits[i][j] = nextChar - '0';
            }
        }
        return bits;
    }

    private static float[][] readP2(BufferedReader reader, int rows, int columns, int mbv) throws IOException {
        float[][] bits = new float[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int nextChar = Integer.parseInt(reader.readLine());
                while (nextChar == ' ' || nextChar == '\n') {
                    nextChar = Integer.parseInt(reader.readLine());
                }
                bits[i][j] = (float)nextChar/mbv; //dzielimy przez najwyższą wartość (normalizacja jakby)
            }
        }
        return bits;
    }

    private static float[][] readP3(BufferedReader reader, int rows, int columns, int mbv) throws IOException {
        float[][] bits = new float[rows][columns*3];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns*3; j++) {
                int nextChar = Integer.parseInt(reader.readLine());
                while (nextChar == ' ' || nextChar == '\n') {
                    nextChar = Integer.parseInt(reader.readLine());
                }
                bits[i][j] = (float)nextChar/mbv; //normalizacja
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
        float[][] bits = new float[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns/8; j++) {
                int next = dis.readUnsignedByte();
                if (next < 127){
                    next = 0;
                }
                else {
                    next = 1;
                }
                for (int k = 0; k < 8; k ++) {
                    bits[i][8*j + k] = next;
                    System.out.print(bits[i][j] + " ");
                }
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
        float[][] bits = new float[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int next = dis.readUnsignedByte();
                bits[i][j] = (float)next/mbv;
                System.out.print(bits[i][j] + " ");
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
        float[][] bits = new float[rows][columns*3];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns*3; j++) {
                int next = dis.readUnsignedByte();
                bits[i][j] = (float)next/mbv;
                System.out.print(bits[i][j] + " ");
            }
        }
        return bits;
    }
}

