package com.example.app.util;

import com.example.app.model.Project;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Requests {
    public static List<Project> getAllProjectsFromDB() throws IOException, InterruptedException {

        List<Project> projects = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest requestToMembers = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/Person/projects-of-person/" + SessionManager.getUserId()))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(requestToMembers, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> projectList = mapper.readValue(
                response.body(),
                new TypeReference<List<Map<String, Object>>>() {
                }
        );
        for (Map<String, Object> project : projectList) {
            long id = Long.parseLong(project.get("id").toString());
            String name = project.get("title").toString();

            Project tempProject = new Project(id, name);
            projects.add(tempProject);
        }
        return projects;
    }

    public static void addProjectToDB(Project project) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String jsonToCreate = String.format(
                "{\"title\":\"%s\"}",
                project.getName()
        );

        HttpRequest requestToCreate = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/Project/create"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonToCreate))
                .build();

        HttpResponse<String> response = client.send(requestToCreate, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapperDb = new ObjectMapper();
        Map<String, Object> jsonResponse = mapperDb.readValue(response.body(), Map.class);
        long id = Long.parseLong(jsonResponse.get("id").toString());
        project.setId(id);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        rootNode.put("projectId", project.getId());
        rootNode.put("personId", SessionManager.getUserId());
        String jsonToMember = mapper.writeValueAsString(rootNode);

        HttpRequest requestToMembers = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/Project/members"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonToMember))
                .build();


        client.send(requestToMembers, HttpResponse.BodyHandlers.ofString());

    }
}