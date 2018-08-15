package com.ygs.android.yigongshe.view;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.ygs.android.yigongshe.R;
import com.ygs.android.yigongshe.bean.response.ScrollPicBean;
import com.ygs.android.yigongshe.push.PushManager;
import com.ygs.android.yigongshe.ui.base.BaseCard;
import com.ygs.android.yigongshe.utils.DensityUtil;

import java.util.List;

/**
 * Created by ruichao on 2018/6/15.
 */

public class TopBannerCard extends BaseCard {
  @BindView(R.id.banner) AutoScrollViewPager mViewPager;
  @BindView(R.id.rg_point) RadioGroup mRg;
  private Context mContext;

  public TopBannerCard(Context context, @NonNull ViewGroup root, int type) {
    this(context, root, false, type);
  }

  public TopBannerCard(Context context, @NonNull ViewGroup root, boolean attachToRoot, int type) {
    super(context, root, attachToRoot, type);
    mContext = context;
  }

  @Override protected void onViewCreated(View var1) {
    ButterKnife.bind(this, var1);
  }

  @Override protected int onBindLayoutId() {
    if (mType == 0) {
      return R.layout.view_top_banner;
    } else {
      return R.layout.view_top_banner2;
    }
  }

  public void initViewWithData(final List<ScrollPicBean> list) {
    BannerPagerAdapter adapter = new BannerPagerAdapter(mContext, list);
    mViewPager.removeAllViews();
    mViewPager.setAdapter(adapter);
    mViewPager.setHandleSlipConflict(true);
    mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override public void onPageSelected(int position) {
        if (null != mRg.getChildAt(position)) {
          ((RadioButton) mRg.getChildAt(position)).setChecked(true);
        }
      }

      @Override public void onPageScrollStateChanged(int state) {

      }
    });
    mRg.removeAllViews();
    int n = list.size();
    if (n > 1) {
      for (int i = 0; i < n; i++) {
        RadioButton rb = new RadioButton(getContext());
        rb.setBackgroundResource(R.drawable.selector_viewpager_host);
        rb.setChecked(false);
        rb.setEnabled(false);
        mRg.addView(rb);
        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(DensityUtil.dp2px(mContext, 8),
                DensityUtil.dp2px(mContext, 8));
        params.leftMargin = DensityUtil.dp2px(mContext, 10);
        params.bottomMargin = DensityUtil.dp2px(mContext, 10);
        rb.setLayoutParams(params);
      }
      ((RadioButton) mRg.getChildAt(0)).setChecked(true);
      mViewPager.setInterval(4000); //4s
      mViewPager.startAutoScroll();
    } else {
      if (mRg != null) {
        mRg.setVisibility(View.GONE);
      }
    }
  }

  private class BannerPagerAdapter extends PagerAdapter {
    private List<ScrollPicBean> mList; //ImageUrls
    private Context mContext;

    BannerPagerAdapter(Context context, List<ScrollPicBean> list) {
      mContext = context;
      mList = list;
    }

    @Override public Object instantiateItem(ViewGroup container, final int position) {
      ImageView iv = new ImageView(container.getContext());
      iv.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          //Intent intent = new Intent(mContext, WebViewActivity.class);
          //Bundle bundle = new Bundle();
          //bundle.putString(WebViewActivity.URL_KEY, mList.get(position).hope_url);
          //intent.putExtra(BaseActivity.PARAM_INTENT, bundle);
          //mContext.startActivity(intent);
          PushManager.handle(Uri.parse(mList.get(position).hope_url));
        }
      });
      iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
      if (mType == 0) {
        Glide.with(mContext)
            .load(mList.get(position).pic)
            .placeholder(R.drawable.loading2)
            .error(R.drawable.loading2)
            .fallback(R.drawable.loading2)
            .transform(new CenterCrop(mContext), new GlideRoundTransform(mContext))
            .into(iv);
      } else {
        Glide.with(mContext)
            .load(mList.get(position).pic)
            .placeholder(R.drawable.loading2)
            .error(R.drawable.loading2)
            .fallback(R.drawable.loading2)
            .into(iv);
      }
      container.addView(iv);

      return iv;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
      container.removeView((View) object);
    }

    @Override public int getCount() {
      return mList.size() == 0 ? 1 : mList.size();
    }

    @Override public boolean isViewFromObject(View view, Object object) {
      return view == object;
    }
  }
}
