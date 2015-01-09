package com.example.admin.vkreader.async_task;

import android.os.AsyncTask;

import com.example.admin.vkreader.entity.JsonClass;
import com.example.admin.vkreader.java_classes.BaseParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ParseTask extends AsyncTask<Void, Void, ArrayList<String>> {
    private String resultJson;
    private String stringUrl;
    private BaseParser baseParser = new BaseParser();
    private ArrayList title;

    public ParseTask(String stringUrl) {
        this.stringUrl = stringUrl;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            resultJson = buffer.toString();
            JSONObject jsonObject = new JSONObject(resultJson);

            ArrayList<JsonClass> result = baseParser.jsonParse(jsonObject);
            title = baseParser.forResult(result);

        } catch (JSONException e) {
            System.out.println(e + " - in ParseTask");
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println(e + " - in ParseTask");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e + " - in ParseTask");
            e.printStackTrace();
        }
        return title;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        super.onPostExecute(result);
    }
}
