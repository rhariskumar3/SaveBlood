package com.harishsk.saveblood;

public class User {

    private String name, phno, role;

    public User() {
    }

    public User(String name, String phno, String role) {
        this.name = name;
        this.phno = phno;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhno() {
        return phno;
    }

    public void setPhno(String phno) {
        this.phno = phno;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
