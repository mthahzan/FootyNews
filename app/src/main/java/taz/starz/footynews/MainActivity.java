package taz.starz.footynews;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nirhart.parallaxscroll.views.ParallaxListView;

import org.arasthel.googlenavdrawermenu.views.GoogleNavigationDrawer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import taz.starz.footynews.adapter.CustomListAdapter;
import taz.starz.footynews.library.DatabaseHandler;
import taz.starz.footynews.library.NetworkFunctions;
import taz.starz.footynews.library.SharedPref;


public class MainActivity extends ActionBarActivity {

    private ActionBarDrawerToggle drawerToggle;
    private GoogleNavigationDrawer mDrawer;

    private ProgressDialog pDialog;

    private NetworkFunctions funcs;
    private DatabaseHandler db;
    private SharedPref pref;

    private ImageView parallaxImg;
    private ParallaxListView list;

    private Animation fadeIn, fadeOut;

    private CustomListAdapter adapter;

    private int curImg;

    private BroadcastReceiver receiver;

    private ActionBar actionBar;

    private TextView networkIndicator;

    private int sel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        funcs = new NetworkFunctions();
        db = new DatabaseHandler(this);
        pref = new SharedPref(this);

        networkIndicator = (TextView) findViewById(R.id.main_network_indicator);

        fadeIn = AnimationUtils.loadAnimation(this, R.anim.abc_fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.abc_fade_out);

