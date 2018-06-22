package com.ygs.android.yigongshe.ui.profile.record;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.MeRecordItemBean;

import java.util.List;

public class MeRecordAdapter extends BaseQuickAdapter<MeRecordItemBean,BaseViewHolder> {

    List<MeRecordItemBean> itemBeans;

    public MeRecordAdapter(Context context){
        super(R.layout.item_me_record,null);

    }

    @Override
    protected void convert(BaseViewHolder helper, MeRecordItemBean item) {

        helper.setText(R.id.me_record_title_tv,item.name);
        helper.setText(R.id.me_record_time_tv,item.time);
        helper.setText(R.id.me_record_duration_tv,item.duration);

    }

    @Override
    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        super.setOnItemClickListener(listener);

    }
}
