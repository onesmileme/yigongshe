package com.ygs.android.yigongshe.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ruichao on 2018/7/15.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

  private int space;

  public SpaceItemDecoration(int space) {
    this.space = space;
  }

  @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {

    if (parent.getChildPosition(view) != 0) outRect.left = space;
  }
}