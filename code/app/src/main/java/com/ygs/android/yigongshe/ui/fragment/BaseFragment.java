package com.ygs.android.yigongshe.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;

/**
 * Created by ruichao on 2018/6/13.
 */

public abstract class BaseFragment extends Fragment {
  View mRoot;

  @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mRoot = inflater.inflate(getLayoutResId(), container, false);
    ButterKnife.bind(this, mRoot);

    initView();
    return mRoot;
  }

  protected abstract void initView();

  public abstract int getLayoutResId();
}
