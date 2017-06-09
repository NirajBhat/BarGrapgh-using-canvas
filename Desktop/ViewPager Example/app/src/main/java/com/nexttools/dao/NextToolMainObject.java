package com.nexttools.dao;

import java.util.List;

/**
 * Created by next on 8/5/17.
 * Purpose:
 * It Will Contains The Data Object Only WhereIn If You Declare The Object
 * Private You Need To Use Getter And Setter.It Will Have The State And
 * Behaviour Of The Class.
 */
public class NextToolMainObject {

    String title;
    String thumb;
    String info;
    List<Tabs> tabsInfo;


    public NextToolMainObject(String title, String thumb, String info, List<Tabs> tabsInfo) {
        this.title = title;
        this.thumb = thumb;
        this.info = info;
        this.tabsInfo = tabsInfo;
    }

    public NextToolMainObject() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<Tabs> getTabsInfo() {
        return tabsInfo;
    }

    public void setTabsInfo(List<Tabs> tabsInfo) {
        this.tabsInfo = tabsInfo;
    }
}
