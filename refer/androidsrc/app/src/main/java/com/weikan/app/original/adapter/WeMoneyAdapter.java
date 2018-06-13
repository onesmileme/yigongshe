package com.weikan.app.original.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weikan.app.R;
import com.weikan.app.original.bean.OriginalItem;
import com.weikan.app.util.ImageLoaderUtil;

import java.util.List;

/**
 * Created by ylp on 2016/10/18.
 */

public class WeMoneyAdapter extends OriginalMainAdapter {
    private Context context;
    private Context mContext;
    private LayoutInflater mInflater;
    private List<OriginalItem> mOriginalList;
    private boolean isAvatarClickable = true;
    private boolean isShowTime = true;
    private boolean isShowTags = true;
    private OnShareClickListener onShareClickListener;
    private OnPraiseClickListener onPraiseClickListener;

    public WeMoneyAdapter (Context context){
        super(context);
        this.context = context;
    }
    public void setContent(List<OriginalItem> originalObjectList) {
        mOriginalList = originalObjectList;
        notifyDataSetChanged();
    }

    public void setAvatarClickable(boolean able){
        isAvatarClickable = able;
    }

    public void setShowTime(boolean showTime){
        isShowTime = showTime;
    }

    public void setShowTags(boolean showTags) {
        isShowTags = showTags;
    }
    @Override
    public int getCount() {
        if (mOriginalList != null && mOriginalList.size() != 0) {
            return mOriginalList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mOriginalList != null) {
            return mOriginalList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_shouye_wemoney,null);
            viewHolder.ivHead = (ImageView)convertView.findViewById(R.id.iv_wemoney_head);
            viewHolder.ivPhoto = (ImageView)convertView.findViewById(R.id.iv_wemoney_photo);
            viewHolder.tvDes = (TextView)convertView.findViewById(R.id.tv_wemoney_des);
            viewHolder.tvName = (TextView)convertView.findViewById(R.id.tv_wemoney_name);
            viewHolder.tvTime = (TextView)convertView.findViewById(R.id.tv_wemoney_time);
            viewHolder.tvTitle = (TextView)convertView.findViewById(R.id.tv_wemoney_tite);
            viewHolder.tvYuedu = (TextView)convertView.findViewById(R.id.tv_wemoney_yuedu);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        OriginalItem originalListItem = mOriginalList.get(position);
        if(!TextUtils.isEmpty(originalListItem.headimgurl)){
            ImageLoaderUtil.updateImage(viewHolder.ivHead,originalListItem.headimgurl,R.drawable.user_default);
        }else{
            viewHolder.ivHead.setImageResource(R.drawable.user_default);
        }
        if(!TextUtils.isEmpty(originalListItem.pic.s.url)){
            ImageLoaderUtil.updateImage(viewHolder.ivPhoto,originalListItem.pic.s.url,R.drawable.image_bg);
        }else{
            viewHolder.ivPhoto.setImageResource(R.drawable.image_bg);
        }
        viewHolder.tvYuedu.setText(Integer.toString(originalListItem.read_num));
        viewHolder.tvTitle.setText(originalListItem.title);
        viewHolder.tvDes.setText(originalListItem.contentAbstract);
        viewHolder.tvTime.setText(String.valueOf(getShowTime(originalListItem.pubtime)));
        viewHolder.tvName.setText(originalListItem.author);
        return convertView;
    }
    class ViewHolder{
        ImageView ivHead;
        TextView tvName;
        ImageView ivPhoto;
        TextView tvTitle;
        TextView tvDes;
        TextView tvTime;
        TextView tvYuedu;
    }
}
