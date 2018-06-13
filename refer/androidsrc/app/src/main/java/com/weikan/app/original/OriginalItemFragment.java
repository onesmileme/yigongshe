package com.weikan.app.original;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jakewharton.rxbinding.widget.AdapterViewItemClickEvent;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.weikan.app.Constants;
import com.weikan.app.MainApplication;
import com.weikan.app.MainFragment;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BaseFragment;
import com.weikan.app.bean.ClearRedMsgEvent;
import com.weikan.app.common.Model.RedNoticeModel;
import com.weikan.app.original.adapter.OriginalMainAdapter;
import com.weikan.app.original.adapter.WeMoneyAdapter;
import com.weikan.app.original.bean.OriginalBanner;
import com.weikan.app.original.bean.OriginalDetailItem;
import com.weikan.app.original.bean.OriginalItem;
import com.weikan.app.original.bean.OriginalItemData;
import com.weikan.app.original.widget.SearchHeaderView;
import com.weikan.app.search.SearchActivity;
import com.weikan.app.util.DensityUtil;
import com.weikan.app.util.JumpUtil;
import com.weikan.app.util.LLog;
import com.weikan.app.util.LToast;
import com.weikan.app.util.PrefDefine;
import com.weikan.app.util.URLDefine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import platform.http.HttpUtils;
import platform.http.responsehandler.AmbJsonResponseHandler;
import rx.functions.Action1;

/**
 * abstract of class/interface and so on
 *
 * @author liujian on 16/2/3
 */
public class OriginalItemFragment extends BaseFragment implements MainFragment.ItemFragment {
    private View mRootView;
    private ListView mListView;
    private OriginalMainAdapter mOriginalMainAdapter;
    private View mListHeadView;
    private ArrayList<OriginalItem> mOriginalObjectList = new ArrayList<>();
    private LinearLayout mBannerLinearLayout;

    private PullToRefreshListView mPullRefreshListView;

    public static final String TYPE_NEW = "new";
    public static final String TYPE_APPEND = "append";
    public static final String TYPE_NEXT = "next";

    private String tabName = "";
    private int tabType = 0;

    private ArrayList<OriginalBanner.Content> mBannerContentList;
    private SearchHeaderView searchHeaderView;

