package com.opensource.opengles.ui_yida.bean;

import java.io.Serializable;

public class FilterBean implements Serializable {
    private int imgResource;
    private String name;

    public int getImgResource() {
        return imgResource;
    }

    public void setImgResource(int imgResource) {
        this.imgResource = imgResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
