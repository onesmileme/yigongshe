package com.ygs.android.yigongshe.ui.profile.record;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.bean.CharityRecordBean;
import com.ygs.android.yigongshe.bean.CharityRecordItemBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.net.ApiStatus;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.CDividerItemDecoration;
import com.ygs.android.yigongshe.view.CommonTitleBar;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Response;

/**
 * 公益记录
 */
public class MeRecordListActivity extends BaseActivity {


    @BindView(R.id.me_record_recycle_view)
    RecyclerView recyclerView;

    MeRecordAdapter recordAdapter;

    @BindView(R.id.titlebar)
    CommonTitleBar titleBar;

    @BindView(R.id.message_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    LinkCall<BaseResultDataInfo<CharityRecordBean>> mCall;

    int page ;

    static final int PER_PAGE = 20;

    List<CharityRecordItemBean> recordBeanList = new LinkedList<>();

    @Override
    protected  void initIntent(Bundle bundle){

    }

    @Override
    protected  void initView(){

        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON){
                    finish();
                }
            }
        });

        recordAdapter = new MeRecordAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recordAdapter);

        CDividerItemDecoration itemDecoration = new CDividerItemDecoration(this,
            CDividerItemDecoration.VERTICAL_LIST, new ColorDrawable(Color.parseColor("#e0e0e0")));//
        itemDecoration.setHeight(1);
        recyclerView.addItemDecoration(itemDecoration);

        recordAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRecods(true);
            }
        });

        swipeRefreshLayout.setRefreshing(true);
        loadRecods(true);

    }

    @Override
    protected  int getLayoutResId(){
        return R.layout.activity_record_list;
    }


    private void loadRecods(final boolean refresh){

        if (refresh){
            page = 0;
        }

        String token = YGApplication.accountManager.getToken();
        mCall = LinkCallHelper.getApiService().getCharityRecrodList(token,page,PER_PAGE);
        mCall.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<CharityRecordBean>>(){
            @Override
            public void onResponse(BaseResultDataInfo<CharityRecordBean> entity, Response<?> response,
                                   Throwable throwable) {
                super.onResponse(entity, response, throwable);
                if (entity != null && entity.error == ApiStatus.OK){

                    if (refresh){
                        recordBeanList.clear();
                        recordAdapter.setNewData(entity.data.list);
                    }else {
                        recordAdapter.addData(entity.data.list);
                    }
                    recordBeanList.addAll(entity.data.list);

                }else{
                    String reason = "请求公益记录失败";
                    if (entity != null && entity.msg != null){
                        reason += "("+entity.msg+")";
                    }
                    Toast.makeText(MeRecordListActivity.this,reason,Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

}
