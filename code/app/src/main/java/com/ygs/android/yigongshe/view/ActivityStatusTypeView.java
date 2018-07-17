package com.ygs.android.yigongshe.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
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
  private int mStatusOldPos, mTypeOldPos;

  public ActivityStatusTypeView(Context context, ViewGroup root,
      StatusSelectListener statusSelectListener, TypeSelectListener typeSelectListener) {
    mStatusSelectListener = statusSelectListener;
    mTypeSelectListener = typeSelectListener;
    initView(context, root);
  }

  private void initView(final Context context, ViewGroup root) {
    mView = LayoutInflater.from(context).inflate(R.layout.view_status_type, root, false);
    int spacingInPixels = context.getResources().getDimensionPixelSize(R.dimen.space);

    ButterKnife.bind(this, mView);
    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
    layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    mStatus.setLayoutManager(layoutManager);
    mStatus.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
    final TextAdapter statusAdapter = new TextAdapter(
        Arrays.asList(context.getResources().getStringArray(R.array.activity_status)));
    mStatus.setAdapter(statusAdapter);
    //statusAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
    //  @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
    //    view.setBackgroundResource(R.drawable.bg_status_checked);
    //    mStatusSelectListener.OnStatusSelected((String) adapter.getItem(position));
    //  }
    //});
    statusAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
      @Override public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        adapter.getViewByPosition(mStatus, mStatusOldPos, R.id.tv)
            .setBackgroundResource(R.drawable.bg_status_unchecked);
        adapter.getViewByPosition(mStatus, position, R.id.tv)
            .setBackgroundResource(R.drawable.bg_status_checked);
        TextView tv1 = (TextView) (adapter.getViewByPosition(mStatus, mStatusOldPos, R.id.tv));
        tv1.setTextColor(context.getResources().getColor(R.color.gray2));
        TextView tv2 = (TextView) (adapter.getViewByPosition(mStatus, position, R.id.tv));
        tv2.setTextColor(context.getResources().getColor(R.color.white));
        mStatusOldPos = position;
        mStatusSelectListener.OnStatusSelected((String) adapter.getItem(position));
      }
    });
    LinearLayoutManager layoutManager2 = new LinearLayoutManager(context);
    layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
    mType.setLayoutManager(layoutManager2);
    mType.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
    TextAdapter typeAdapter = new TextAdapter(
        Arrays.asList(context.getResources().getStringArray(R.array.activity_type)));
    mType.setAdapter(typeAdapter);
    typeAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
      @Override public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        adapter.getViewByPosition(mType, mTypeOldPos, R.id.tv)
            .setBackgroundResource(R.drawable.bg_status_unchecked);
        adapter.getViewByPosition(mType, position, R.id.tv)
            .setBackgroundResource(R.drawable.bg_status_checked);
        TextView tv1 = (TextView) (adapter.getViewByPosition(mType, mTypeOldPos, R.id.tv));
        tv1.setTextColor(context.getResources().getColor(R.color.gray2));
        TextView tv2 = (TextView) (adapter.getViewByPosition(mType, position, R.id.tv));
        tv2.setTextColor(context.getResources().getColor(R.color.white));
        mTypeOldPos = position;
        mTypeSelectListener.OnTypeSelected((String) adapter.getItem(position));
      }
    });
  }

  public View getView() {
    return this.mView;
  }

  private class TextAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private List<String> mList;

    public TextAdapter(@Nullable List<String> data) {
      super(R.layout.item_activity_text, data);
      mList = data;
    }

    @Override protected void convert(BaseViewHolder helper, String item) {
      helper.setText(R.id.tv, item);
      helper.addOnClickListener(R.id.tv);
      if ("全部".equals(item)) {
        helper.setBackgroundRes(R.id.tv, R.drawable.bg_status_checked);
        helper.setTextColor(R.id.tv, mContext.getResources().getColor(R.color.white));
      } else {
        helper.setBackgroundRes(R.id.tv, R.drawable.bg_status_unchecked);
        helper.setTextColor(R.id.tv, mContext.getResources().getColor(R.color.gray2));
      }
    }
  }

  public interface StatusSelectListener {
    void OnStatusSelected(String item);
  }

  public interface TypeSelectListener {
    void OnTypeSelected(String item);
  }
}