    private int filter = 0;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle b = getArguments();
        if (b != null) {
            tabName = b.getString(Constants.TAB_NAME);
            tabType = b.getInt(Constants.TAB_ID);
        }
        View contentView = inflater.inflate(R.layout.fragment_original_item, null);
        updateUI(contentView);
//        if (tabType == HomeConst.HOME_RECOMMAND) {
        requestIfNeed();
//        }
        EventBus.getDefault().register(mOriginalMainAdapter);
        return contentView;
    }


    @Override
    public void onResume() {
        super.onResume();
        mOriginalMainAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void recordClickTime() {
        if (getActivity() == null) {
            return;
        }
        SharedPreferences sp = getActivity().getSharedPreferences(PrefDefine.PREF_FILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("indextime", System.currentTimeMillis() / 1000);
        editor.apply();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void updateUI(View view) {
        if (view == null) {
            return;
        }

        mPullRefreshListView = (PullToRefreshListView) view.findViewById(R.id.original_main_listview);
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);
                sendNewRequest(-1);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);
                if (mOriginalObjectList != null && mOriginalObjectList.size() > 0) {
                    long ctime = mOriginalObjectList.get(mOriginalObjectList.size() - 1).pubtime;
                    if (tabType == HomeConst.HOME_TOP && mOriginalObjectList.get(mOriginalObjectList.size() - 1).top != null) {
                        ctime = mOriginalObjectList.get(mOriginalObjectList.size() - 1).top.time;
                    }
                    sendNextRequest(ctime);
                } else {
                    mPullRefreshListView.onRefreshComplete();
                }
            }
        });

        mListView = mPullRefreshListView.getRefreshableView();
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        if (com.weikan.app.BuildConfig.ARTICLE_TEMPLATE.equals("single_wemoney")) {
            mOriginalMainAdapter = new WeMoneyAdapter(getActivity());
        } else if (com.weikan.app.BuildConfig.ARTICLE_TEMPLATE.equals("single_wenjing")) {
            mOriginalMainAdapter = new OriginalMainAdapter(getActivity());
        }
        mListView.setAdapter(mOriginalMainAdapter);
        mListView.setDivider(new ColorDrawable(MainApplication.getInstance().getResources().getColor(R.color.gray)));
        mListView.setDividerHeight(DensityUtil.dip2px(getActivity(), 8));
        mListView.setSelection(mListView.getHeaderViewsCount() - 1);

        searchHeaderView = new SearchHeaderView(getActivity());
        mListHeadView = LayoutInflater.from(getActivity()).inflate(R.layout.original_main_list_header, null);
        mListView.addHeaderView(searchHeaderView);
        mListView.addHeaderView(mListHeadView);

        // SearchHeaderView在没数据的时候，先藏起来
        if (mOriginalObjectList.size() == 0) {
            searchHeaderView.setVisibility(View.GONE);
        }
        searchHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivityForResult(intent, Constants.ARTICLE_SEARCH_REQUEST_CODE);
            }
        });

        if (tabType == HomeConst.HOME_RECOMMAND) {
            mOriginalMainAdapter.setShowTime(false);
        }
        mOriginalMainAdapter.setContent(mOriginalObjectList);

        mBannerLinearLayout = (LinearLayout) mListHeadView.findViewById(R.id.original_banner_ll);
        if (tabType != HomeConst.HOME_RECOMMAND) {
            mBannerLinearLayout.setVisibility(View.GONE);
        } else {
            mBannerLinearLayout.setVisibility(View.VISIBLE);
        }

        // 用RxBinding代替Click Listener,滤除抖动
        RxAdapterView.itemClickEvents(mListView)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<AdapterViewItemClickEvent>() {
                    @Override
                    public void call(AdapterViewItemClickEvent adapterViewItemClickEvent) {
                        if (mOriginalObjectList == null) {
                            return;
                        }
                        int pos = adapterViewItemClickEvent.position() - mListView.getHeaderViewsCount();
                        if (pos < 0 || pos >= mOriginalObjectList.size()) {
                            return;
                        }
                        OriginalItem originalObject = mOriginalObjectList.get(pos);
                        if (originalObject == null || TextUtils.isEmpty(originalObject.schema)) {
                            return;
                        }
                        Bundle bundle = new Bundle();
                        bundle.putInt("requestCode", Constants.ARTICLE_DETAIL_REQUEST_CODE);
                        JumpUtil.executeSchema(originalObject.schema, getActivity(), bundle);

                        LLog.e("key:" + System.currentTimeMillis());
                    }
                });

    }

    @Override
    public void executeRefreshing() {
        if (mPullRefreshListView != null) {
            mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            mPullRefreshListView.setRefreshing();
            mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        }
    }

    @Override
    public void requestIfNeed() {
        if (mOriginalObjectList.size() == 0) {
            sendNewRequest(-1);
        }
    }

    private MyHttpResponseHandler httpHandler = new MyHttpResponseHandler();


    public class MyHttpResponseHandler extends AmbJsonResponseHandler<OriginalItemData> {

        private String refreshType = TYPE_NEW;

        public void setRefreshType(String refreshType) {
            this.refreshType = refreshType;
        }

        @Override
        public void success(@Nullable OriginalItemData data) {
            if (data == null || data.content == null || data.content.size() == 0) {
                LToast.showToast("暂无更多内容");
                return;
            }
            ArrayList<OriginalItem> newOriginalObjects = data.content;
            if (refreshType.equals(TYPE_NEW)) {
                // 下拉刷新，清空后最新的从头开始加
                if (mOriginalObjectList != null) {
                    mOriginalObjectList.clear();
                    mOriginalObjectList.addAll(0, newOriginalObjects);
                    mOriginalMainAdapter.notifyDataSetChanged();
                    recordClickTime();

                    // 有数据了再把SearchHeaderView显示出来
                    searchHeaderView.setVisibility(View.VISIBLE);
                    mListView.setSelection(mListView.getHeaderViewsCount() - 1); // -1是因为有Banner

                    RedNoticeModel.saveTweetRefreshTime();
                    EventBus.getDefault().post(new ClearRedMsgEvent(ClearRedMsgEvent.CLEAR_TWEET));
                }
            } else if (refreshType.equals(TYPE_APPEND)) {
                if (mOriginalObjectList != null) {
                    mOriginalObjectList.addAll(0, newOriginalObjects);
                    mOriginalMainAdapter.notifyDataSetChanged();
                }
            } else {
                // 上拉加载更多，从最后加
                if (mOriginalObjectList != null) {
                    int size = mOriginalObjectList.size();
                    mOriginalObjectList.addAll(size, newOriginalObjects);
                    mOriginalMainAdapter.notifyDataSetChanged();
                }

            }
        }

        @Override
        public void end() {
            super.end();

            if (mPullRefreshListView != null) {
                mPullRefreshListView.onRefreshComplete();
            }
        }
    }

    private void sendNewRequest(long ctime) {
        Uri.Builder builder = new Uri.Builder();
        Map<String, String> params = new HashMap<>();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.TWEET_MATERIAL);
        params.put(URLDefine.TYPE, TYPE_NEW);
        if (ctime != -1) {
            params.put("first_ctime", String.valueOf(ctime));
        }
        params.put("filter_type", filter + "");
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        httpHandler.setRefreshType(TYPE_NEW);
        HttpUtils.get(builder.build().toString(), params, httpHandler);
    }

    private void sendNextRequest(long ctime) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        Map<String, String> params = new HashMap<>();
        builder.encodedPath(URLDefine.TWEET_MATERIAL);
        params.put(URLDefine.TYPE, TYPE_NEXT);
        params.put("filter_type", filter + "");
        params.put("last_ctime", String.valueOf(ctime));
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        httpHandler.setRefreshType(TYPE_NEXT);
        HttpUtils.get(builder.build().toString(), params, httpHandler);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        mBannerLinearLayout.removeAllViews();
        EventBus.getDefault().unregister(mOriginalMainAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (data == null) {
            return;
        }

        if (requestCode == Constants.ARTICLE_DETAIL_REQUEST_CODE) {
            OriginalDetailItem obj = (OriginalDetailItem) data.getSerializableExtra(Constants.DETAIL_OBJECT);

            if (data.getBooleanExtra(Constants.IS_DELETE, false)) {
                int pos = -1;
                for (int i = 0; i < mOriginalObjectList.size(); i++) {
                    if (mOriginalObjectList.get(i).tid.equals(obj.tid)) {
                        pos = i;
                        break;
                    }
                }
                if (pos != -1) {
                    mOriginalObjectList.remove(pos);
                    mOriginalMainAdapter.notifyDataSetChanged();
                }
            } else {
                for (OriginalItem item : mOriginalObjectList) {
                    if (item.tid.equals(obj.tid)) {
                        item.praise.flag = obj.praise.flag;
                        item.praise.num = obj.praise.num;
                        item.comment.num = obj.comment.num;
                        mOriginalMainAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
        } else if (requestCode == Constants.ARTICLE_SEARCH_REQUEST_CODE) {
            mOriginalObjectList.clear();
            mOriginalMainAdapter.notifyDataSetChanged();
            filter = data.getIntExtra(Constants.SEARCH_FILTER, 0);
            executeRefreshing();
        }
    }
}
