package com.weikan.app.wenyouquan;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.jakewharton.rxbinding.view.RxView;
import com.weikan.app.MainApplication;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.account.bean.LogoutEvent;
import com.weikan.app.base.BasePullToRefreshFragment;
import com.weikan.app.bean.ClearRedMsgEvent;
import com.weikan.app.common.Model.RedNoticeModel;
import com.weikan.app.common.manager.FunctionConfig;
import com.weikan.app.group.AllGroupActivity;
import com.weikan.app.group.GroupDetailActivity;
import com.weikan.app.listener.OnNoRepeatClickListener;
import com.weikan.app.original.widget.BannerView;
import com.weikan.app.util.BundleParamKey;
import com.weikan.app.util.DensityUtil;
import com.weikan.app.util.Global;
import com.weikan.app.util.ImageLoaderUtil;
import com.weikan.app.util.IntentUtils;
import com.weikan.app.util.LToast;
import com.weikan.app.util.URLDefine;
import com.weikan.app.wenyouquan.adapter.WenyouListAdapter;
import com.weikan.app.wenyouquan.bean.WenyouBannerObject;
import com.weikan.app.wenyouquan.bean.WenyouListData;
import com.weikan.app.wenyouquan.model.DataTransModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import platform.http.HttpUtils;
import platform.http.responsehandler.ConfusedJsonResponseHandler;
import rx.functions.Action1;

/**
 * 文友圈列表
 */
public class WenyouListFragment extends BasePullToRefreshFragment {

    // 顶部筛选项的集合
    List<FilterEntity> filterEntityList = new ArrayList<>();

    private WenyouListAdapter adapter;
    private ArrayList<WenyouListData.WenyouListItem> dataList = new ArrayList<>();
    private ArrayList<WenyouListData.HotGroup> hotGroupList = new ArrayList<>();

    TextView titleText;
    View titleLayout;

    //    private LinearLayout mBannerLinearLayout;
    private BannerView bannerView;
    // 热门群组view
    private ViewGroup hotGroupView;

    public PopupWindow pop;
    public int curPopSel = 0;


    int titlecolor;
    int titlecolorAlpha;

    /**
     * 筛选项的实体类
     */
    class FilterEntity {
        public String type;
        public String title;
        public int imageId;
        public boolean needLogin = false;

        public FilterEntity(String type, String title, int imageId) {
            this.type = type;
            this.title = title;
            this.imageId = imageId;
        }

