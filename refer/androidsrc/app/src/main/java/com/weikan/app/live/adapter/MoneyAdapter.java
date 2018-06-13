package com.weikan.app.live.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.weikan.app.R;
import com.weikan.app.live.bean.MoneyDogBean;

import java.util.List;

/**
 * Created by ylp on 2016/11/15.
 */

public class MoneyAdapter extends BaseAdapter {
    private Context context;
    private List<MoneyDogBean> moneyList;
    public MoneyAdapter(Context context){
        this.context = context;
    }

    public void setMoneyList(List<MoneyDogBean> moneyList) {
        this.moneyList = moneyList;
    }

    @Override
    public int getCount() {
        return moneyList == null || moneyList.size() == 0 ? 0:moneyList.size();
    }

    @Override
    public Object getItem(int position) {
        return moneyList == null || moneyList.size() == 0 ? null:moneyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_pay_money,null);
            viewHolder = new ViewHolder();
            viewHolder.tvDog = (TextView)convertView.findViewById(R.id.tv_dog);
            viewHolder.tvMoney = (TextView)convertView.findViewById(R.id.tv_money);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        MoneyDogBean bean = moneyList.get(position);
        viewHolder.tvDog.setText(Integer.toString(bean.dog));
        viewHolder.tvMoney.setText("¥"+Integer.toString(bean.money));
        return convertView;
    }
    class ViewHolder{
        TextView tvDog;
        TextView tvMoney;
    }
}
