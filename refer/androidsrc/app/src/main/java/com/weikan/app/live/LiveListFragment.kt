package com.weikan.app.live

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.handmark.pulltorefresh.library.PullToRefreshBase
import com.handmark.pulltorefresh.library.PullToRefreshListView
import com.jakewharton.rxbinding.view.RxView
import com.weikan.app.R
import com.weikan.app.account.AccountManager
import com.weikan.app.base.BaseFragment
import com.weikan.app.common.adater.BaseListAdapter
import com.weikan.app.common.net.Page
import com.weikan.app.common.widget.BaseListItemView
import com.weikan.app.common.widget.NavigationView
import com.weikan.app.listener.LoginListener
import com.weikan.app.live.adapter.LiveListAdapter
import com.weikan.app.live.bean.LiveClosEvent
import com.weikan.app.live.bean.LiveListDataObject
import com.weikan.app.live.bean.LiveListObject
import com.weikan.app.personalcenter.UserHomeActivity
import com.weikan.app.util.LToast
import com.weikan.app.util.PermissionUtil
import com.weikan.app.util.ShareTools
import de.greenrobot.event.EventBus
import platform.http.responsehandler.JsonResponseHandler
import rx.functions.Action1

import java.util.ArrayList
import java.util.Collections
import java.util.concurrent.TimeUnit

/**
 * @author kailun on 16/8/26.
 */
class LiveListFragment : BaseFragment() {

    private lateinit var navigation: NavigationView
    private lateinit var listView: PullToRefreshListView
    private lateinit var adapter: LiveListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        @SuppressLint("InflateParams")
        val view = inflater!!.inflate(R.layout.fragment_live_list, null)
        setStatusBarTransparent(view.findViewById(R.id.status_margin))
        navigation = view.findViewById(R.id.navigation) as NavigationView
        navigation.setRightOnClickListener1({ startLive() })

        listView = view.findViewById(R.id.base_pull_list_view) as PullToRefreshListView
        listView.setOnRefreshListener(object : PullToRefreshBase.OnRefreshListener2<ListView> {
            override fun onPullDownToRefresh(refreshView: PullToRefreshBase<ListView>) {
                val label = DateUtils.formatDateTime(activity,
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_ABBREV_ALL)

                refreshView.loadingLayoutProxy.setLastUpdatedLabel(label)
                onPullDown()
            }

            override fun onPullUpToRefresh(refreshView: PullToRefreshBase<ListView>) {
                val label = DateUtils.formatDateTime(activity,
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_ABBREV_ALL)

                refreshView.loadingLayoutProxy.setLastUpdatedLabel(label)
                onPullUp()
            }
        })

        val actualListView = listView.refreshableView
        listView.mode = PullToRefreshBase.Mode.BOTH

        actualListView.divider = null
        actualListView.dividerHeight = 0

        adapter = LiveListAdapter(inflater.context)
        adapter.setOnItemClickListener { itemView ->
            val obj = itemView.get()
            if (obj != null) {
                if (obj.status == 1 || obj.status == 3) {
                    val intent = LivePlayActivity.makeIntent(activity, obj)
                    activity.startActivity(intent)
                }
            }
        }
        adapter.actionUserClick = Action1 { itemView ->
            val obj = itemView.get()
            if (obj != null) {
                val intent = UserHomeActivity.makeIntent(activity, obj.uid)
                activity.startActivity(intent)
            }
        }

        listView.setAdapter(adapter)
        requestNew()

        return view
    }

    private fun onPullUp() {
        requestNext()
    }

    private fun onPullDown() {
        requestNew()
    }

    private fun startLive() {
        if (!PermissionUtil.isCameraPermission()) {
            LToast.showToast("请开启相机权限再重试")
            return
        }
        if (!PermissionUtil.isVoicePermission()) {
            LToast.showToast("请开启麦克风权限再重试")
            return
        }
        if (!AccountManager.getInstance().isLogin) {
            AccountManager.getInstance().gotoLogin(activity)
            return
        }

        val intent = Intent(activity, NewLiveActivity::class.java)
        activity.startActivity(intent)
    }

    private fun requestNew() {
        LiveAgent.getList(Page.NEW, 0, object : JsonResponseHandler<LiveListDataObject>() {
            override fun success(data: LiveListDataObject) {
                listView.refreshableView.setSelection(0)
                val content = filterInvalidItems(data.content)
                adapter.items = content
                adapter.notifyDataSetChanged()
            }

            override fun end() {
                listView.onRefreshComplete()
            }
        })
    }

    private fun requestNext() {
        var lastLiveId: Long = 0
        val last = adapter.last()
        if (last != null) {
            lastLiveId = last.liveId
        }

        LiveAgent.getList(Page.NEXT, lastLiveId, object : JsonResponseHandler<LiveListDataObject>() {
            override fun success(data: LiveListDataObject) {
                val content = filterInvalidItems(data.content)
                adapter.addItems(content)
                adapter.notifyDataSetChanged()
            }

            override fun end() {
                listView.onRefreshComplete()
            }
        })
    }

    /**
     * 由于MainActivity不能设置status bar，改为页面内设置status bar，透明全屏
     * @param view view
     */
    private fun setStatusBarTransparent(view: View) {
        if (Build.VERSION.SDK_INT >= 19) {
            view.visibility = View.VISIBLE

            val lp = view.layoutParams
            lp.height = statusBarHeight
            view.layoutParams = lp
        }
    }

    private fun filterInvalidItems(data: List<LiveListObject>): List<LiveListObject> {
        return data.filter { it.status == 1 || it.status == 2 || it.status == 3 }
                .toList()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
    fun  onEventMainThread( event : LiveClosEvent) {
        requestNew()
    }
    }
