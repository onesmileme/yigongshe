package com.ygs.android.yigongshe.ui.fragment;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseFragment;
import com.ygs.android.yigongshe.ui.profile.MeProfileAdapter;
import com.ygs.android.yigongshe.ui.profile.MeSectionDecoration;

import java.util.LinkedList;
import java.util.List;

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

        mProfileAdapter = new MeProfileAdapter(getContext());
        mProfileAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                System.out.println("click position "+position);
                click(position);
            }
        });
        mRecycleView.setAdapter(mProfileAdapter);
        mRecycleView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });



        List<Integer> showList = new LinkedList<>();
        showList.add(1);
        showList.add(3);
        showList.add(6);
        MeSectionDecoration decoration = new MeSectionDecoration(showList,getContext());
        mRecycleView.addItemDecoration(decoration);
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
