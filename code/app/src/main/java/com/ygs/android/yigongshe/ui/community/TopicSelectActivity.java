package com.ygs.android.yigongshe.ui.community;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.ui.base.BaseActivity;
import com.ygs.android.yigongshe.ui.base.BaseDetailActivity;
import com.ygs.android.yigongshe.view.CommonTitleBar;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ruichao on 2018/6/27.
 */

public class TopicSelectActivity extends BaseActivity {
  @BindView(R.id.titleBar) CommonTitleBar mTitleBar;
  @BindView(R.id.rv_list) RecyclerView mRecyclerView;
  private String mSelected;//被选中的字串
  private int mSelectedPos;//被选中的位置
  private int mId;//话题/城市

  @Override protected void initIntent(Bundle bundle) {
    mId = bundle.getInt("id");
  }

  @Override protected void initView() {
    mTitleBar.getCenterTextView().setText(getResources().getString(R.string.topicSelect));
    mTitleBar.getRightTextView().setText(getResources().getString(R.string.done));
    mTitleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
      @Override public void onClicked(View v, int action, String extra) {
        if (action == CommonTitleBar.ACTION_LEFT_BUTTON
            || action == CommonTitleBar.ACTION_RIGHT_TEXT) {
          Bundle bundle = new Bundle();
          bundle.putString("key", mSelected);
          bundle.putInt("id", mId);
          backForResult(BaseDetailActivity.class, bundle, 100);
          finish();
        }
      }
    });
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerView.addItemDecoration(
        new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    initAdapter();
  }

  private void initAdapter() {
    final List<String> data =
        Arrays.asList(getResources().getStringArray(R.array.community_topic_select_list));
    TopicSelectAdapter adapter = new TopicSelectAdapter(data);
    mRecyclerView.setAdapter(adapter);
    mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
      @Override public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
        ((TextView) adapter.getViewByPosition(mRecyclerView, mSelectedPos,
            R.id.tv_topic)).setTextColor(getResources().getColor(R.color.gray1));
        mSelectedPos = position;
        ((TextView) adapter.getViewByPosition(mRecyclerView, mSelectedPos,
            R.id.tv_topic)).setTextColor(getResources().getColor(R.color.green));
        String tmp = data.get(position);
        if (mId != 0) {
          mSelected = tmp.substring(1, tmp.length() - 1);
        } else {
          mSelected = tmp;
        }
      }
    });
  }

  @Override protected int getLayoutResId() {
    return R.layout.activity_topic_select;
  }
}
