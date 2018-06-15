package com.ygs.android.yigongshe.ui.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ruichao on 2018/6/15.
 */

public abstract class BaseCard {
  private Context mContext;
  private View mView;

  public BaseCard(Context context, @NonNull ViewGroup root) {
    this(context, root, false);
  }

  public BaseCard(Context context, @NonNull ViewGroup root, boolean attachToRoot) {
    this.mContext = context;
    this.createView(context, root, attachToRoot);
  }

  private void createView(Context context, ViewGroup root, boolean attachToRoot) {
    this.mView = LayoutInflater.from(context).inflate(this.onBindLayoutId(), root, attachToRoot);
    this.onViewCreated(this.mView);
  }

  protected abstract void onViewCreated(View var1);

  protected abstract int onBindLayoutId();

  public View getView() {
    return this.mView;
  }

  protected Context getContext() {
    return this.mContext;
  }

  protected void goToOthers(Class<?> cls, Bundle bundle) {
    Intent intent = new Intent(this.getContext(), cls);
    intent.putExtra("intentData", bundle);
    this.getContext().startActivity(intent);
  }

  protected void goToOthersF(Class<?> cls, Bundle bundle) {
    Intent intent = new Intent(this.getContext(), cls);
    intent.putExtra("intentData", bundle);
    this.getContext().startActivity(intent);
    ((BaseActivity) this.getContext()).finish();
  }

  protected void goToOthers(Class<?> cls) {
    Intent intent = new Intent(this.getContext(), cls);
    this.getContext().startActivity(intent);
  }
}
