package com.ygs.android.yigongshe.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ygs.android.yigongshe.R;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ruichao on 2018/6/27.
 */

public class ActivityStatusTypeView {
  @BindView(R.id.activity_status) RecyclerView mStatus;
  @BindView(R.id.activity_type) RecyclerView mType;
  private View mView;
  private StatusSelectListener mStatusSelectListener;
  private TypeSelectListener mTypeSelectListener;

  public ActivityStatusTypeView(Context context, ViewGroup root,
      StatusSelectListener statusSelectListener, TypeSelectListener typeSelectListener) {
    mStatusSelectListener = statusSelectListener;
    mTypeSelectListener = typeSelectListener;
    initView(context, root);
  }

  private void initView(Context context, ViewGroup root) {
    mView = LayoutInflater.from(context).inflate(R.layout.view_status_type, root, false);
    ButterKnife.bind(this, mView);
    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
    layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    mStatus.setLayoutManager(layoutManager);

    TextAdapter statusAdapter = new TextAdapter(
        Arrays.asList(context.getResources().getStringArray(R.array.activity_status)));
    mStatus.setAdapter(statusAdapter);
    mStatus.addOnItemTouchListener(new OnItemClickListener() {
      @Override public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
        mStatusSelectListener.OnStatusSelected((String) adapter.getItem(position));
      }
    });
    LinearLayoutManager layoutManager2 = new LinearLayoutManager(context);
    layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
    mType.setLayoutManager(layoutManager2);
    mType.setAdapter(new TextAdapter(
        Arrays.asList(context.getResources().getStringArray(R.array.activity_type))));
    mType.addOnItemTouchListener(new OnItemClickListener() {
      @Override public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
        mTypeSelectListener.OnTypeSelected((String) adapter.getItem(position));
      }
    });
  }

  public View getView() {
    return this.mView;
  }

  private class TextAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public TextAdapter(@Nullable List<String> data) {
      super(R.layout.item_activity_text, data);
    }

    @Override protected void convert(BaseViewHolder helper, String item) {
      helper.setText(R.id.tv, item);
    }
  }

  public interface StatusSelectListener {
    void OnStatusSelected(String item);
  }

  public interface TypeSelectListener {
    void OnTypeSelected(String item);
  }
}
