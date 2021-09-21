package com.caicongyang.client.domain;

/**
 * Created by pengrongxin on 2017/3/1.
 */
public class SerialResult {

    private Long id;
    private String img_scale;
    private String img_wm;
    private String fileName;
    private String url;
    /**
     * 生成套图的大小(单位是B)
     */
    private Integer size;

    private boolean is_major;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImg_scale() {
        return img_scale;
    }

    public void setImg_scale(String img_scale) {
        this.img_scale = img_scale;
    }

    public String getImg_wm() {
        return img_wm;
    }

    public void setImg_wm(String img_wm) {
        this.img_wm = img_wm;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public boolean isIs_major() {
        return is_major;
    }
    public void setIs_major(boolean is_major) {
        this.is_major = is_major;
    }
}
