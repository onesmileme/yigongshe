package com.ygs.android.yigongshe.ui.profile.set;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;

import java.util.LinkedList;
import java.util.List;

public class MeSetAdapter extends BaseQuickAdapter<String , BaseViewHolder> {

    private List<String> titleList;

    public static final int FRQ_QUESTION = 0;
    public static final int ABOUT_US = 1;
    public static final int CLEAR_CACHE = 2;
    public static final int USER_PROTOCOL = 3;

    public MeSetAdapter(Context context){

        super(R.layout.item_me_set,null);

        titleList = new LinkedList<>();
        titleList.add("常见问题");
        titleList.add("关于我们");
        titleList.add("清除缓存");
        titleList.add("用户协议");

        setNewData(titleList);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {

        helper.setText(R.id.me_set_item_title,item);

    }

//    @Override
//    public MeSetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_me_set,parent);
//        MeSetViewHolder viewHolder = new MeSetViewHolder(view);
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(MeSetViewHolder holder, int position) {
//
//        TextView titleView = holder.titleView;
//        Log.e("ME", "onBindViewHolder: position is "+position );
//        titleView.setText(titleList.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return titleList.size();
//    }
}


