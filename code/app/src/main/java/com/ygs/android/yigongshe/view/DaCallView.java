package com.ygs.android.yigongshe.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.ActivityDetailResponse;
import com.ygs.android.yigongshe.bean.response.DaCallResponse;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import com.ygs.android.yigongshe.utils.StringUtil;
import retrofit2.Response;

/**
 * Created by ruichao on 2018/6/28.
 */

public class DaCallView {
  @BindView(R.id.callstatus) TextView mCallStatus;
  @BindView(R.id.markgood) TextView mMardgood;
  @BindView(R.id.iv_markgood) ImageView mIvMarkgood;
  private View mView;
  private Context mContext;
  private int mId;

  public DaCallView(Context context, ViewGroup root, int id) {
    mContext = context;
    mId = id;
    initView(context, root);
  }

  private void initView(final Context context, ViewGroup root) {
    mView = LayoutInflater.from(context).inflate(R.layout.view_dacall, root, false);
    ButterKnife.bind(this, mView);
    mIvMarkgood.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        AccountManager accountManager = YGApplication.accountManager;
        if (TextUtils.isEmpty(accountManager.getToken())) {
          Toast.makeText(context, "没有登录", Toast.LENGTH_SHORT).show();
          return;
        }
        LinkCall<BaseResultDataInfo<DaCallResponse>> signup =
            LinkCallHelper.getApiService().daCallActivity(mId, accountManager.getToken());
        signup.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<DaCallResponse>>() {
          @Override
          public void onResponse(BaseResultDataInfo<DaCallResponse> entity, Response<?> response,
              Throwable throwable) {
            super.onResponse(entity, response, throwable);
            if (entity != null) {
              if (entity.error == 2000) {
                Toast.makeText(context, "打call成功", Toast.LENGTH_SHORT).show();
                mIvMarkgood.setImageResource(R.drawable.yizan);
              } else {
                Toast.makeText(context, entity.msg, Toast.LENGTH_SHORT).show();
              }
            }
          }
        });
      }
    });
  }

  public void setDacallViewData(ActivityDetailResponse data) {
    mMardgood.setText(data.cur_call_num + "");
    if (data.is_call == 0) {
      mIvMarkgood.setImageResource(R.drawable.zan);
    } else {
      mIvMarkgood.setImageResource(R.drawable.yizan);
    }
    mCallStatus.setText(
        StringUtil.getReleaseString(mContext.getResources().getString(R.string.dacallstatus),
            new Object[] { data.target_call_num, data.cur_call_num }));
  }

  public View getView() {
    return this.mView;
  }
}
