package com.ygs.android.yigongshe.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ruichao on 2018/6/13.
 */

public abstract class BaseFragment extends Fragment {
  View mRoot;
  private Unbinder unbinder;

  @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    if (mRoot != null) {
      ViewGroup parent = (ViewGroup) mRoot.getParent();
      if (parent != null) {
        parent.removeView(mRoot);
      }
    } else {
      mRoot = inflater.inflate(getLayoutResId(), container, false);
      unbinder = ButterKnife.bind(this, mRoot);

      initView();
    }

    return mRoot;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
  }

  protected abstract void initView();

  public abstract int getLayoutResId();

  protected void goToOthers(Class<?> cls, Bundle bundle) {
    Intent intent = new Intent(getActivity(), cls);
    intent.putExtra(BaseActivity.PARAM_INTENT, bundle);
    getActivity().startActivity(intent);
  }

  //A-->B: A跳转到B，并且通过onActivityResult得到回传的数据
  protected void goToOthersForResult(Class<?> cls, Bundle bundle, int requestCode) {
    Intent intent = new Intent(getActivity(), cls);
    intent.putExtra(BaseActivity.PARAM_INTENT, bundle);
    startActivityForResult(intent, requestCode);
  }

  //A-->B: B回传数据给A
  public void backForResult(Class<?> cls, Bundle bundle, int resultCode) {
    Intent intent = new Intent();
    if (cls != null) {
      intent.setClass(getActivity(), cls);
    }
    intent.putExtra(BaseActivity.PARAM_INTENT, bundle);
    getActivity().setResult(resultCode, intent);
    getActivity().finish();
  }
}
