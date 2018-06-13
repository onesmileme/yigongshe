package com.weikan.app.news.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.ViewGroup;

import com.weikan.app.news.NewsFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/5/2
 */
public class HomePagerAdapter extends FragmentPagerAdapter {

    List<Pair<String, Integer>> pairs = new ArrayList<>();
    ViewGroup container;
    FragmentManager fragmentManager;

    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
        this.fragmentManager = fm;
    }

    @Override
    public void startUpdate(ViewGroup container) {
        super.startUpdate(container);
        this.container = container;
    }

    @Override
    public Fragment getItem(int position) {
        Log.e(getClass().getSimpleName(), "getItem(" + position);

        NewsFragment fragment = new NewsFragment();

        Pair<String, Integer> pair = pairs.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt(NewsFragment.NEWS_SUB_ID, pair.second);
        bundle.putString(NewsFragment.NEWS_SUB_NAME, pair.first);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pairs.get(position).first;
    }

    @Override
    public int getCount() {
        return pairs.size();
    }


    public void setPairs(List<Pair<String, Integer>> pairs) {
        this.pairs = pairs;
    }

    @Nullable
    public Fragment getItemAtPosition(int position) {
        final long itemId = getItemId(position);

        // Do we already have this fragment?
        String name = makeFragmentName(container.getId(), itemId);
        return fragmentManager.findFragmentByTag(name);
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }
}
