package taz.starz.footynews;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import taz.starz.footynews.library.DatabaseHandler;

/**
 * Created by Thahzan on 7/6/2014.
 */
public class OfflineReadActivity extends Activity {

    private DatabaseHandler db;
    int news_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_offline);

        db = new DatabaseHandler(this);

        TextView headline = (TextView)findViewById(R.id.read_offline_headline);
        TextView body = (TextView)findViewById(R.id.read_offline_body);

        headline.setText(getIntent().getStringExtra("head"));
        body.setText(getIntent().getStringExtra("body"));

    }

}
