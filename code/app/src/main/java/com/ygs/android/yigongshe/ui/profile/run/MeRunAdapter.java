package com.ygs.android.yigongshe.ui.profile.run;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.MeRunBean;
import com.ygs.android.yigongshe.bean.RunItemBean;
import com.ygs.android.yigongshe.utils.ImageLoadUtil;

import java.util.List;

public class MeRunAdapter extends BaseQuickAdapter<RunItemBean, BaseViewHolder> {

    Context mContext;

    MeRunAdapter(Context context) {
        super(R.layout.item_me_run, null);
        mContext = context;
    }

    @Override
    public void convert(BaseViewHolder helper, RunItemBean item) {

        TextView myIndexTv = helper.getView(R.id.me_run_me_index_tv);
        TextView indexTv = helper.getView(R.id.me_run_index_tv);
        TextView nameTv = helper.getView(R.id.me_run_name_tv);
        List<RunItemBean>datas = this.getData();
        int position = datas.indexOf(item);
        if (position == 0) {//this.getItem(0) == item
            myIndexTv.setVisibility(View.VISIBLE);
            myIndexTv.setText("第"+item.rank+"名");
            indexTv.setVisibility(View.GONE);
            int color = mContext.getResources().getColor(R.color.black1);
            nameTv.setTextColor(color);
        } else {
            myIndexTv.setVisibility(View.GONE);
            indexTv.setVisibility(View.VISIBLE);
            int color = mContext.getResources().getColor(R.color.gray1);
            nameTv.setTextColor(color);
            indexTv.setText(""+(position));
        }
        nameTv.setText(item.username);

        ImageView avatarImageView = helper.getView(R.id.me_run_avatar_iv);
        ImageLoadUtil.loadImage(avatarImageView, item.avatar,R.drawable.defalutavar);
        helper.setText(R.id.me_run_steps_tv, item.step_count + "");
    }

}
