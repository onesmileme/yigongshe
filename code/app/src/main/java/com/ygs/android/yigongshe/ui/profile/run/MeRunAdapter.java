package com.ygs.android.yigongshe.ui.profile.run;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.MeRunBean;

import java.util.List;

public class MeRunAdapter extends BaseQuickAdapter<MeRunBean,BaseViewHolder> {

    Context mContext;
    MeRunAdapter(Context context){
        super(R.layout.item_me_run,null);
        mContext = context;
    }

    public void updateList(List<MeRunBean> items){

        this.setNewData(items);
    }

    @Override
    protected void convert(BaseViewHolder helper, MeRunBean item) {

        TextView myIndexTv = helper.getView(R.id.me_run_me_index_tv);
        TextView indexTv = helper.getView(R.id.me_run_index_tv);
        TextView nameTv = helper.getView(R.id.me_run_name_tv);
        if (item.isMe){
            myIndexTv.setVisibility(View.VISIBLE);
            indexTv.setVisibility(View.GONE);
            int color = mContext.getResources().getColor(R.color.black1);
            nameTv.setTextColor(color);
        }else{
            myIndexTv.setVisibility(View.GONE);
            indexTv.setVisibility(View.VISIBLE);
            int color = mContext.getResources().getColor(R.color.gray1);
            nameTv.setTextColor(color);
        }
        nameTv.setText(item.name);

        helper.setText(R.id.me_run_steps_tv,item.steps);
    }


}