        public FilterEntity(String type, String title, int imageId, boolean needLogin) {
            this.type = type;
            this.title = title;
            this.imageId = imageId;
            this.needLogin = needLogin;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        filterEntityList.clear();
        filterEntityList.add(new FilterEntity("0", "全部", R.drawable.selector_icon_wenyou_pop_all));
        filterEntityList.add(new FilterEntity("1", "精华", R.drawable.selector_icon_wenyou_pop_cream));
        filterEntityList.add(new FilterEntity("2", MainApplication.getInstance().getResources().getString(R.string.app_name),
                R.drawable.selector_icon_wenyou_pop_owner));
        filterEntityList.add(new FilterEntity("4", "我的关注", R.drawable.selector_icon_wenyou_pop_mine, true));
        if (FunctionConfig.getInstance().isSupportWenyouGroup()) {
            filterEntityList.add(new FilterEntity("5", "我的群组", R.drawable.selector_icon_wenyou_pop_group, true));
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(LogoutEvent event) {
        selectList(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (adapter == null) {
            adapter = new WenyouListAdapter(getActivity(), dataList);
            adapter.setOnItemCommentMoreClickListener(new WenyouListAdapter.onItemCommentMoreClickListener() {
                @Override
                public void onItemCommentMoreClick(final int position) {
                    if (position <= actualListView.getFirstVisiblePosition() || position > actualListView.getLastVisiblePosition()) {
                        actualListView.smoothScrollToPosition(position);
                    }
                }
            });
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titlecolor = getResources().getColor(R.color.news_titlebar_bg);
        titlecolorAlpha = Color.alpha(titlecolor);

        view.findViewById(R.id.base_pull_title).setVisibility(View.GONE);

        titleText = ((TextView) view.findViewById(R.id.tv_wenyou_titlebar_title));
        titleText.setText("全部");
        Drawable arrow = getActivity().getResources().getDrawable(R.drawable.selector_arrow_updown);
        arrow.setBounds(0, 0, arrow.getIntrinsicWidth(), arrow.getIntrinsicHeight());
        titleText.setCompoundDrawables(null, null, arrow, null);
        ImageView rightBtn = (ImageView) view.findViewById(R.id.iv_wenyou_titlebar_right);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setImageResource(R.drawable.wenyoupub);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AccountManager.getInstance().isLogin()) {
                    AccountManager.getInstance().gotoLogin(getActivity());
                    return;
                }
                Intent intent = new Intent(getActivity(), WenyouPubActivity.class);
                startActivity(intent);
            }
        });

        titleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleText.setSelected(true);
                showpop(view);
            }
        });

        titleLayout = view.findViewById(R.id.rl_wenyou_titlebar);
        titleLayout.setVisibility(View.VISIBLE);
        final LinearLayout line = (LinearLayout) view.findViewById(R.id.ll_line);

        setStatusBarTransparent(titleLayout);
        setStatusBarTransparent(line);

        getPullRefreshListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                View item = view.getChildAt(0);
                if (item != null && firstVisibleItem <= 1) {
                    int h = item.getTop() / 2;
                    int a = Math.abs(h);
                    a = a < titlecolorAlpha ? a : titlecolorAlpha;
//                    titleLayout.getBackground().setAlpha(a < 255 ? a : 255);
                    line.setBackgroundColor(a << 24 | (titlecolor & 0x00ffffff));
                } else {
                    line.setBackgroundColor(titlecolor);
                }
            }
        });

        sendNewRequest(-1);
        sendBannerRequest();

        updateTitle();

    }

    @Override
    protected View makeHeadView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.wenyouquan_header, null);

        bannerView = (BannerView) view.findViewById(R.id.bv_wenyou_banner);
        hotGroupView = (ViewGroup) view.findViewById(R.id.rl_hot_group);

        WindowManager wm = getActivity().getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, width / 9 * 5);
        bannerView.setLayoutParams(layoutParams);

        return view;
    }

    public void showpop(View v) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.popmenu, null);
        LinearLayout container = (LinearLayout) view.findViewById(R.id.ll_pop_menu_container);
        for (int i = 0; i < filterEntityList.size(); i++) {
            final FilterEntity entity = filterEntityList.get(i);
            View filterItemView = LayoutInflater.from(getActivity()).inflate(R.layout.popmenu_item, null);
            final int finalI = i;
            filterItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pop != null)
                        pop.dismiss();

                    if (entity.needLogin && !AccountManager.getInstance().isLogin()) {
                        AccountManager.getInstance().gotoLogin(getActivity());
                        return;
                    }
                    selectList(finalI);
                }
            });

            if (i == filterEntityList.size() - 1) {
                filterItemView.findViewById(R.id.iv_pop_menu_item_split).setVisibility(View.GONE);
            } else {
                filterItemView.findViewById(R.id.iv_pop_menu_item_split).setVisibility(View.VISIBLE);
            }
            ((ImageView) filterItemView.findViewById(R.id.iv_wenyou_pop_all)).setImageResource(entity.imageId);
            ((TextView) filterItemView.findViewById(R.id.tv_wenyou_pop_all)).setText(entity.title);

            container.addView(filterItemView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Global.dpToPx(getActivity(), 34)));
        }

        if (curPopSel >= 0 && curPopSel < filterEntityList.size()) {
            container.getChildAt(curPopSel).findViewById(R.id.iv_wenyou_pop_all).setSelected(true);
            container.getChildAt(curPopSel).findViewById(R.id.tv_wenyou_pop_all).setSelected(true);
        }

        int w = Global.dpToPx(getActivity(), 200);
        pop = new PopupWindow(view, w,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        // 需要设置一下此参数，点击外边可消失
        pop.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        pop.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        pop.setFocusable(true);
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        int width = v.getMeasuredWidth();
        int xoffset = width - w;
        pop.showAsDropDown(v, xoffset / 2, 0);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                titleText.setSelected(false);
            }
        });
    }

    private void updateTitle() {
        if (curPopSel >= 0 && curPopSel < filterEntityList.size()) {
            titleText.setText(filterEntityList.get(curPopSel).title);
        }
    }

    private void updateHotGroup() {
        if (FunctionConfig.getInstance().isSupportWenyouGroup() && hotGroupList.size() > 0) {
            hotGroupView.setVisibility(View.VISIBLE);
            innerUpdateHotGroup(0, R.id.ll_hot_group_1, R.id.tv_hot_group_name1, R.id.tv_hot_group_people1, R.id.iv_hot_group_bg1);
            innerUpdateHotGroup(1, R.id.ll_hot_group_2, R.id.tv_hot_group_name2, R.id.tv_hot_group_people2, R.id.iv_hot_group_bg2);
            innerUpdateHotGroup(2, R.id.ll_hot_group_3, R.id.tv_hot_group_name3, R.id.tv_hot_group_people3, R.id.iv_hot_group_bg3);
            hotGroupView.findViewById(R.id.tv_hot_group_more).setOnClickListener(new OnNoRepeatClickListener() {
                @Override
                public void onNoRepeatClick(View v) {
                    IntentUtils.to(getActivity(), AllGroupActivity.class);
                }
            });
        } else {
            hotGroupView.setVisibility(View.GONE);
        }
    }

    private void innerUpdateHotGroup(int index, int groupId, int nameId, int introId, int bgId) {
        final View group1 = hotGroupView.findViewById(groupId);

        group1.post(new Runnable() {
            @Override
            public void run() {
                WindowManager wm = getActivity().getWindowManager();
                int width = (wm.getDefaultDisplay().getWidth() - DensityUtil.dip2px(getContext(), 30)) / 3;
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width / 9 * 5);
                layoutParams.setMargins(DensityUtil.dip2px(getContext(), 5), 0, DensityUtil.dip2px(getContext(), 5), 0);
                group1.setLayoutParams(layoutParams);
            }
        });

        if (index >= 0 && index < hotGroupList.size()) {
            group1.setVisibility(View.VISIBLE);
            final WenyouListData.HotGroup hotData1 = hotGroupList.get(index);
            TextView tvName1 = (TextView) group1.findViewById(nameId);
            TextView tvPeople1 = (TextView) group1.findViewById(introId);
            final ImageView ivBg = (ImageView) group1.findViewById(bgId);
            tvName1.setText(hotData1.name);
            tvPeople1.setText(hotData1.follow_count + "人");

            ImageLoaderUtil.updateImage(ivBg, hotData1.hot_background_pic != null ? hotData1.hot_background_pic.getImageUrlBig() : "");
            RxView.clicks(group1)
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            Intent intent = new Intent(getActivity(), GroupDetailActivity.class);
                            intent.putExtra(BundleParamKey.GROUPID, hotData1.group_id);
                            intent.putExtra(BundleParamKey.GROUPNAME, hotData1.name);
                            getActivity().startActivity(intent);
                        }
                    });
        } else {
            group1.setVisibility(View.GONE);
        }
    }

    /**
     * 设置status bar，透明全屏，如果是4.4以上，就设置一个padding top，否则，什么都不做
     *
     * @param view
     */
    public void setStatusBarTransparent(View view) {
        if (Build.VERSION.SDK_INT >= 19) {
            int statusHeight = getStatusBarHeight();
//            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
//            int originalPaddingTop = lp.topMargin;
//            lp.topMargin = originalPaddingTop + statusHeight;
//            view.setLayoutParams(lp);
            view.setPadding(0, view.getPaddingTop() + statusHeight, 0, view.getPaddingBottom());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DataTransModel.needRefresh) {
            // 发帖后强制筛选改为 全部
            curPopSel = 0;
            updateTitle();
            executeRefreshing();
            DataTransModel.needRefresh = false;
        }
    }

    @Override
    protected BaseAdapter getAdapter() {
        return adapter;
    }

    public void executeRefreshing() {
        if (getPullRefreshListView() != null) {
            getPullRefreshListView().setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            getPullRefreshListView().setRefreshing();
            getPullRefreshListView().setMode(PullToRefreshBase.Mode.BOTH);
        }
    }

    @Override
    protected void onPullDown() {
        super.onPullDown();
        sendNewRequest(-1);
        sendBannerRequest();
    }

    @Override
    protected void onPullUp() {
        super.onPullUp();
        if (dataList.size() != 0) {
            WenyouListData.WenyouListItem item = dataList.get(dataList.size() - 1);
            sendNextRequest(item.ctime);
        } else {
            sendNewRequest(-1);
        }
    }

    private void sendNewRequest(long ctime) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.WENYOU_LIST);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TYPE, TYPE_NEW);
        params.put("filter_type", filterEntityList.get(curPopSel).type);
        HttpUtils.get(builder.build().toString(), params, new platform.http.responsehandler.AmbJsonResponseHandler<WenyouListData>() {
            @Override
            public void success(@Nullable WenyouListData data) {
                dataList.clear();
                hotGroupList.clear();
                if (data == null) {
                    LToast.showToast("暂无内容。");
                } else {
                    dataList.addAll(data.content);
                    hotGroupList.addAll(data.hot_group);
                }
                adapter.notifyDataSetChanged();
                updateHotGroup();

                if (data == null) {
                    return;
                }

                RedNoticeModel.saveMomentRefreshTime();
                EventBus.getDefault().post(new ClearRedMsgEvent(ClearRedMsgEvent.CLEAR_MOMENT));
            }

            @Override
            public void end() {
                getPullRefreshListView().onRefreshComplete();
            }
        });
    }

    private void sendNextRequest(long ctime) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.WENYOU_LIST);

        Map<String, String> params = new HashMap<String, String>();
        params.put(URLDefine.UID, AccountManager.getInstance().getUserId());
        params.put(URLDefine.TOKEN, AccountManager.getInstance().getSession());
        params.put(URLDefine.TYPE, TYPE_NEXT);
        params.put("filter_type", filterEntityList.get(curPopSel).type);
        params.put("last_ctime", String.valueOf(ctime));

        HttpUtils.get(builder.build().toString(), params, new platform.http.responsehandler.AmbJsonResponseHandler<WenyouListData>() {
            @Override
            public void success(@Nullable WenyouListData data) {
                if (data == null || data.content == null || data.content.size() == 0) {
                    LToast.showToast("没有更多内容了。");
                    return;
                }
                dataList.addAll(data.content);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void end() {
                getPullRefreshListView().onRefreshComplete();
            }
        });
    }

    private void selectList(int popSel) {
        curPopSel = popSel;
        clearAndForceRefresh();
        updateTitle();
    }

    void clearAndForceRefresh() {
        dataList.clear();
        adapter.notifyDataSetChanged();
        getPullRefreshListView().setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        getPullRefreshListView().setRefreshing();
        getPullRefreshListView().setMode(PullToRefreshBase.Mode.BOTH);
    }


    private void sendBannerRequest() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLDefine.SCHEME);
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API);
        builder.encodedPath(URLDefine.BANNER_LISTS);
        Map<String, String> params = new HashMap<>();
        HttpUtils.get(builder.build().toString(), params, new ConfusedJsonResponseHandler<WenyouBannerObject>() {

            @Override
            public void success(@NonNull WenyouBannerObject data) {
                if (data.data != null && data.data.size() > 0) {
                    bannerView.setData(data.data);
                    bannerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
