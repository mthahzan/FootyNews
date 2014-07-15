package taz.starz.footynews.library;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Thahzan on 7/5/2014.
 */
public class SharedPref {

    private Context context;
    private SharedPreferences sharedPref;

    public SharedPref(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences("footy_news", 0);
    }

    public void setSelectedNews(String i) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("news_id", i);
        editor.commit();
    }

    public String getSelectedNews() {
        return sharedPref.getString("news_id", "");
    }

    public void setViewvingNews(JSONObject json) throws JSONException {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("news_id", json.getString("news_id"));
        editor.putString("headline", json.getString("headline"));
        editor.putString("body", json.getString("body"));
        editor.putString("source", json.getString("source"));
//        editor.putString("img_url", json.getString("img_url"));
        editor.putString("league", json.getString("league"));
        editor.commit();
    }

    public JSONObject getViewingNews() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("news_id", sharedPref.getString("news_id", ""));
        json.put("headline", sharedPref.getString("headline", ""));
        json.put("body", sharedPref.getString("body", ""));
        json.put("source", sharedPref.getString("source", ""));
        json.put("img_url", sharedPref.getString("img_url", ""));
        json.put("league", sharedPref.getString("league", ""));

        return json;
    }
}
