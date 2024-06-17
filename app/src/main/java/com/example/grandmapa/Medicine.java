package com.example.grandmapa;


public class Medicine {
    private String time;
    private String name;
    private boolean taken;

    public Medicine(String time, String name, boolean taken) {
        this.time = time;
        this.name = name;
        this.taken = taken;
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

}

