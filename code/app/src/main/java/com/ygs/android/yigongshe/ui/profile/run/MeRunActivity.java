package com.ygs.android.yigongshe.ui.profile.run;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.profile.MeSectionDecoration;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;

public class MeRunActivity extends BaseActivity {


    @BindView(R.id.titlebar_text_title)
    TextView titleView;

    @BindView(R.id.titlebar_backward_btn)
    Button backButton;

    @BindView(R.id.me_run_recycleview)
    RecyclerView recyclerView;

    @BindView(R.id.run_refresh_layout)
    SwipeRefreshLayout refreshLayout;


    MeRunAdapter runAdapter;

    protected void initIntent(){

    }

    protected void initView(){

        List<Integer> showList = new LinkedList<>();
        showList.add(1);
        MeSectionDecoration decoration = new MeSectionDecoration(showList,this);
        decoration.setHintHight(10);
        recyclerView.addItemDecoration(decoration);

        titleView.setText(R.string.run_list);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        runAdapter = new MeRunAdapter(this);
        recyclerView.setAdapter(runAdapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

    }

    protected int getLayoutResId(){
        return R.layout.activity_me_run;
    }

}
