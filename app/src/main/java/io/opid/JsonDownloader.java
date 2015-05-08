package io.opid;

import android.os.AsyncTask;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public abstract class JsonDownloader<JsonType> extends AsyncTask<String, JsonType, JsonType> {
    private final Class<JsonType> type;

    public JsonDownloader(Class<JsonType> type) {
        this.type = type;
    }

    @Override
    protected JsonType doInBackground(String... params) {
        try {
            String data = downloadData(params[0]);
            return new ObjectMapper().readValue(data, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

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