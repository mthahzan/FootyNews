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
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import taz.starz.footynews.adapter.CustomOfflineReaderAdapter;
import taz.starz.footynews.library.DatabaseHandler;
import taz.starz.footynews.library.NewsItem;

/**
 * Created by Thahzan on 7/6/2014.
 */
public class ViewOfflineActivity extends Activity {

    private DatabaseHandler db;

    private BroadcastReceiver receiver;

    private ListView listView;

    private boolean first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        db = new DatabaseHandler(this);

        listView = (ListView)findViewById(R.id.offline_read_listview);
        CustomOfflineReaderAdapter adapter = new CustomOfflineReaderAdapter(this, db.getAllNews());
        listView.setAdapter(adapter);

        monitorNetwork();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NewsItem item = db.getNewsOfId(db.getAllNews().get(i).getNews_id());
                Intent intent = new Intent(ViewOfflineActivity.this, OfflineReadActivity.class);
                intent.putExtra("head", item.getHeadline());
                intent.putExtra("body", item.getBody());
                intent.putExtra("source", item.getSource());
                intent.putExtra("news_id", item.getNews_id());
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, final long l) {
                final NewsItem item = db.getAllNews().get(i);
                AlertDialog dialog = new AlertDialog.Builder(ViewOfflineActivity.this)
                        .setMessage("Do you want to remove this story?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                db.deleteOfflineStory(item.getNews_id());
                                refreshItems();
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .create();
                dialog.show();
                return true;
            }
        });

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
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
                        if(!first) {
                            AlertDialog alert = new AlertDialog.Builder(context)
                                    .setMessage("Would you like to read latest news?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(new Intent(ViewOfflineActivity.this, SplashScreen.class));
                                            finish();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    })
                                    .create();
                            alert.show();
                        } else {
                            first = false;
                        }
                    } else {
                        first = false;
                        Log.d("Network info", "Network if off");
                    }
                }
            };

            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(receiver, intentFilter);

        }
    }

    public void refreshItems() {
        listView.setAdapter(new CustomOfflineReaderAdapter(ViewOfflineActivity.this, db.getAllNews()));
        if(!db.offlineAvailable()){
            TextView emptyTxt = (TextView)findViewById(R.id.offline_nothing_txt);
            emptyTxt.startAnimation(AnimationUtils.loadAnimation(ViewOfflineActivity.this, R.anim.abc_fade_in));
            emptyTxt.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("See latest news");
        return super.onCreateOptionsMenu(menu);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getTitle().equals("See latest news")) {
            if(isNetworkAvailable()){
                startActivity(new Intent(ViewOfflineActivity.this, SplashScreen.class));
                finish();
            } else {
                Toast.makeText(ViewOfflineActivity.this, "Please switch on mobile data or WiFi", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
