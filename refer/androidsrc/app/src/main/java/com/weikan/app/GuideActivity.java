package com.weikan.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.util.LToast;
import com.weikan.app.util.PrefDefine;
import com.weikan.app.util.SharePrefsUtils;
import com.weikan.app.util.ShareTools;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends BaseActivity implements OnClickListener {

	private ViewPager _viewPager;
	private ViewPagerAdapter _pagerAdapter;
	private View enterWeixin;
	private View enterQQ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AccountManager.getInstance().init(this);

		setContentView(R.layout.activity_guide);
		setSwipeBackEnable(false);

		_viewPager = (ViewPager) findViewById(R.id.guide_viewpager);
		_pagerAdapter = new ViewPagerAdapter();
		_viewPager.setAdapter(_pagerAdapter);


		ArrayList<ImageView> imageViews = new ArrayList<ImageView>();
		int[] imageReses = { R.drawable.vs_guid_1, R.drawable.vs_guid_2 };
		for (int index = 0; index < imageReses.length; index++) {
			int imageRes = imageReses[index];
			ImageView imageView = new ImageView(this);
			imageView.setImageResource(imageRes);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageViews.add(imageView);

			if (index == imageReses.length-1) {
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						gotoMain();
					}
				});
			}
		}

		_pagerAdapter.setmListViews(imageViews);
		_pagerAdapter.notifyDataSetChanged();

	}

	public void gotoMain(){

		// 设置已经看过引导页
		SharePrefsUtils sharePrefsUtils = new SharePrefsUtils(GuideActivity.this,
				PrefDefine.FIRSTLOGIN_STRING);
		sharePrefsUtils.setBoolean("is_first_login", false);

		// 登录完成进入主界面
		Intent intent = new Intent(GuideActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		default:
			break;
		}
	}

	private class ViewPagerAdapter extends PagerAdapter {

		private List<ImageView> mListViews = new ArrayList<ImageView>();

		public ViewPagerAdapter() {
		}

		public void setmListViews(List<ImageView> listViews) {
			this.mListViews = listViews;
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View collection, int position) {
			((ViewPager) collection).addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView(mListViews.get(position));
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == (object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}
	}

}
