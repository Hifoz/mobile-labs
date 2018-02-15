package com.example.hifoz.lab2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/*
* Custom adapter for rss items
*
* */
public class RSSItemAdapter extends ArrayAdapter<RSSItem> {
    private final Context context;
    private final ArrayList<RSSItem> items;


    public RSSItemAdapter(Context context, ArrayList<RSSItem> items) {
        super(context,R.layout.rss_item_layout, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder vh = new ViewHolder();
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.rss_item_layout, parent, false);

            vh.title = convertView.findViewById(R.id.itemTitle);
            vh.description = convertView.findViewById(R.id.itemDesc);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder)convertView.getTag();
        }

        vh.title.setText(items.get(position).getTitle());
        vh.description.setText(items.get(position).getDesc());

        return convertView;
    }

    private class ViewHolder{
        TextView title;
        TextView description;
    }
}
