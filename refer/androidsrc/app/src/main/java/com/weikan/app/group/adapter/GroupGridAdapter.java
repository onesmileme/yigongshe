package com.weikan.app.group.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.weikan.app.R;
import com.weikan.app.group.bean.GroupDetailBean;

import java.util.List;

/**
 * Created by ylp on 2017/1/8.
 */

public class GroupGridAdapter extends BaseAdapter {
    private List<GroupDetailBean> list;
    private Context context;
    public GroupGridAdapter(Context context){
        this.context = context;
    }

    public List<GroupDetailBean> getList() {
        return list;
    }

    public void setList(List<GroupDetailBean> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null || list.size() == 0 ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null || list.size() == 0 ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_wenyou_pub_group,null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView)convertView.findViewById(R.id.group_name);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        GroupDetailBean groupDetailBean = list.get(position);
        if(groupDetailBean.isSelected == 1){
            viewHolder.name.setTextColor(context.getResources().getColor(R.color.white));
            viewHolder.name.setBackgroundResource(R.drawable.icon_wenyou_pub_select);
        }else{
            if(position == list.size() - 1){
                viewHolder.name.setBackgroundResource(R.drawable.icon_wenyou_pub_more);
            }else{
                viewHolder.name.setTextColor(context.getResources().getColor(R.color.wenyou_group));
                viewHolder.name.setBackgroundResource(R.drawable.icon_wenyou_pub_unselect);
            }
        }
        if(TextUtils.isEmpty(groupDetailBean.groupName)){
            viewHolder.name.setText("");
        }else{
            viewHolder.name.setText(groupDetailBean.groupName);
        }
        return convertView;
    }
    static class ViewHolder{
        TextView name;
    }
}
