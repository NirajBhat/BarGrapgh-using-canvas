package com.nexttools.dao;

import java.util.List;

/**
 * Created by next on 8/5/17.
 * Purpose:
 * It Will Contains The Data Object Only WhereIn If You Declare The Object
 * Private You Need To Use Getter And Setter.It Will Have The State And
 * Behaviour Of The Class.
 */
public class Cards {

    String title;
    String info;
    String thumb;
    String url;
    List<Guide> guideInfo;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Guide> getGuideInfo() {
        return guideInfo;
    }

    public void setGuideInfo(List<Guide> guideInfo) {
        this.guideInfo = guideInfo;
    }
}
