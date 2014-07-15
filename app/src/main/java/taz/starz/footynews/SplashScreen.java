package taz.starz.footynews;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBarUtils;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;
import taz.starz.footynews.library.DatabaseHandler;
import taz.starz.footynews.library.NetworkFunctions;
import taz.starz.footynews.library.NewsItem;

/**
 * Created by Thahzan on 6/29/2014.
 */
public class SplashScreen extends Activity {

    private Animation fadeIn, fadeOut, pushRightIn, pushRightOut, pushLeftIn, pushLeftOut;

    private SmoothProgressBar mPocketBar;
    private TextView dwntext;

    private DatabaseHandler db;

    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        db = new DatabaseHandler(this);

        mPocketBar = (SmoothProgressBar) findViewById(R.id.pocket);
        mPocketBar.setSmoothProgressDrawableBackgroundDrawable(SmoothProgressBarUtils.generateDrawableWithColors(getResources().getIntArray(R.array.pocket_background_colors), ((SmoothProgressDrawable) mPocketBar.getIndeterminateDrawable()).getStrokeWidth()));

        dwntext = (TextView)findViewById(R.id.dwn_txt);

        fadeIn = AnimationUtils.loadAnimation(this, R.anim.abc_fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.abc_fade_out);
        pushLeftIn = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
        pushLeftOut = AnimationUtils.loadAnimation(this, R.anim.push_left_out);
        pushRightIn = AnimationUtils.loadAnimation(this, R.anim.push_right_in);
        pushRightOut = AnimationUtils.loadAnimation(this, R.anim.push_right_out);

        if(isNetworkAvailable()){
            db.clearTempTables();
            new LoadInitialData().execute();
        } else {
            //Proceed to offline reading activity
            if(db.offlineAvailable()){
                startActivity(new Intent(this, ViewOfflineActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.fadeout);
                finish();
            } else {
                Toast.makeText(SplashScreen.this, "Please turn on mobile data or WiFi", Toast.LENGTH_SHORT).show();
                monitorNetwork();
            }
        }

    }

    @Override
    protected void onDestroy() {
        if(receiver != null) {
            unregisterReceiver(receiver);
        }
        super.onDestroy();
    }

    private void monitorNetwork() {
        if(receiver == null) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle extras = intent.getExtras();
                    NetworkInfo info = (NetworkInfo) extras
                            .getParcelable("networkInfo");
                    NetworkInfo.State state = info.getState();

                    if (state == NetworkInfo.State.CONNECTED) {
                        Toast.makeText(SplashScreen.this, "Connected. Please wait.", Toast.LENGTH_SHORT).show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                db.clearTempTables();
                                new LoadInitialData().execute();
                            }
                        }, 1000);
                    } else {
                        Log.d("Network info", "Network if off");
                    }
                }
            };

            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(receiver, intentFilter);

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class LoadInitialData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dwntext.startAnimation(pushLeftIn);
            dwntext.setVisibility(View.VISIBLE);
            mPocketBar.startAnimation(fadeIn);
            mPocketBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JSONArray jsonArray = new NetworkFunctions().getInfo("getAllNews");
            List<NewsItem> list = new ArrayList<NewsItem>();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    NewsItem item = new NewsItem();
                    item.setNews_id(jsonObject.getString("news_id"));
                    item.setHeadline(jsonObject.getString("headline"));
                    item.setLeague(jsonObject.getString("league"));
                    list.add(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            db.storeHeadlines(list);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mPocketBar.startAnimation(fadeOut);
            mPocketBar.setVisibility(View.INVISIBLE);
            dwntext.startAnimation(fadeOut);
            dwntext.setVisibility(View.INVISIBLE);

            Handler handler = new Handler();
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    dwntext.setText("Download Finished");
                    dwntext.startAnimation(fadeIn);
                    dwntext.setVisibility(View.VISIBLE);
                }
            };
            handler.postDelayed(run, 300);

            transit();

        }
    }

    public void transit() {
        Handler handler = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.fadeout);
                finish();
            }
        };
        handler.postDelayed(run, 500);
    }

}
