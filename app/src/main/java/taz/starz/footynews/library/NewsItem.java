package taz.starz.footynews.library;

/**
 * Created by Thahzan on 6/29/2014.
 */
public class NewsItem {

    private String news_id;
    private String headline, body, img_url, source, league;

    public NewsItem() {

    }

    public NewsItem(String news_id, String headline, String body, String img_url, String source, String league) {
        this.news_id = news_id;
        this.headline = headline;
        this.body = body;
        this.img_url = img_url;
        this.source = source;
        this.league = league;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public String getNews_id() {
        return news_id;
    }

    public void setNews_id(String news_id) {
        this.news_id = news_id;
    }
}
