package com.example.admin.vkreader.java_classes;

import com.example.admin.vkreader.entity.JsonClass;
import com.example.admin.vkreader.entity.ResultClass;
import com.example.admin.vkreader.interfaces.IBaseParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseParser implements IBaseParser {
    private ArrayList<JsonClass> arrayList = new ArrayList();
    private ArrayList<String> title = new ArrayList();
    private ResultClass resultClass = ResultClass.getInstance();

    @Override
    public ArrayList<JsonClass> jsonParse(JSONObject jsonObject) throws JSONException {
        jsonObject = jsonObject.getJSONObject("response");
        JSONArray jArray = jsonObject.getJSONArray("wall");
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject json_message = jArray.getJSONObject(i);
            if (json_message != null) {
                String text = json_message.getString("text");
                int data = json_message.getInt("date");
                JSONObject im = json_message.getJSONObject("attachment");
                im = im.getJSONObject("photo");
                String urls = im.getString("src_big");
                JsonClass jsonClass = new JsonClass(text, data, urls);
                arrayList.add(jsonClass);
            }
        }
        return arrayList;
    }

    @Override
    public ArrayList<String> forResult(ArrayList<JsonClass> result) {
        for (int i = 0; i < result.size(); i++) {
            Pattern pat = Pattern.compile("<.+?>");
            Matcher mat = pat.matcher(result.get(i).getTextContent());
            mat.find();
            int k = mat.start();
            String match = mat.replaceAll("\n");
            String substring = match.substring(0, k);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String text = match + "\n\n" + sdf.format(result.get(i).getDate());
            String urls = result.get(i).getImageContent();

            title.add(substring);

            resultClass.getTitle().add(substring);
            resultClass.getText().add(text);
            resultClass.getUrls().add(urls);
        }
        return title;
    }
}
