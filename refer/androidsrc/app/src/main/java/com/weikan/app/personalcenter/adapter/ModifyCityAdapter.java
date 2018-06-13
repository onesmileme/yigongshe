package com.weikan.app.personalcenter.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.weikan.app.personalcenter.widget.CityCellView;
import com.weikan.app.personalcenter.widget.CitySectionView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 选择所在城市的Adpter
 * @author kailun on 15/12/14
 */
public class ModifyCityAdapter extends BaseAdapter {

    final Context context;
    final List<Object> citiesWithChars = new ArrayList<>();

    @NonNull String chooseCity = "";

    public ModifyCityAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    public String getChooseCity() {
        return this.chooseCity;
    }

    public void setChooseCity(@NonNull String city) {
        this.chooseCity = city;
    }

    public void setCityList(@NonNull Map<Character, List<String>> cities) {

        citiesWithChars.clear();

        // 把城市首字母的名字，比如B
        // 和已这个字母开头的所有城市名字，比如北京市，本溪市
        // 都加入同一个List里，便于Adapter处理
        for(Character c: cities.keySet()) {
            citiesWithChars.add(c);

            List<String> cs = cities.get(c);
            for (String city: cs) {
                citiesWithChars.add(city);
            }
        }
    }

    @Override
    public int getCount() {
        return citiesWithChars.size();
    }

    @Override
    public Object getItem(int position) {
        return citiesWithChars.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Object item = getItem(position);
        if (item instanceof Character) {

            Character c = (Character) item;

            CitySectionView view = null;
            if (convertView instanceof CitySectionView) {
                view = (CitySectionView) convertView;
            } else {
                view = new CitySectionView(context);
            }
            view.setText(c.toString());
            return view;

        } else if (item instanceof String) {

            String s = (String) item;

            CityCellView view;
            if (convertView instanceof CityCellView) {
                view = (CityCellView) convertView;
            } else {
                view = new CityCellView(context);
                view.setOnClickListener(clickListener);
            }
            view.setText(s);

            // 判断是否是选中的城市
            view.setChoose(TextUtils.equals(chooseCity, s));

            // 判断是否是最后一项，或者下一项是否是Character
            boolean isLast = (position + 1 == getCount()) ||
                    (getItem(position + 1) instanceof Character);
            view.setSplitterVisible(!isLast);

            return view;
        }

        throw new RuntimeException("unexpected item type: " + item.getClass().getName());
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CityCellView view = (CityCellView) v;
            chooseCity = view.getText();
            notifyDataSetChanged();
        }
    };
}
