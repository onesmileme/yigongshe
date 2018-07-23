package com.ygs.android.yigongshe.view;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.YGApplication;
import com.ygs.android.yigongshe.account.AccountManager;
import com.ygs.android.yigongshe.bean.CommunityItemBean;
import com.ygs.android.yigongshe.bean.base.BaseResultDataInfo;
import com.ygs.android.yigongshe.bean.response.AttentionResponse;
import com.ygs.android.yigongshe.bean.response.CircleDeleteResponse;
import com.ygs.android.yigongshe.bean.response.ListLikeResponse;
import com.ygs.android.yigongshe.bean.response.UnAttentionResponse;
import com.ygs.android.yigongshe.net.LinkCallHelper;
import com.ygs.android.yigongshe.net.adapter.LinkCall;
import com.ygs.android.yigongshe.net.callback.LinkCallbackAdapter;
import retrofit2.Response;

/**
 * Created by ruichao on 2018/6/28.
 */

public class CommunityDetailHeaderView {
  private View mView;
  @BindView(R.id.createAvatar) ImageView mAvatar;
  @BindView(R.id.createName) TextView mCreateName;
  @BindView(R.id.content) TextView mContent;
  @BindView(R.id.pic) ImageView mPic;
  @BindView(R.id.createDate) TextView mCreateDate;
  @BindView(R.id.topic) TextView mTopic;
  @BindView(R.id.markgood) TextView mMarkGood;
  @BindView(R.id.attention) TextView mAttention;
  private Context mContext;
  @BindView(R.id.iv_markgood) ImageView mIvMarkGoodk;
  @BindView(R.id.delete) TextView mDelete;
  private ItemClickListener mListener;

  public CommunityDetailHeaderView(Context context, ViewGroup root, ItemClickListener listener) {
    mContext = context;
    mListener = listener;
    initView(context, root);
  }

  private void initView(Context context, ViewGroup root) {
    mView = LayoutInflater.from(context).inflate(R.layout.item_community, root, false);
    ButterKnife.bind(this, mView);
  }

  public void setViewData(final CommunityItemBean item) {
    final AccountManager accountManager = YGApplication.accountManager;
    Glide.with(mContext)
        .load(item.create_avatar)
        .placeholder(R.drawable.defalutavar)
        .error(R.drawable.defalutavar)
        .fallback(R.drawable.defalutavar)
        .transform(new CenterCrop(mContext), new GlideCircleTransform(mContext))
        .into(mAvatar);
    mCreateName.setText(item.create_name);
    mContent.setText(item.topic + item.content);
    if (TextUtils.isEmpty(item.pic)) {
      mPic.setVisibility(View.GONE);
    } else {
      mPic.setVisibility(View.VISIBLE);
      Glide.with(mContext)
          .load(item.pic)
          .placeholder(R.drawable.loading2)
          .error(R.drawable.loading2)
          .fallback(R.drawable.loading2)
          .transform(new CenterCrop(mContext), new GlideRoundTransform(mContext))
          .into(mPic);
    }

    mCreateDate.setText(item.create_at);
    mTopic.setText(item.topic);
    mMarkGood.setText(item.like_num + "");
    if (item.is_follow == 0) {
      mAttention.setBackgroundResource(R.drawable.bg_unattention);
      mAttention.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          LinkCall<BaseResultDataInfo<AttentionResponse>> attention = LinkCallHelper.getApiService()
              .attentionUser(accountManager.getUserid(), accountManager.getToken());
          attention.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<AttentionResponse>>() {
            @Override public void onResponse(BaseResultDataInfo<AttentionResponse> entity,
                Response<?> response, Throwable throwable) {
              super.onResponse(entity, response, throwable);
              if (entity != null) {
                if (entity.error == 2000) {
                  Toast.makeText(mContext, "关注成功", Toast.LENGTH_SHORT).show();
                  mAttention.setBackgroundResource(R.drawable.bg_attention);
                } else {
                  Toast.makeText(mContext, entity.msg, Toast.LENGTH_SHORT).show();
                }
              }
            }
          });
        }
      });
    } else {
      mAttention.setBackgroundResource(R.drawable.bg_attention);
      mAttention.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          LinkCall<BaseResultDataInfo<UnAttentionResponse>> unattention =
              LinkCallHelper.getApiService()
                  .unAttentionUser(accountManager.getUserid(), accountManager.getToken());
          unattention.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<UnAttentionResponse>>() {
            @Override public void onResponse(BaseResultDataInfo<UnAttentionResponse> entity,
                Response<?> response, Throwable throwable) {
              super.onResponse(entity, response, throwable);
              if (entity != null && entity.error == 2000) {
                Toast.makeText(mContext, "取消关注成功", Toast.LENGTH_SHORT).show();
                mAttention.setBackgroundResource(R.drawable.bg_unattention);
              }
            }
          });
        }
      });
    }

    if (item.is_like == 0)

    {
      mIvMarkGoodk.setImageResource(R.drawable.markgood);
    } else

    {
      mIvMarkGoodk.setImageResource(R.drawable.hasmarkgood);
    }
    mIvMarkGoodk.setOnClickListener(new View.OnClickListener()

    {
      @Override public void onClick(View view) {
        if (item.is_like == 0) {
          LinkCall<BaseResultDataInfo<ListLikeResponse>> like = LinkCallHelper.getApiService()
              .likeCircle(accountManager.getUserid(), accountManager.getToken());
          like.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<ListLikeResponse>>() {
            @Override public void onResponse(BaseResultDataInfo<ListLikeResponse> entity,
                Response<?> response, Throwable throwable) {
              super.onResponse(entity, response, throwable);
              if (entity != null) {
                if (entity.error == 2000) {
                  Toast.makeText(mContext, "点赞成功", Toast.LENGTH_SHORT).show();
                  mIvMarkGoodk.setBackgroundResource(R.drawable.hasmarkgood);
                } else {
                  Toast.makeText(mContext, entity.msg, Toast.LENGTH_SHORT).show();
                }
              }
            }
          });
        }
      }
    });

    if (!TextUtils.isEmpty(accountManager.getToken())
        && item.create_id == accountManager.getUserid())

    {
      mDelete.setVisibility(View.VISIBLE);
      mDelete.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
      mDelete.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          LinkCall<BaseResultDataInfo<CircleDeleteResponse>> deleteCircle =
              LinkCallHelper.getApiService()
                  .deleteCircle(item.pubcircleid, accountManager.getToken());
          deleteCircle.enqueue(new LinkCallbackAdapter<BaseResultDataInfo<CircleDeleteResponse>>() {
            @Override public void onResponse(BaseResultDataInfo<CircleDeleteResponse> entity,
                Response<?> response, Throwable throwable) {
              super.onResponse(entity, response, throwable);
              if (entity != null) {
                if (entity.error == 2000) {
                  Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                  mListener.onItemClicked();
                } else {
                  Toast.makeText(mContext, entity.msg, Toast.LENGTH_SHORT).show();
                }
              }
            }
          });
        }
      });
    } else

    {
      mDelete.setVisibility(View.GONE);
    }
  }

  public View getView() {
    return this.mView;
  }

  public interface ItemClickListener {
    void onItemClicked();
  }
}
