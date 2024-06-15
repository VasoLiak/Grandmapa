package com.example.grandmapa;

public class Contact {
    private long id;
    private String name;
    private String phone;
    private boolean isSos;

    public Contact(long id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.isSos = false;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isSos() {
        return isSos;
    }

    public void setSos(boolean sos) {
        isSos = sos;
    }
}

