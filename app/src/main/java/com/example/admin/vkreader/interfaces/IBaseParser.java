package com.example.admin.vkreader.interfaces;

import com.example.admin.vkreader.entity.JsonClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public interface IBaseParser {
    public ArrayList<JsonClass> jsonParse(JSONObject jsonObject) throws JSONException;
    public ArrayList<String> forResult(ArrayList<JsonClass> result);
}
