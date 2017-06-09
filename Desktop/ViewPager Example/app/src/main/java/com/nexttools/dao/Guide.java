package com.nexttools.dao;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by next on 8/5/17.
 * Purpose:
 * It Will Contains The Data Object Only WhereIn If You Declare The Object
 * Private You Need To Use Getter And Setter.It Will Have The State And
 * Behaviour Of The Class.
 */
public class Guide  {

    String thumb;
    String title;
    String colour;
    String description;

    public Guide() {

    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

   /* @Override
    public void parse(JSONObject object) {
        try {
            thumb = object.getString("thumb");
            title = object.getString("title");
            colour = object.getString("color");
            description = object.getString("description");

        } catch (JSONException jsone) {
            jsone.printStackTrace();
        }
    }*/
}
