package com.ygs.android.yigongshe.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import butterknife.ButterKnife;

/**
 * Created by ruichao on 2018/6/13.
 */

public abstract class BaseActivity extends FragmentActivity {
  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutResId());
    ButterKnife.bind(this);
    initIntent();
    iniView();
  }

  protected abstract void initIntent();

  protected abstract void iniView();

  protected abstract int getLayoutResId();
}
