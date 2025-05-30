package com.example.app.util;

import com.example.app.model.ProjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectStructureParser {

    /**
     * Парсит файл со структурой и кодом проекта и возвращает класс-узел дерева
     * @param txtFile файл со структурой и кодом проекта
     * @return {@code root} - корень проекта
     */
    public static ProjectNode parse(File txtFile) throws IOException {
        String content = Files.readString(txtFile.toPath());
        return parse(content);
    }

    public static ProjectNode parse(String rawText) {
        // Ищем разделитель
        int splitIndex = rawText.indexOf("Содержимое файлов:");
        String treePart = rawText.substring(0, splitIndex).trim();
        String contentPart = rawText.substring(splitIndex).trim();

        // Извлекаем список файлов
        List<String> filePaths = new ArrayList<>();
        for (String line : treePart.split("\n")) {
            line = line.trim();
            if (line.startsWith("├─") || line.startsWith("└─")) {
                String path = line.substring(2).trim();
                filePaths.add(path.replace("\\", "/"));
            }
        }

        // Построение дерева
        ProjectNode root = new ProjectNode("root", false);
        Map<String, ProjectNode> pathToNode = new HashMap<>();
        pathToNode.put("", root); // корень

        for (String path : filePaths) {
            String[] parts = path.split("/");
            StringBuilder currentPath = new StringBuilder();
            ProjectNode current = root;

            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                currentPath.append(part);
                String fullPath = currentPath.toString();
                boolean isFile = (i == parts.length - 1);

                if (!pathToNode.containsKey(fullPath)) {
                    ProjectNode newNode = new ProjectNode(part, isFile);
                    current.addChild(newNode);
                    pathToNode.put(fullPath, newNode);
                }

                current = pathToNode.get(fullPath);
                currentPath.append("/");
            }
        }

        // Парсинг содержимого файлов
        Pattern fileBlockPattern = Pattern.compile(
                "Файл: (.+?)\\n(.*?)\\n={10,}",
                Pattern.DOTALL
        );

        Matcher matcher = fileBlockPattern.matcher(contentPart);
        while (matcher.find()) {
            String path = matcher.group(1).trim().replace("\\", "/");
            String fileContent = matcher.group(2).trim();

            ProjectNode fileNode = pathToNode.get(path);
            if (fileNode != null && fileNode.isFile()) {
                fileNode.setContent(fileContent);
            }
        }

        return root;
    }
}
