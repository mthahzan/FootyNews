package taz.starz.footynews;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBarUtils;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;
import taz.starz.footynews.library.DatabaseHandler;
import taz.starz.footynews.library.NetworkFunctions;
import taz.starz.footynews.library.NewsItem;
import taz.starz.footynews.library.SharedPref;

/**
 * Created by Thahzan on 7/1/2014.
 */
public class ViewStoryActivity extends Activity {

    private TextView dwn_txt, storyBody, headline;
    private SmoothProgressBar progressBar;
    private ImageView storyImage;
    private RelativeLayout layout;

    private NetworkFunctions funcs;
    private SharedPref pref;

    private DatabaseHandler db;

    private int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_story);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;

        db = new DatabaseHandler(this);
        funcs = new NetworkFunctions();
        pref = new SharedPref(this);

        layout = (RelativeLayout)findViewById(R.id.view_story_progress_layout);

        progressBar = (SmoothProgressBar)findViewById(R.id.pocket_view_story);
        progressBar.setSmoothProgressDrawableBackgroundDrawable(SmoothProgressBarUtils.generateDrawableWithColors(getResources().getIntArray(R.array.pocket_background_colors), ((SmoothProgressDrawable) progressBar.getIndeterminateDrawable()).getStrokeWidth()));

        dwn_txt = (TextView)findViewById(R.id.dwn_txt_view_story);
        storyBody = (TextView)findViewById(R.id.news_body);
        storyImage = (ImageView)findViewById(R.id.story_image);
        headline = (TextView)findViewById(R.id.news_headline);

        new GetArticleInfo().execute(pref.getSelectedNews());
    }

    private class GetArticleInfo extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            progressBar.startAnimation(AnimationUtils.loadAnimation(ViewStoryActivity.this, R.anim.abc_fade_in));
            progressBar.setVisibility(View.VISIBLE);
            dwn_txt.startAnimation(AnimationUtils.loadAnimation(ViewStoryActivity.this, R.anim.push_right_in));
            dwn_txt.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... integers) {

            JSONObject details = funcs.getNews(integers[0]);
            if(details.has("body")){
                return details;
            }

            return null;
        }

        @Override
        protected void onPostExecute(final JSONObject json) {
            if(json != null) {
                try {
                    pref.setViewvingNews(json);
                    storyBody.setText(json.getString("body"));
                    headline.setText(json.getString("headline"));
                    if(json.has("img_url")){
                        Ion.with(ViewStoryActivity.this).load(json.getString("img_url")).withBitmap().resize(width, width).centerInside().intoImageView(storyImage);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                storyBody.setText("Error");
            }
            Handler handler = new Handler();
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    layout.startAnimation(AnimationUtils.loadAnimation(ViewStoryActivity.this, R.anim.abc_fade_out));
                    layout.setVisibility(View.GONE);
                    storyBody.startAnimation(AnimationUtils.loadAnimation(ViewStoryActivity.this, R.anim.abc_fade_in));
                    storyBody.setVisibility(View.VISIBLE);
                    headline.startAnimation(AnimationUtils.loadAnimation(ViewStoryActivity.this, R.anim.abc_fade_in));
                    headline.setVisibility(View.VISIBLE);
                    storyImage.startAnimation(AnimationUtils.loadAnimation(ViewStoryActivity.this, R.anim.abc_fade_in));
                    storyImage.setVisibility(View.VISIBLE);
                }
            };
            handler.postDelayed(run, 600);

            super.onPostExecute(json);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Save Story");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getTitle().equals("Save Story")){
//            startActivity(new Intent(ViewStoryActivity.this, DownloadStoryActivity.class));
            NewsItem store = new NewsItem();
            try {
                JSONObject json = pref.getViewingNews();
                store.setNews_id(json.getString("news_id"));
                store.setHeadline(json.getString("headline"));
                store.setBody(json.getString("body"));
                store.setLeague(json.getString("league"));
                store.setSource(json.getString("source"));
                if(!db.isStoryAlreadySaved(store.getHeadline())){
                    db.storeNewsItems(store);
                    Toast.makeText(ViewStoryActivity.this, "Story saved to read offline later", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ViewStoryActivity.this, "Story already saved", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
