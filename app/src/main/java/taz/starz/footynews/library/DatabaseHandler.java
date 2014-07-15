package taz.starz.footynews.library;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thahzan on 6/26/2014.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private final String TABLE_HEADER_IMAGE = "main_header_image";
    private final String KEY_IMAGE_URL = "image_url";

    private final String TABLE_NEWS = "news";
    private final String KEY_NEWS_ID = "news_id";
    private final String KEY_HEADLINE = "headline";
    private final String KEY_NEWS_BODY = "news_body";
    private final String KEY_NEWS_IMG_URL = "img_url";
    private final String KEY_SOURCE = "source";
    private final String KEY_LEAGUE = "league";

    private final String TABLE_OFFLINE_READ = "offline_read";

    private final String TABLE_HEADLINE = "headline";

    public DatabaseHandler(Context context) {
        super(context, "footy_news", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_NEWS_TABLE = "CREATE TABLE " + TABLE_NEWS + "("
                + KEY_NEWS_ID + " VARCHAR(36), "
                + KEY_HEADLINE + " VARCHAR(128), "
                + KEY_NEWS_BODY + " TEXT, "
                + KEY_SOURCE + " VARCHAR(256), "
                + KEY_LEAGUE + " VARCHAR(32)"
                + ")";
        String CREATE_HEADLINES_TABLE = "CREATE TABLE " + TABLE_HEADLINE + "("
                + KEY_NEWS_ID + " VARCHAR(36) PRIMARY KEY, "
                + KEY_HEADLINE + " VARCHAR(128), "
                + KEY_LEAGUE + " VARCHAR(32)"
                + ")";
        sqLiteDatabase.execSQL(CREATE_NEWS_TABLE);
        sqLiteDatabase.execSQL(CREATE_HEADLINES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_HEADER_IMAGE);
    }

    public boolean isStoryAlreadySaved(String headline) {
        SQLiteDatabase db = this.getReadableDatabase();
        String qry = "SELECT * FROM " + TABLE_NEWS + " WHERE " + KEY_HEADLINE + " = ?";
        Cursor cursor = db.rawQuery(qry, new String[]{headline});
        if(cursor.moveToFirst())
            return true;
        return false;
    }

    public void storeNewsItems(NewsItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_NEWS_ID, item.getNews_id());
            values.put(KEY_HEADLINE, item.getHeadline());
            values.put(KEY_NEWS_BODY, item.getBody());
            values.put(KEY_SOURCE, item.getSource());
            values.put(KEY_LEAGUE, item.getLeague());
            db.insert(TABLE_NEWS, null, values);
        db.close();
    }

    public List<NewsItem> getAllNews() {
        SQLiteDatabase db = this.getReadableDatabase();
        String qry = "SELECT "
                + KEY_HEADLINE + ", "
                + KEY_NEWS_BODY + ", "
                + KEY_SOURCE + ", "
                + KEY_LEAGUE + ", "
                + KEY_NEWS_ID
                + " FROM " + TABLE_NEWS;
        Cursor cursor = db.rawQuery(qry, null);
        List<NewsItem> list = new ArrayList<NewsItem>();
        String csr = DatabaseUtils.dumpCursorToString(cursor);
        if(cursor.moveToFirst()){
            do{
                NewsItem item = new NewsItem();
                item.setHeadline(cursor.getString(0));
                item.setBody(cursor.getString(1));
                item.setSource(cursor.getString(2));
                item.setLeague(cursor.getString(3));
                item.setNews_id(cursor.getString(4));
                list.add(item);
            }while(cursor.moveToNext());
        }
        return list;
    }

    public NewsItem getNewsOfId(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String qry = "SELECT "
                + KEY_HEADLINE + ", "
                + KEY_NEWS_BODY + ", "
                + KEY_SOURCE
                + " FROM " + TABLE_NEWS + " WHERE "
                + KEY_NEWS_ID + " =?";
        Cursor cursor = db.rawQuery(qry, new String[]{id});
        NewsItem item = new NewsItem();
        if(cursor.moveToFirst()){
            item.setHeadline(cursor.getString(0));
            item.setBody(cursor.getString(1));
            item.setSource(cursor.getString(2));
        }
        return item;
    }

    public List<NewsItem> getLeagueItems(String league) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<NewsItem> list = new ArrayList<NewsItem>();
        String qry = "SELECT * FROM " + TABLE_HEADLINE + " WHERE " + KEY_LEAGUE + " = '" + league + "'";
        Cursor cursor = db.rawQuery(qry, null);
        if(cursor.moveToFirst()){
            do{
                NewsItem item = new NewsItem();
                item.setNews_id(cursor.getString(0));
                item.setHeadline(cursor.getString(1));
                item.setLeague(league);
                list.add(item);
            }while (cursor.moveToNext());
        }
        return list;
    }

    public void storeHeadlines(List<NewsItem> list) {
        SQLiteDatabase db = this.getWritableDatabase();
        for(int i=0; i<list.size(); i++) {
            ContentValues values = new ContentValues();
            NewsItem item = list.get(i);
            values.put(KEY_NEWS_ID, item.getNews_id());
            values.put(KEY_HEADLINE, item.getHeadline());
            values.put(KEY_LEAGUE, item.getLeague());
            db.insert(TABLE_HEADLINE, null, values);
        }
        db.close();
    }

    public List<NewsItem> getHeadlines() {
        SQLiteDatabase db = this.getReadableDatabase();
        String qry = "SELECT * FROM " + TABLE_HEADLINE;
        Cursor cursor = db.rawQuery(qry, null);
        List<NewsItem> list = new ArrayList<NewsItem>();
        String csr = DatabaseUtils.dumpCursorToString(cursor);
        if(cursor.moveToFirst()){
            do{
                NewsItem item = new NewsItem();
                item.setNews_id(cursor.getString(0));
                item.setHeadline(cursor.getString(1));
                item.setLeague(cursor.getString(2));
                list.add(item);
            }while(cursor.moveToNext());
        }
        return list;
    }

    public boolean newsExists() {
        SQLiteDatabase db = this.getReadableDatabase();
        String qry = "SELECT * FROM " + TABLE_HEADLINE;
        Cursor cursor = db.rawQuery(qry, null);
        if(cursor.moveToFirst())
            return true;
        return false;
    }

    public boolean offlineAvailable() {
        SQLiteDatabase db = this.getReadableDatabase();
        String qry = "SELECT * FROM " + TABLE_NEWS;
        Cursor cursor = db.rawQuery(qry, null);
        if(cursor.moveToFirst())
            return true;
        return false;
    }

    public void clearTempTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_HEADLINE);
        Log.d("Message from DBHandler", "Table cleared");
        db.close();
    }

    public void deleteOfflineStory(String news_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NEWS + " WHERE " + KEY_NEWS_ID + " = '" + news_id + "'");
        db.close();
    }

}
