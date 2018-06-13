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
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.widget.roundedimageview.CircleImageView;

import java.util.List;

/**
 * Created by ylp on 2017/1/8.
 */

public class FollowGroupAdapter extends BaseAdapter {
    private List<GroupDetailBean> list;
    private Context context;
    public FollowGroupAdapter(Context context){
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
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_my_group_follow,null);
            viewHolder = new ViewHolder();
            viewHolder.groupPhoto = (CircleImageView)convertView.findViewById(R.id.iv_avatar);
            viewHolder.tvName = (TextView)convertView.findViewById(R.id.tv_group_name);
            viewHolder.tvFollowNum = (TextView)convertView.findViewById(R.id.tv_group_follow_num);
            viewHolder.tvGrohpInfo = (TextView)convertView.findViewById(R.id.tv_group_info);
            viewHolder.line1 = convertView.findViewById(R.id.line1);
            viewHolder.line2 = convertView.findViewById(R.id.line2);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        GroupDetailBean groupDetailBean = list.get(position);
        try{
            if(TextUtils.isEmpty(groupDetailBean.avatar.getImageUrlLittle())){
                viewHolder.groupPhoto.setImageResource(R.drawable.ic_launcher);
            }else{
                ImageLoaderUtil.updateImage(viewHolder.groupPhoto,groupDetailBean.avatar.getImageUrlLittle(),R.drawable.ic_launcher);
            }
            viewHolder.tvName.setText(groupDetailBean.groupName);
            viewHolder.tvFollowNum.setText(groupDetailBean.followCount+"人");
            viewHolder.tvGrohpInfo.setText(groupDetailBean.intro);
            if(position == list.size()-1){
                viewHolder.line1.setVisibility(View.GONE);
                viewHolder.line2.setVisibility(View.VISIBLE);
            }else{
                viewHolder.line1.setVisibility(View.VISIBLE);
                viewHolder.line2.setVisibility(View.GONE);
            }
        }catch (Exception e){}
        return convertView;
    }
    static class ViewHolder{
        CircleImageView groupPhoto;
        TextView tvName;
        TextView tvFollowNum;
        TextView tvGrohpInfo;
        View line1;
        View line2;
    }
}
