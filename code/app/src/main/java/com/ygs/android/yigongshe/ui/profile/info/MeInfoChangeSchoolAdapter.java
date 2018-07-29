package com.ygs.android.yigongshe.ui.profile.info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MeInfoChangeSchoolAdapter extends ArrayAdapter {

    private LayoutInflater infalter;
    private String[] items;
    private int resource;
    private int textViewResourceId;

    public MeInfoChangeSchoolAdapter(Context context ,int resource, int textViewResourceId, String[] objects){
        super(context,resource,textViewResourceId,objects);

        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = objects;

        infalter = LayoutInflater.from(context);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = infalter.inflate(resource, null);
        }
        TextView text = (TextView) convertView
            .findViewById(textViewResourceId);
        text.setText(items[position]);
        return convertView;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    public String nameAtIndex(int index){
        return this.items[index];
    }



}
