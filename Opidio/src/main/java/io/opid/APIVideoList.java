package io.opid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class APIVideoList extends JsonDownloader<List<String>> {

    @Override
    protected List<String> parseJSON(JSONObject obj) {
        ArrayList<String> groups = new ArrayList<>();

        try {
            JSONArray jsonArray = obj.getJSONArray("grupper");
            for (int i = 0; i < jsonArray.length(); i++) {
                String group = jsonArray.getString(i);
                groups.add(group);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return groups;
    }

    public void run() {
        execute(Config.HUB_SERVER + "/api/videos/");
    }
}