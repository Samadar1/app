package com.example.app.util.requests;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Requests {
    private static final HttpClient client = HttpClient.newHttpClient();

    public static String checkJWT(String token) throws IOException, InterruptedException {
        String json = String.format(
                "{\"token\":\"%s\"}",
                token
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000/auth/check-jwt"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return new ObjectMapper().readTree(response.body()).get("username").asText();
        }

        return null;
    }

    public static String signIn(String login, String password) throws IOException, InterruptedException {
        String json = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                login, password
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000/auth/sign-in"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return new ObjectMapper().readTree(response.body()).get("token").asText();
        }
        return null;
    }

    public static String signUp(String login, String email, String password) throws IOException, InterruptedException {
        String role = "ROLE_USER";

        String json = String.format(
                "{\"username\":\"%s\",\"email\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}",
                login, email, password, role
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000/auth/sign-up"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return new ObjectMapper().readTree(response.body()).get("token").asText();
        }
        return null;
    }

    public static String getEmailByUserName(String username) throws IOException, InterruptedException {
        String baseUrl = "http://localhost:8000/auth/"+ username + "/email";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .header("Accept", "application/json")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

}