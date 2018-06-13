package com.weikan.app.original.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


public class HeadViewPager extends ViewPager implements Runnable {
    private PagerAdapter pagerAdapter;

    private static final int POST_DELAYED_TIME = 1000 * 5;
    // 手指是否放在上面
    private boolean touching;

    // 更新数据需要获得myPagerAdapter
    private PagerAdapter myPagerAdapter;

    public HeadViewPager(Context context) {
        super(context);
    }

    public HeadViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 对setAdapter的数据进行包装
    private class HeadPagerAdapter extends PagerAdapter {
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return pa.isViewFromObject(view, object);
        }

        private PagerAdapter pa;

        public HeadPagerAdapter(PagerAdapter pa) {
            this.pa = pa;
        }

        @Override
        // 关键之一:修改Count长度
        public int getCount() {
            return pa.getCount() > 2 ? 10000 : pa.getCount();
            // return pa.getCount() > 1 ? pa.getCount() + 2 : pa.getCount();
        }

        @Override
        // 这里是关键之二:修改索引(如果不考虑内容问题可以全部加载进数组然后操作更简单)
        public Object instantiateItem(ViewGroup container, int position) {

            // if (position == 0) {
            // return pa.instantiateItem(container, pa.getCount() - 1);
            // } else if (position == pa.getCount() + 1) {
            // return pa.instantiateItem(container, 0);
            // } else {
            // return pa.instantiateItem(container, position - 1);
            // }
            // Log.e("instantiateItem", ""+position);
            return pa.instantiateItem(container, position % pa.getCount());
        }
        
		@Override
        public int getItemPosition(Object object) {
			return pa.getItemPosition(object);
		}

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            pa.destroyItem(container, position % pa.getCount(), object);
        }

    }


    @Override
    public void setAdapter(PagerAdapter arg0) {
        myPagerAdapter = arg0 == null ? null : new HeadPagerAdapter(arg0);
        super.setAdapter(myPagerAdapter);

        // if (arg0 != null && arg0.getCount() != 0) {
        // setCurrentItem(0, false);
        // }
        this.pagerAdapter = arg0;
    }

    @Override
    // 兼容PageIndicator
    public PagerAdapter getAdapter() {
        return pagerAdapter;
    }

    public PagerAdapter getMyPagerAdapter() {
        return myPagerAdapter;
    }

    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, true);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    // 自动滚动关键
    public void run() {
        if (getAdapter() != null && getAdapter().getCount() > 1 && !touching) {
            if (getCurrentItem() + 1 < getMyPagerAdapter().getCount()) {
                setCurrentItem(getCurrentItem() + 1, true);
            } else {
                setCurrentItem(0, true);
            }
        }
        // startMoving();
    }

    public void startMoving() {
        stopMoving();
        postDelayed(this, POST_DELAYED_TIME);
    }

    public void stopMoving() {
        removeCallbacks(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.touching = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL) {
            this.touching = false;
        }

        return super.onTouchEvent(event);
    }
}
