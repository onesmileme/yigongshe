package com.ygs.android.yigongshe.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseFragment;
import com.ygs.android.yigongshe.ui.profile.MeProfileAdapter;
import com.ygs.android.yigongshe.ui.profile.MeSectionDecoration;
import com.ygs.android.yigongshe.ui.profile.set.MeSetAcitivity;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by ruichao on 2018/6/13.
 */

public class MeFragment extends BaseFragment {


    @BindView(R.id.me_main_recycleview)
    RecyclerView mRecycleView;

    @BindView(R.id.titlebar_backward_btn)
    Button mNavBackButton;

    @BindView(R.id.titlebar_text_title)
    TextView titleView;

    @BindView(R.id.titlebar_right_btn)
    Button mNavRightButton;

    private MeProfileAdapter mProfileAdapter;

    public void initView(){

        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mProfileAdapter = new MeProfileAdapter(getContext());
        mProfileAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                click(position);
            }
        });
        mRecycleView.setAdapter(mProfileAdapter);

        List<Integer> showList = new LinkedList<>();
        showList.add(1);
        showList.add(3);
        showList.add(6);
        MeSectionDecoration decoration = new MeSectionDecoration(showList,getContext());
        mRecycleView.addItemDecoration(decoration);

        mNavBackButton.setVisibility(View.INVISIBLE);

        Drawable drawable = getContext().getResources().getDrawable(R.drawable.set);
        mNavRightButton.setBackground(drawable);
        mNavRightButton.setText(null);
        mNavRightButton.setVisibility(View.VISIBLE);
        mNavRightButton.setScaleX(0.3f);
        mNavRightButton.setScaleY(0.5f);
        mNavRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MeFragment.this.getContext(), MeSetAcitivity.class);
                MeFragment.this.getContext().startActivity(intent);
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

    private void  click(int position){

        Class clazz = mProfileAdapter.detailClassAtPosition(position);
        if (clazz != null) {
            Intent intent = new Intent(this.getActivity(),clazz);
            startActivity(intent);
        }

    }

}
