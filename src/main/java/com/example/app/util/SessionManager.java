package com.example.app.util;

public class SessionManager {
    private static String jwtToken;

    public static void setAuthToken(String token) {
        jwtToken = token;
    }

    public static String getAuthToken() {
        return jwtToken;
    }

    public static void removeAuthToken() {
        jwtToken = null;
    }
}