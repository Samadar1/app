package com.example.app.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class TextEncoderDecoder {
    // Получаем путь к AppData
    private static final String APP_DATA_PATH = System.getenv("APPDATA") + "\\MyApp";
    private static final String FILE_NAME = "data.txt";
    private static final Path FILE_PATH = Paths.get(APP_DATA_PATH, FILE_NAME);

    // Закодировать строку и сохранить в файл
    public static void encodeAndSave(String input) throws IOException {
        // Создаём директорию, если её нет
        Files.createDirectories(FILE_PATH.getParent());

        String encoded = Base64.getEncoder().encodeToString(input.getBytes());

        try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH)) {
            writer.write(encoded);
        }
    }

    // Прочитать файл и декодировать
    public static String decodeFromFile() throws IOException {
        if (!Files.exists(FILE_PATH)) {
            throw new FileNotFoundException("Файл не найден: " + FILE_PATH.toString());
        }

        String encoded;

        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
            encoded = reader.readLine();
        }

        byte[] decodedBytes = Base64.getDecoder().decode(encoded);
        return new String(decodedBytes);
    }

    public static void clearFileContent() throws IOException {
        if (Files.exists(FILE_PATH)) {
            try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH)) {
                writer.write(""); // Очищаем содержимое
            }
        }
    }

    public static boolean is_empty() throws IOException {
        String encoded;
        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
            encoded = reader.readLine();
        }
        if (encoded == null || encoded.trim().isEmpty()) {
            System.err.println("Файл пустой или не найден.");
            return true;
        }
        return false;
    }
}