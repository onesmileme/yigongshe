package com.weikan.app.news.widget;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.umeng.analytics.MobclickAgent;
import com.weikan.app.R;
import com.weikan.app.common.widget.BaseListItemView;
import com.weikan.app.news.TemplateConfig;
import com.weikan.app.news.bean.BannerContent;
import com.weikan.app.news.event.AutoScrollEvent;
import com.weikan.app.original.bean.OriginalItem;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 轮播的View
 *
 * @author kailun on 16/1/10
 */
public class BannerView extends BaseListItemView<OriginalItem> implements INewsView {

    ImageView ivPic;
    ViewPager vp;
    LinearLayout llIcons;

    BannerPagerAdapter adapter;

    public BannerView(Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(adapter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(adapter);
    }

    @Override
    protected void initViews() {
        ivPic = (ImageView) findViewById(R.id.iv_pic);
        int placeHolder = TemplateConfig.getConfig().getDrawablePlaceHolderBanner();
        ivPic.setImageResource(placeHolder);
        vp = (ViewPager) findViewById(R.id.vp);
        adapter = new BannerPagerAdapter();
        vp.setAdapter(adapter);
        vp.addOnPageChangeListener(adapter);
        llIcons = (LinearLayout) findViewById(R.id.ll_icons);
    }

    @Override
    public void set(@Nullable OriginalItem item) {
        super.set(item);

        if (item != null) {
            if (item.bannerContent.size() == 0) {
                vp.setVisibility(View.GONE);
            } else {
                vp.setVisibility(View.VISIBLE);
            }

            ArrayList<BannerContent> arr = new ArrayList<>();
            for (OriginalItem.BannerContent c : item.bannerContent) {
                BannerContent content = BannerContent.from(c);
                arr.add(content);
            }
            adapter.setItems(arr);
            adapter.notifyDataSetChanged();
            vp.setCurrentItem(1, false);
        }
    }

    private OnClickListener listener = null;

    @Override
    public void setOnItemClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int layoutResourceId() {
        return R.layout.widget_banner_view;
    }

    @Nullable
    public BannerContent getCurrentBannerContent() {
        return adapter.getContent();
    }

    public class BannerPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener  {
        private static final int MIN_SCROLL_SIZE = 1;

        private int position = 0;
        private int scrollState = ViewPager.SCROLL_STATE_IDLE;
        @NonNull
        private List<BannerContent> items = new ArrayList<>();

        @NonNull
        private List<ImageView> icons = new ArrayList<>();
        @NonNull
        private SparseArray<BannerImageView> bufferedViews = new SparseArray<>();

        public BannerPagerAdapter() {
        }

        public void setItems(@NonNull List<BannerContent> items) {

            DisplayMetrics dm = new DisplayMetrics();
            Activity activity = (Activity) getContext();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            float density = dm.density;

            // 清空旧的BannerContent项
            this.items.clear();
            this.items.addAll(items);

            // 重置所有标记位置的小圆点
            this.icons.clear();
            llIcons.removeAllViews();

            if (this.items.size() >= MIN_SCROLL_SIZE) {
                Log.e(BannerView.class.getSimpleName(), "items for in");

                for (int i = 0; i < this.items.size(); i++) {
                    Log.e(BannerView.class.getSimpleName(), "items for: " + i);
                    ImageView icon = new ImageView(getContext());
                    llIcons.addView(icon);

                    // 必须先Add到ViewGroup里，才有LayoutParams...
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) icon.getLayoutParams();
                    lp.width = lp.height = Math.round(6 * density);
                    lp.setMargins(Math.round(4 * density), 0, Math.round(4 * density), 0);
                    icon.setLayoutParams(lp);

                    icon.setImageResource(i == 0 ?
                            R.drawable.pager_icon_sel : R.drawable.pager_icon);
                    icons.add(icon);
                }
            } else {
                Log.e(BannerView.class.getSimpleName(), "not items to for in");
            }
        }

        @SuppressWarnings("UnusedParameters")
        public void onEventMainThread(AutoScrollEvent event) {
            // 只有在ViewPage状态为空闲的时候，才会自动滚动
            if (this.scrollState == ViewPager.SCROLL_STATE_IDLE && this.items.size() > MIN_SCROLL_SIZE) {
                int newIndex = vp.getCurrentItem() + 1;
                vp.setCurrentItem(newIndex, true); // 无需判断是否在最后一项，到切换到最后一项时，Adapter会自动切回第一项
            }
        }

        private BannerImageView newBannerImageView(int position) {
            BannerImageView iv = new BannerImageView(getContext());
            iv.setTag(position);

            int realPosition = toRealPosition(position);
            BannerContent content = items.get(realPosition);

            NewsViewFactory.setBannerPhoto(iv.getImageView(), content.imgUrl);
            iv.getTextView().setText(content.title);
            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        MobclickAgent.onEvent(getContext(), "AD_banner_click");
                        listener.onClick(BannerView.this);
                    }
                }
            });
            return iv;
        }

        /**
         * 把位置映射到真实下标上去
         * @param position ViewPager的位置
         * @return 轮播图真实的位置
         */
        private int toRealPosition(int position) {
            int size = items.size();
            if (size >= MIN_SCROLL_SIZE) {
                int realPosition;
                if (position == 0) {
                    realPosition = size - 1;
                } else if (position == size + 1) {
                    realPosition = 0;
                } else if (position > 0 && position < size + 1) {
                    realPosition = position - 1;
                } else {
                    throw new RuntimeException("out of range: " + position + ", size " + size);
                }
                return realPosition;
            } else {
                return 0;
            }
        }

        @Override
        public int getCount() {
            int size = items.size();
            if (size >= MIN_SCROLL_SIZE) {
                size += 2;
            }
            return size;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            BannerImageView view = bufferedViews.get(position);

            if (view == null) {
                view = newBannerImageView(position);
                bufferedViews.put(position, view);
                container.addView(view);
            }

            return position;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            Object tag = view.getTag();
            if (tag == null) {
                throw new RuntimeException("tag of view cant be null");
            }
            return tag.equals(o);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            BannerImageView view = bufferedViews.get(position);
            if (view != null) {
                container.removeView(view);
                bufferedViews.remove(position);
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public void onPageScrolled(int position, float offset, int offsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            this.position = position;
            int realPosition = toRealPosition(position);
            for (int i = 0; i < this.items.size(); i++) {
                int drawable = (i == realPosition ? R.drawable.pager_icon_sel: R.drawable.pager_icon);
                if (i < icons.size()) {
                    icons.get(i).setImageResource(drawable);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // 如果状态变为IDLE，则需要判断当前位置
            // 如果补上去的头或者尾，则需要作一次无动画跳转
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                int item = vp.getCurrentItem();

                final int size = items.size();
                if (size >= MIN_SCROLL_SIZE) {
                    if (item == 0) {
                        vp.setCurrentItem(size, false);
                    } else if (item == size + 1) {
                        vp.setCurrentItem(1, false);
                    }
                }
            }
            this.scrollState = state;
        }

        @Nullable
        public BannerContent getContent() {
            int realPosition = toRealPosition(position);
            if (realPosition >= 0 && realPosition < items.size()) {
                return items.get(realPosition);
            }

            return null;
        }
    }
}
