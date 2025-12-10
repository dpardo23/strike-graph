package com.dpardo.strike.domain;

public class SessionInfo {
    private int userId;
    private int pid;
    private String clientIp;
    private int clientPort;

    public SessionInfo(int userId, int pid, String clientIp, int clientPort) {
        this.userId = userId;
        this.pid = pid;
        this.clientIp = clientIp;
        this.clientPort = clientPort;
    }

    public int getUserId() {
        return userId;
    }

    public int getPid() {
        return pid;
    }
}