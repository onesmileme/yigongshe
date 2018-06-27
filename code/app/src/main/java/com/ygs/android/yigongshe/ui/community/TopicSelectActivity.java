package com.ygs.android.yigongshe.ui.community;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import java.util.Arrays;

/**
 * Created by ruichao on 2018/6/27.
 */

public class TopicSelectActivity extends BaseActivity {
  @BindView(R.id.titleBar) CommonTitleBar mTitleBar;
  @BindView(R.id.rv_list) RecyclerView mRecyclerView;

  @Override protected void initIntent(Bundle bundle) {

  }

  @Override protected void initView() {
    mTitleBar.getCenterTextView().setText(getResources().getString(R.string.topicSelect));
    mTitleBar.getRightTextView().setText(getResources().getString(R.string.done));
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    initAdapter();
  }

  private void initAdapter() {
    TopicSelectAdapter adapter = new TopicSelectAdapter(
        Arrays.asList(getResources().getStringArray(R.array.community_topic_select_list)));
    mRecyclerView.setAdapter(adapter);
    mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
      @Override public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {

      }
    });
  }

  @Override protected int getLayoutResId() {
    return R.layout.activity_topic_select;
  }
}
