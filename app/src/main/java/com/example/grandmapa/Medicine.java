package com.example.grandmapa;


public class Medicine {
    private String time;
    private String name;
    private boolean taken;
    private String notificationTime;

    public Medicine(String time, String name, boolean taken, String notificationTime) {
        this.time = time;
        this.name = name;
        this.taken = taken;
        this.notificationTime = null;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }
}

