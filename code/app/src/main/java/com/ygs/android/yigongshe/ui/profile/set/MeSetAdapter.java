package com.ygs.android.yigongshe.ui.profile.set;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ygs.android.yigongshe.R;

import java.util.LinkedList;
import java.util.List;

public class MeSetAdapter extends RecyclerView.Adapter<MeSetViewHolder> {

    private List<String> titleList;

    private Context context;

    public MeSetAdapter(Context context){

        this.context = context;
        titleList = new LinkedList<>();
        titleList.add("常见问题");
        titleList.add("关于我们");
        titleList.add("清除缓存");
        titleList.add("用户协议");
    }

    @Override
    public MeSetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_me_set,parent);
        MeSetViewHolder viewHolder = new MeSetViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MeSetViewHolder holder, int position) {

        TextView titleView = holder.titleView;
        titleView.setText(titleList.get(position));
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }
}


class MeSetViewHolder extends  RecyclerView.ViewHolder {

    TextView titleView;

    public MeSetViewHolder(View view){
        super(view);
        titleView = (TextView)view.findViewById(R.id.me_set_item_title);
    }

}