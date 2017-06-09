package com.nexttools.dao;

import android.content.Context;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by next on 8/5/17.
 * Purpose:
 * It Will Contains The Data Object Only WhereIn If You Declare The Object
 * Private You Need To Use Getter And Setter.It Will Have The State And
 * Behaviour Of The Class.
 */
public class Tabs{
    String title;
    String icon;
    String info;
    List<Cards> cardInfo;

    public Tabs() {

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<Cards> getCardInfo() {
        return cardInfo;
    }

    public void setCardInfo(List<Cards> cardInfo) {
        this.cardInfo = cardInfo;
    }

   /* @Override
    public void parse(JSONObject object) {

    }*/
}
