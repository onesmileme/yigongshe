package com.ygs.android.yigongshe.ui.community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.SortModel;
import java.util.ArrayList;

public class CityAdapter extends BaseAdapter implements SectionIndexer {

  private ArrayList<SortModel> list;
  private Context mContext;

  public CityAdapter(Context mContext, ArrayList<SortModel> list) {
    this.mContext = mContext;
    this.list = list;
  }

  public void updateListView(ArrayList<SortModel> list) {
    this.list = list;
    notifyDataSetChanged();
  }

  @Override public int getCount() {
    return this.list.size();
  }

  @Override public Object getItem(int position) {
    return list.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder;
    if (convertView == null) {
      viewHolder = new ViewHolder();
      convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_city_item, parent, false);
      viewHolder.tvCity = convertView.findViewById(R.id.tv_city);
      viewHolder.tvLetter = convertView.findViewById(R.id.tv_letter);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    if (null != list && !list.isEmpty()) {
      SortModel mContent = list.get(position);
      int section = getSectionForPosition(position);
      if (position == getPositionForSection(section)) {
        viewHolder.tvLetter.setVisibility(View.VISIBLE);
        viewHolder.tvLetter.setText(mContent.getSortLetters());
      } else {
        viewHolder.tvLetter.setVisibility(View.GONE);
      }
      viewHolder.tvCity.setText(mContent.getName());
    }
    return convertView;
  }

  class ViewHolder {
    TextView tvCity;
    TextView tvLetter;
  }

  @Override public int getSectionForPosition(int position) {
    return list.get(position).getSortLetters().charAt(0);
  }

  @Override public int getPositionForSection(int section) {
    for (int i = 0; i < getCount(); i++) {
      String sortStr = list.get(i).getSortLetters();
      char firstChar = sortStr.toUpperCase().charAt(0);
      if (firstChar == section) {
        return i;
      }
    }
    return -1;
  }

  @Override public Object[] getSections() {
    return null;
  }
}