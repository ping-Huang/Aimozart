package com.aimozart.user.myapplicationlayouttest;

import java.io.Serializable;

public class Music implements Serializable { // VO- Value Object
    private int id;
    private String name;
    private String path;
    private String type;

    public Music(String name,String path){
        this(0, name,path);
    }

    public Music(int id, String name,String path) {
        super();
        this.id = id;
        this.name = name;
        this.path = path;
    }
    public Music(int id, String name,String path,String type) {
        super();
        this.id = id;
        this.name = name;
        this.path = path;
        this.type = type;
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

    public String getPath(){return path;}

    public void setPath(String path){this.path = path;}

    public void setType(String type){this.type = type;}

    public String getType(){return type;}

}