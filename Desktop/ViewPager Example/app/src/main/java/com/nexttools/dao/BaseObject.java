package com.nexttools.dao;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by next on 10/5/17.
 */
public abstract class BaseObject {

    public BaseObject(Context context) {

    }

    public abstract void parse(JSONObject object) throws JSONException;
}
