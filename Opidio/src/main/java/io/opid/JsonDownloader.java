package io.opid;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public abstract class JsonDownloader<Result> extends AsyncTask<String, Result, Result> {
    @Override
    protected Result doInBackground(String... params) {
        try {
            String data = downloadData(params[0]);
            JSONObject obj;
            obj = new JSONObject(data);
            return parseJSON(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected abstract Result parseJSON(JSONObject obj);

    private String downloadData(String urlString) throws IOException {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        URLConnection conn = url.openConnection();
        String response = "";
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            response += line;
        }
        in.close();
        return response;
    }
}