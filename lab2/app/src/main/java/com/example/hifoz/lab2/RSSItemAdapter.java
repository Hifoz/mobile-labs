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
public class RSSItemAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<RSSItem> items;


    public RSSItemAdapter(Context context, ArrayList<RSSItem> items) {
        super(context, -1);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.rss_item_layout, parent, false);

        TextView title = itemView.findViewById(R.id.itemTitle);
        TextView desc = itemView.findViewById(R.id.itemDesc);
        title.setText(items.get(position).getTitle());
        desc.setText(items.get(position).getDesc());

        return itemView;
    }


}
