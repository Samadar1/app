package com.example.app.util;

public class SessionManager {
    private static String jwtToken;
    private static String username;



    public static void setAuthToken(String token) {
        jwtToken = token;
    }

    public static String getAuthToken() {
        return jwtToken;
    }

    public static void removeAuthToken() {
        jwtToken = null;
    }

    public static void setUsername(String name) {
        username = name;
    }

    public String getUsername() {
        return username;
    }
}