package com.aimozart.user.myapplicationlayouttest;

import java.io.Serializable;

public class Member implements Serializable { // VO- Value Object
    private int id;
    private String name;
    private String music;

    public Member(String name){
        this(0, name, "");
    }

    public Member(int id, String name, String music) {
        super();
        this.id = id;
        this.name = name;
        this.music = music;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMusic(){return music;}

    public void setMusic(String music){this.music = music;}

}