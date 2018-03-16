package com.harishsk.saveblood;

public class Blood {

    private String name, gender, age, blgrp, phno, place, update;

    public Blood() {
    }

    public Blood(String name, String gender, String age, String blgrp, String phno, String place, String update) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.blgrp = blgrp;
        this.phno = phno;
        this.place = place;
        this.update = update;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBlgrp() {
        return blgrp;
    }

    public void setBlgrp(String blgrp) {
        this.blgrp = blgrp;
    }

    public String getPhno() {
        return phno;
    }

    public void setPhno(String phno) {
        this.phno = phno;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }
}
