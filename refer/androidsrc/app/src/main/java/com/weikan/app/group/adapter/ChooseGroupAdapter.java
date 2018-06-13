package com.weikan.app.group.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weikan.app.R;
import com.weikan.app.group.bean.GroupDetailBean;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.widget.roundedimageview.CircleImageView;

import java.util.List;

/**
 * Created by ylp on 2017/1/8.
 */

public class ChooseGroupAdapter extends BaseAdapter{
    private List<GroupDetailBean> list;
    private Context context;
    private String chooseId;

    public List<GroupDetailBean> getList() {
        return list;
    }

    public void setList(List<GroupDetailBean> list) {
        this.list = list;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getChooseId() {
        return chooseId;
    }

    public void setChooseId(String chooseId) {
        this.chooseId = chooseId;
    }
    public ChooseGroupAdapter(Context context) {
        this.context = context;
    }
    @Override
    public int getCount() {
        return list == null || list.size() == 0 ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null || list.size() == 0 ? 0 : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_choose_group,null);
            viewHolder = new ViewHolder();
            viewHolder.photo = (CircleImageView)convertView.findViewById(R.id.iv_avatar);
            viewHolder.tvGroupName = (TextView)convertView.findViewById(R.id.tv_group_name);
            viewHolder.imCheck = (ImageView)convertView.findViewById(R.id.iv_group_check);
            viewHolder.line1 = convertView.findViewById(R.id.line1);
            viewHolder.line2 = convertView.findViewById(R.id.line2);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        GroupDetailBean groupDetailBean = list.get(position);
        try{
            if(TextUtils.isEmpty(groupDetailBean.avatar.getImageUrlLittle())){
                viewHolder.photo.setImageResource(R.drawable.ic_launcher);
            }else{
                ImageLoaderUtil.updateImage(viewHolder.photo,groupDetailBean.avatar.getImageUrlLittle(),R.drawable.ic_launcher);
            }
            viewHolder.tvGroupName.setText(groupDetailBean.groupName);
            if(groupDetailBean.groupId.equals(chooseId)){
                viewHolder.imCheck.setVisibility(View.VISIBLE);
            }else{
                viewHolder.imCheck.setVisibility(View.GONE);
            }
            if(position == list.size() - 1){
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
        CircleImageView photo;
        TextView tvGroupName;
        ImageView imCheck;
        View line1;
        View line2;
    }
}
