package com.quemb.qmbform.pojo;

/**
 * Created by Years.im on 16/3/19.
 */
public class ImageItem {

    private Integer id;
    private String path;

    public ImageItem(String path) {
        this.path = path;
    }

    public ImageItem(Integer id, String path) {
        this.path = path;
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
