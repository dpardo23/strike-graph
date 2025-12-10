package com.dpardo.strike.domain;

public class SessionManager {
    private static SessionManager instance;

    private int userId;
    private String username;
    private String role;
    private int sessionId; // ID del nodo Session en Neo4j
    private int pid;       // PID simulado

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public void clear() {
        userId = 0;
        username = null;
        role = null;
        sessionId = 0;
        pid = 0;
    }
}