package com.example.app.util.requests;

import com.example.app.model.Project;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class RequestsNeo4j {
    private static final HttpClient client = HttpClient.newHttpClient();

    public static long getPersonIdByUserName(String username) throws IOException, InterruptedException {
        HttpRequest requestNeo4j = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/Person/get-person-by-name/"+ username))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(requestNeo4j, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200 && !Objects.equals(response.body(), "[]")) {
            ObjectMapper objectMapper = new ObjectMapper();

            List<Map<String, Object>> userList = objectMapper.readValue(
                    response.body(),
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            Map<String, Object> user = userList.get(0);

            return Long.parseLong(user.get("id").toString());
        }
        return -1;
    }

    public static List<Project> getAllUsersProjectsByUserId(long personId) throws IOException, InterruptedException {
        List<Project> projects = new ArrayList<>();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/Person/projects-of-person/" + personId))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 302) {
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
        }
        return projects;
    }

    public static long createProject(Project project, long personId) throws IOException, InterruptedException {
        String jsonToCreate = String.format(
                "{\"title\":\"%s\",\"creatorId\":\"%s\"}",
                project.getName(), personId
        );

        HttpRequest requestToCreate = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/Project/create"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonToCreate))
                .build();

        HttpResponse<String> response = client.send(requestToCreate, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201) {
            ObjectMapper mapperDb = new ObjectMapper();
            Map<String, Object> jsonResponse = mapperDb.readValue(response.body(), Map.class);

            return Long.parseLong(jsonResponse.get("id").toString());
        }
        return -1;
    }

    public static long createUser(String username) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("name", username);

        ArrayNode skillSet = mapper.createArrayNode(); // Пустой массив
        rootNode.set("skillSet", skillSet);

        String jsonNeo4j = mapper.writeValueAsString(rootNode);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/Person/create"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonNeo4j))
                .build();

        return new ObjectMapper().readTree(client.send(request, HttpResponse.BodyHandlers.ofString()).body()).get("id").asLong();
    }

    public static void addMembersToProject(long projectId, long memberId) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        rootNode.put("projectId", projectId);
        rootNode.put("personId", memberId);
        String jsonToMember = mapper.writeValueAsString(rootNode);

        HttpRequest requestToMembers = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/Project/members"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonToMember))
                .build();

        client.send(requestToMembers, HttpResponse.BodyHandlers.ofString());
    }

    public static void updateSkillSetById(List<String> checkedItems, long personId) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("personId", personId);

        ArrayNode skillArray = rootNode.putArray("personSkillSet");

        for (String skill : checkedItems) {
            skillArray.add(skill);
        }
        String jsonOutput = mapper.writeValueAsString(rootNode);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/Person/updateSkillSetById"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonOutput))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static List<String> getAllMembersInProjectByProjectId(long projectId) throws IOException, InterruptedException {
        HttpRequest requestToCreate = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/Project/members-of-project/" + projectId))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(requestToCreate, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();

        if (response.statusCode() == 302) {
            List<String> members = new ArrayList<>();

            List<Map<String, Object>> membersList = mapper.readValue(
                    response.body(),
                    new TypeReference<List<Map<String, Object>>>() {
                    }
            );

            for (Map<String, Object> member : membersList) {
                String name = member.get("name").toString();
                members.add(name);
            }

            return members;
        }

        return null;
    }

    public static List<Long> getProjectById(long projectId) throws IOException, InterruptedException {
        List<Long> personid = new ArrayList<>();

        HttpRequest requestToCreate = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/Project/get-project/" + projectId))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(requestToCreate, HttpResponse.BodyHandlers.ofString());
        List<Long> ids = new ArrayList<>();

        if (response.statusCode() == 302) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.body());

                // Извлекаем creator.id
                JsonNode creatorArray = rootNode.get("creator");
                if (creatorArray != null && creatorArray.isArray()) {
                    for (JsonNode node : creatorArray) {
                        ids.add(node.get("id").asLong());
                    }
                }

                // Извлекаем admins.id
                JsonNode adminsArray = rootNode.get("admins");
                if (adminsArray != null && adminsArray.isArray()) {
                    for (JsonNode node : adminsArray) {
                        ids.add(node.get("id").asLong());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ids;
    }
}
