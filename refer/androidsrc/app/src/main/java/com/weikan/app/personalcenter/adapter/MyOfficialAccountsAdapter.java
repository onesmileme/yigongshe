package com.weikan.app.personalcenter.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.weikan.app.R;
import com.weikan.app.personalcenter.bean.OfficialAccount;
import com.weikan.app.util.ImageLoaderUtil;
import java.util.List;

/**
 * Created by zhaorenhui on 2015/11/7.
 */
public class MyOfficialAccountsAdapter extends BaseAdapter {

    private Context context;
    private List<OfficialAccount> data;
    private LayoutInflater inflater;


    public MyOfficialAccountsAdapter(Context context,List<OfficialAccount> data){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }
    @Override
    public int getCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return data != null ? data.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.official_account_row, null);
            holder.ivPic = (ImageView) convertView.findViewById(R.id.iv_pic);
            holder.tvOaNickname = (TextView) convertView.findViewById(R.id.tv_oa_nickname);
            holder.tvCategory = (TextView) convertView.findViewById(R.id.tv_oa_category);
            holder.tvOfcAccount = (TextView) convertView.findViewById(R.id.tv_ofc_account);
            holder.tvOrderNum = (TextView) convertView.findViewById(R.id.tv_order_num);
            holder.tvAccumulatedIncome = (TextView) convertView.findViewById(R.id.tv_accumulated_income);
            holder.tvRanking = (TextView) convertView.findViewById(R.id.tv_ranking);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final OfficialAccount object = (OfficialAccount) getItem(position);
        if (!TextUtils.isEmpty(object.pic)) {
            ImageLoaderUtil.updateImage(holder.ivPic, object.pic, R.drawable.icon_share_qq);
        }
        if (!TextUtils.isEmpty(object.oaNickName)) {
            holder.tvOaNickname.setText(object.oaNickName);
        }
        if (!TextUtils.isEmpty(object.category)) {
            holder.tvCategory.setText(object.category);
        }
        if (!TextUtils.isEmpty(object.ofcAccount)) {
            holder.tvOfcAccount.setText(object.ofcAccount);
        }
        holder.tvOrderNum.setText(String.valueOf(object.orderNum));
        holder.tvAccumulatedIncome.setText(String.valueOf(object.accumulatedIncome));
        holder.tvRanking.setText(String.valueOf(object.ranking));
        return convertView;
    }


    public class ViewHolder{
        ImageView ivPic;

        TextView tvOaNickname;

        TextView tvCategory;

        TextView tvOfcAccount;

        TextView tvOrderNum;

        TextView tvAccumulatedIncome;

        TextView tvRanking;
    }
}
