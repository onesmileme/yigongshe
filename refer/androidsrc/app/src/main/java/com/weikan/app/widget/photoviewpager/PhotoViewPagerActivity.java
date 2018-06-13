/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.weikan.app.widget.photoviewpager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.weikan.app.R;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.util.LToast;
import com.weikan.app.util.StorageUtil;

import java.io.File;
import java.util.ArrayList;

import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.feature.zoom.ImageZoomer;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.DisplayListener;
import me.xiaopan.sketch.request.ErrorCause;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.state.DrawableStateImage;

public class PhotoViewPagerActivity extends BaseActivity {

    private ViewPager mViewPager;
    private PagerAdapter adapter;

    private int index;
    private String[] menuItems = new String[]{"保存到手机"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewPager = new HackyViewPager(this);
        setContentView(mViewPager);

        Intent intent = getIntent();
        if (intent != null) {
            index = intent.getIntExtra("bitmaps_index", 0);
        }
        adapter = new SamplePagerAdapter();
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(index);
        mViewPager.setBackgroundColor(0xff000000);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    class SamplePagerAdapter extends PagerAdapter {
        public ArrayList<Drawable> sDrawables = BitmapPersistence.getInstance().mDrawable;
        public ArrayList<String> mDrawableUrl = BitmapPersistence.getInstance().mDrawableUrl;

        @Override
        public View instantiateItem(final ViewGroup container, final int position) {
            RelativeLayout layout = (RelativeLayout) LayoutInflater.from(container.getContext()).inflate(R.layout.big_img_layout, container, false);

            //SketchImageView详细用法：https://github.com/xiaopansky/Sketch
            final SketchImageView imageView = (SketchImageView) layout.findViewById(R.id.image_item);
            final String imageUri = mDrawableUrl.get(position);

            if (position < sDrawables.size()) {
                Drawable loadingDrawable = sDrawables.get(position);
                if (loadingDrawable != null) {
                    imageView.getOptions().setLoadingImage(new DrawableStateImage(loadingDrawable));
                }
            }
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setSupportZoom(true);
            imageView.setSupportLargeImage(true);
            imageView.setShowDownloadProgress(true);
            imageView.getOptions().setDecodeGifImage(true);
            imageView.getImageZoomer().setReadMode(true);

            imageView.setDisplayListener(new DisplayListener() {
                @Override
                public void onCompleted(ImageFrom imageFrom, String mimeType) {
                    if (!mimeType.contains("gif")) {
                        imageView.getImageZoomer().setOnViewLongPressListener(new ImageZoomer.OnViewLongPressListener() {
                            @Override
                            public void onViewLongPress(View view, float x, float y) {
                                new AlertDialog.Builder(PhotoViewPagerActivity.this)
                                        .setItems(menuItems, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case 0:
                                                        File file = StorageUtil.saveImageToGallery(PhotoViewPagerActivity.this, ((BitmapDrawable) imageView.getDrawable()).getBitmap());
                                                        LToast.showToast("图片已保存到" + file.getAbsolutePath());
                                                        break;
                                                }
                                            }
                                        })
                                        .show();
                            }
                        });
                    }
                }

                @Override
                public void onStarted() {

                }

                @Override
                public void onError(ErrorCause errorCause) {

                }

                @Override
                public void onCanceled(CancelCause cancelCause) {

                }
            });
            imageView.displayImage(imageUri);

            container.addView(layout);
            imageView.getImageZoomer().setOnViewTapListener(new ImageZoomer.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    finish();
                }
            });
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return mDrawableUrl.size();
        }
    }
}
