package com.weikan.app.original.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.squareup.picasso.Picasso;
import com.weikan.app.R;
import com.weikan.app.WebshellActivity;
import com.weikan.app.original.bean.OriginalBanner;
import com.weikan.app.push.PushDefine;
import com.weikan.app.push.PushManager;
import com.weikan.app.util.BundleParamKey;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.wenyouquan.bean.WenyouBannerObject;
import de.greenrobot.event.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by yuzhiboyou on 15-12-22.
 */
public class BannerView extends FrameLayout {

    ViewPager vp;
    LinearLayout llIcons;

    ArrayList<WenyouBannerObject.Content> item;

    private long lastClickTime;

    BannerPagerAdapter adapter = new BannerPagerAdapter();

    public BannerView(Context context) {
        super(context);
        initViews();
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        EventBus.getDefault().register(adapter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        EventBus.getDefault().unregister(adapter);
    }

    private void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.common_banner_view, this);
        vp = (ViewPager) findViewById(R.id.vp);
        vp.setAdapter(adapter);
        vp.setOnPageChangeListener(adapter);
        llIcons = (LinearLayout) findViewById(R.id.ll_icons);
    }

    public void setData(ArrayList<WenyouBannerObject.Content> item) {
        this.item = item;
        if (item.size() == 0) {
            vp.setVisibility(View.GONE);
        } else {
            vp.setVisibility(View.VISIBLE);
        }

        adapter.setItems(item);
        adapter.notifyDataSetChanged();
        vp.setCurrentItem(1, false);
    }

    private OnClickListener listener = null;

    public class BannerPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
        private static final int MIN_SCROLL_SIZE = 2;

        private final Activity context;

        private int position = 0;
        private int scrollState = ViewPager.SCROLL_STATE_IDLE;
        @NonNull
        private List<WenyouBannerObject.Content> items = new ArrayList<>();

        @NonNull
        private List<ImageView> icons = new ArrayList<>();
        @NonNull
        private SparseArray<ImageView> bufferedViews = new SparseArray<>();

        public BannerPagerAdapter() {
            this.context = (Activity) BannerView.this.getContext();
        }

        public void setItems(@NonNull List<WenyouBannerObject.Content> items) {

            DisplayMetrics dm = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(dm);
            float density = dm.density;

            // 清空旧的BannerContent项
            this.items.clear();
            this.items.addAll(items);

            // 重置所有标记位置的小圆点
            this.icons.clear();
            llIcons.removeAllViews();

            if (this.items.size() >= MIN_SCROLL_SIZE) {
                for (int i = 0; i < this.items.size(); i++) {
                    ImageView icon = new ImageView(context);
                    llIcons.addView(icon);

                    // 必须先Add到ViewGroup里，才有LayoutParams...
//                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) icon.getLayoutParams();
//                    lp.setMargins((int) (3 * density), 0, (int) (3 * density), (int) (6 * density));
//                    icon.setLayoutParams(lp);

                    icon.setImageResource(i == 0 ?
                            R.drawable.white_dot : R.drawable.gray_dot);
                    icons.add(icon);
                }
            }
        }

//        @SuppressWarnings("UnusedParameters")
//        public void onEventMainThread(AutoScrollEvent event) {
//            // 只有在ViewPage状态为空闲的时候，才会自动滚动
//            if (this.scrollState == ViewPager.SCROLL_STATE_IDLE && this.items.size() >= MIN_SCROLL_SIZE) {
//                int newIndex = vp.getCurrentItem() + 1;
//                vp.setCurrentItem(newIndex, true); // 无需判断是否在最后一项，到切换到最后一项时，Adapter会自动切回第一项
//            }
//        }

        private ImageView newImageView(int position) {
            ImageView iv = new ImageView(context);
            iv.setTag(position);

            int realPosition = toRealPosition(position);
            final WenyouBannerObject.Content content = items.get(realPosition);

            Picasso.with(context).load(content.img).into(iv);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            iv.getTextView().setText(content.title);
            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isFastDoubleClick()) {
                        if(!TextUtils.isEmpty(content.jump)){
                            if(content.jump.startsWith("http")
                                    || content.jump.startsWith("https")){

                                Intent intent = new Intent();
                                intent.setClass(getContext(), WebshellActivity.class);
                                intent.putExtra(BundleParamKey.URL, content.jump);
                                getContext().startActivity(intent);
                            } else if(content.jump.startsWith(PushDefine.SCHEMA)){
                                PushManager.getInstance().executePushJump(getContext(), content.jump);
                            }
                        }
                    }
                }
            });
            return iv;
        }

        /**
         * 防止重复点击
         * @return
         */
        private boolean isFastDoubleClick() {
            long time = System.currentTimeMillis();
            long timeD = time - lastClickTime;
            if ( 0 < timeD && timeD < 500) {
                return true;
            }
            lastClickTime = time;
            return false;
        }

        /**
         * 把位置映射到真实下标上去
         *
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
            ImageView view = bufferedViews.get(position);

            if (view == null) {
                view = newImageView(position);
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
            ImageView view = bufferedViews.get(position);
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
                int drawable = (i == realPosition ? R.drawable.white_dot : R.drawable.gray_dot);
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

        public WenyouBannerObject.Content getContent() {
            int realPosition = toRealPosition(position);
            if (realPosition >= 0 && realPosition < items.size()) {
                return items.get(realPosition);
            }

            return null;
        }
    }
}
