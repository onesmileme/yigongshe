package com.ygs.android.yigongshe.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseFragment;
import com.ygs.android.yigongshe.ui.profile.MeProfileAdapter;

import butterknife.BindView;

/**
 * Created by ruichao on 2018/6/13.
 */

public class MeFragment extends BaseFragment {


    @BindView(R.id.me_main_recycleview)
    RecyclerView mRecycleView;

    private MeProfileAdapter mProfileAdapter;

    public void initView(){

        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mProfileAdapter = new MeProfileAdapter();
        mRecycleView.setAdapter(mProfileAdapter);
        mRecycleView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
    }

    @Override
    public int getLayoutResId(){
        return R.layout.fragment_me;
    }


    public void onDestroy(){
        super.onDestroy();
    }
}
