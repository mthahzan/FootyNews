package taz.starz.footynews.library;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thahzan on 6/26/2014.
 */
public class NetworkFunctions {

//    private final String GET_INFO_URL = "http://thahzan.ngrok.com/footy_news/news_funcs.php";
    private final String GET_INFO_URL = "http://10oclock.org/android2/footy_news/news_funcs.php";

//    http://10oclock.org/android2/footy_news/news_funcs.php

    private final String GET_HEADLINES_URL = "http://footynews.thahzan.me/api/v1/getHeadlines";
    private final String GET_NEWS_URL = "http://footynews.thahzan.me/api/v1/getArticle";

    private JSONParser jsonParser;

    public NetworkFunctions() {
        jsonParser = new JSONParser();
    }

    public JSONArray getInfo(String newsType) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("request", newsType));
//        params.add(new BasicNameValuePair("league", league));

        return jsonParser.getJSONArrayFromUrl(GET_HEADLINES_URL, params);
    }

    public JSONObject getNews(String news_id) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("request", "getDetailsOfNews"));
        params.add(new BasicNameValuePair("news_id", news_id));

        return jsonParser.getJSONFromUrl(GET_NEWS_URL, params);
    }

}
