package com.example.app.util;

public class SessionManager {
    private static String jwtToken;
    private static String username;
    private static Long userId;
    private static String email;



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

    public static String getUsername() {
        return username;
    }
    public static long getUserId() {
        return userId;
    }

    public static void setUserId(long Id) {
        userId = Id;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        SessionManager.email = email;
    }
}