package taz.starz.footynews.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import taz.starz.footynews.R;
import taz.starz.footynews.library.NewsItem;

/**
 * Created by Thahzan on 6/29/2014.
 */
public class CustomListAdapter extends BaseAdapter {

    private Context context;
    private List<NewsItem> list;

    public CustomListAdapter(Context context, List<NewsItem> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.main_list_item, null);
        }
        TextView txt = (TextView)view.findViewById(R.id.list_item_text);
        txt.setText(list.get(i).getHeadline());

        return view;
    }
}