        mDrawer = (GoogleNavigationDrawer) findViewById(R.id.navigation_drawer_container);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawer, R.drawable.ic_drawer, R.string.app_name, R.string.app_name);

        mDrawer.setDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        actionBar = getActionBar();
        monitorNetwork();

        list = (ParallaxListView) findViewById(R.id.news_listview);
        parallaxImg = new ImageView(this);
        parallaxImg.setImageResource(R.drawable.wall_main);
        parallaxImg.setMaxHeight(256);
        parallaxImg.setScaleType(ImageView.ScaleType.CENTER_CROP);

        adapter = new CustomListAdapter(this, db.getHeadlines());
        list.addParallaxedHeaderView(parallaxImg);
        list.setAdapter(adapter);

        mDrawer.setOnNavigationSectionSelected(new GoogleNavigationDrawer.OnNavigationSectionSelected() {
            @Override
            public void onSectionSelected(View view, int i, long l) {
                curImg = i;
                sel = i;
                if (i == 0) {
                    Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                    changeImageResource(R.drawable.wall_main);
                    setListItems("All");
                }
                if (i == 1) {
                    Toast.makeText(MainActivity.this, "International", Toast.LENGTH_SHORT).show();
                    changeImageResource(R.drawable.wall_international);
                    setListItems("International");
                }
                if (i == 2) {
                    Toast.makeText(MainActivity.this, "Premier League", Toast.LENGTH_SHORT).show();
                    changeImageResource(R.drawable.wall_prem);
                    setListItems("Premier League");
                }
                if (i == 3) {
                    Toast.makeText(MainActivity.this, "Liga BBVA", Toast.LENGTH_SHORT).show();
                    changeImageResource(R.drawable.wall_liga);
                    setListItems("La Liga");
                }
                if (i == 4) {
                    Toast.makeText(MainActivity.this, "Bundesliga", Toast.LENGTH_SHORT).show();
                    changeImageResource(R.drawable.wall_bundesliga);
                    setListItems("Bundesliga");
                }
                if (i == 5) {
                    Toast.makeText(MainActivity.this, "Serie A", Toast.LENGTH_SHORT).show();
                    changeImageResource(R.drawable.wall_seriea);
                    setListItems("Serie A");
                }
                if (i == 6) {
                    Toast.makeText(MainActivity.this, "UEFA Champions League", Toast.LENGTH_SHORT).show();
                    changeImageResource(R.drawable.wall_champs);
                    setListItems("Champions League");
                }
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!(i == 0)) {
                    if (isNetworkAvailable()) {

//                        pref.setSelectedNews(db.getHeadlines().get(i-1).getNews_id());
                        if(sel == 0) {
                            pref.setSelectedNews(db.getHeadlines().get(i-1).getNews_id());
                        } else if (sel == 1) {
                            pref.setSelectedNews(db.getLeagueItems("International").get(i-1).getNews_id());
                        } else if (sel == 2) {
                            pref.setSelectedNews(db.getLeagueItems("Premier League").get(i-1).getNews_id());
                        } else if (sel == 3) {
                            pref.setSelectedNews(db.getLeagueItems("La Liga").get(i-1).getNews_id());
                        } else if (sel == 4) {
                            pref.setSelectedNews(db.getLeagueItems("Bundesliga").get(i-1).getNews_id());
                        } else if (sel == 5) {
                            pref.setSelectedNews(db.getLeagueItems("Serie A").get(i-1).getNews_id());
                        } else if (sel == 6) {
                            pref.setSelectedNews(db.getLeagueItems("Champions League").get(i-1).getNews_id());
                        }
                        startActivity(new Intent(MainActivity.this, ViewStoryActivity.class));
                        overridePendingTransition(R.anim.push_left_in, R.anim.fadeout);
                    } else {
                        Toast.makeText(MainActivity.this, "Please switch on mobile data or WiFi", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void monitorNetwork() {
        if (receiver == null) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle extras = intent.getExtras();
                    NetworkInfo info = (NetworkInfo) extras
                            .getParcelable("networkInfo");
                    NetworkInfo.State state = info.getState();

                    if (state == NetworkInfo.State.CONNECTED) {
                        Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
                        animateActionBar(true);
                        Log.d("Network Info", "Network is on");
                    } else {
                        Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
                        animateActionBar(false);
                        Log.d("Network info", "Network if off");
                    }
                }
            };
            final IntentFilter intentFilter = new android.content.IntentFilter();
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

    private void animateActionBar(boolean state) {
        int colOnline = getResources().getColor(R.color.online_colour);
        int colOffline = getResources().getColor(R.color.offline_colour);
        if (state) {
            final ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), colOffline, colOnline).setDuration(1000);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    actionBar.setBackgroundDrawable(new ColorDrawable((Integer) animation.getAnimatedValue()));
                }
            });

            animator.start();
        } else {
            final ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), colOnline, colOffline).setDuration(1000);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    actionBar.setBackgroundDrawable(new ColorDrawable((Integer) animation.getAnimatedValue()));
                }
            });

            animator.start();
        }
    }

    public void setListItems(String league) {
        list.startAnimation(fadeOut);
        list.setVisibility(View.INVISIBLE);
        if (league.equals("All")) {
            list.setAdapter(new CustomListAdapter(MainActivity.this, db.getHeadlines()));
        } else {
            list.setAdapter(new CustomListAdapter(MainActivity.this, db.getLeagueItems(league)));
        }
        list.startAnimation(fadeIn);
        list.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        db.clearTempTables();
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("img_key", curImg);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        int img = savedInstanceState.getInt("img_key");
        //And change the news items too
        if (img == 1) {
            changeImageResource(R.drawable.wall_international);
        } else if (img == 2) {
            changeImageResource(R.drawable.wall_prem);
        } else if (img == 3) {
            changeImageResource(R.drawable.wall_liga);
        } else if (img == 4) {
            changeImageResource(R.drawable.wall_bundesliga);
        } else if (img == 5) {
            changeImageResource(R.drawable.wall_seriea);
        } else if (img == 6) {
            changeImageResource(R.drawable.wall_champs);
        } else {
            changeImageResource(R.drawable.wall_main);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void changeImageResource(int imgId) {
        parallaxImg.startAnimation(fadeOut);
        parallaxImg.setVisibility(View.INVISIBLE);
        parallaxImg.setImageResource(imgId);
        parallaxImg.startAnimation(fadeIn);
        parallaxImg.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawer != null) {
                if (mDrawer.isDrawerMenuOpen()) {
                    mDrawer.closeDrawerMenu();
                } else {
                    mDrawer.openDrawerMenu();
                }
            }
        } else if (item.getTitle().equals("Switch to offline mode")) {
            if(db.offlineAvailable()){
                startActivity(new Intent(MainActivity.this, ViewOfflineActivity.class));
                finish();
            } else {
                Toast.makeText(MainActivity.this, "No stories available to read offline", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Switch to offline mode");
        return super.onCreateOptionsMenu(menu);
    }

}
