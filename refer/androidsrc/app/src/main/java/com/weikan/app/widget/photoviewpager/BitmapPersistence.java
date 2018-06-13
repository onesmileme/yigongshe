package com.weikan.app.widget.photoviewpager;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class BitmapPersistence {
	
	private static BitmapPersistence mBitmapPersistence;
	
	private BitmapPersistence(){
		
	}
	
	public static BitmapPersistence getInstance(){
		if(mBitmapPersistence == null){
			mBitmapPersistence = new BitmapPersistence();
		}
		return mBitmapPersistence;
	}

	public ArrayList<Drawable> mDrawable = new ArrayList<Drawable>();
	public ArrayList<String> mDrawableUrl = new ArrayList<String>();

	public void clean() {
		mDrawable.clear();
		mDrawableUrl.clear();
	}
}
