package com.example.getpet.Model;

public class AnimalList {

    private String pid,a_name;


    public AnimalList() {
    }

    public AnimalList(String pid, String a_name) {
        this.pid = pid;
        this.a_name = a_name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getA_name() {
        return a_name;
    }

    public void setA_name(String a_name) {
        this.a_name = a_name;
    }
}
