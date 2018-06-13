package com.weikan.app.news

import android.os.Build
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.util.Pair
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.weikan.app.R
import com.weikan.app.base.BaseFragment
import com.weikan.app.common.widget.SimpleNavigationView
import com.weikan.app.listener.OnNoRepeatClickListener
import com.weikan.app.news.adapter.HomePagerAdapter
import com.weikan.app.news.bean.CategoryListData
import com.weikan.app.news.event.AutoScrollEvent
import com.weikan.app.news.utils.CategoryManager
import com.weikan.app.news.utils.SimpleTimer
import com.weikan.app.search.SearchActivity
import com.weikan.app.util.IntentUtils
import platform.http.responsehandler.JsonResponseHandler
import java.util.*

/**
 * @author kailun on 16/11/13.
 */
class NewsHomeFragment : BaseFragment() {

    private lateinit var navigation: SimpleNavigationView
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var llNoNet: LinearLayout


    private var tabsInfo = ArrayList<Pair<String, Int>>()
    private var selectedNum = 0
    private val timer = SimpleTimer(AutoScrollEvent::class.java, 5000, 5000)

    init {
        tabsInfo.add(Pair("首页", 2))
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(TemplateConfig.getConfig().layoutHome, null)
        initViews(view)
        initTags();
        return view
    }

    override fun onResume() {
        super.onResume()
        timer.start()
    }

    override fun onPause() {
        super.onPause()
        timer.stop()
    }

    private fun initViews(view: View) {
        setStatusBarTransparent(view.findViewById(R.id.status_margin))
        navigation = view.findViewById(R.id.navigation) as SimpleNavigationView

        tabLayout = view.findViewById(R.id.tab_layout) as TabLayout
        viewPager = view.findViewById(R.id.view_pager) as ViewPager
        llNoNet = view.findViewById(R.id.ll_no_net) as LinearLayout

        llNoNet.setOnClickListener(object : OnNoRepeatClickListener() {
            override fun onNoRepeatClick(v: View?) {
                getCategoryListConfig()
            }
        })

        navigation.setRightOnClickListener({
            IntentUtils.to(activity, SearchActivity::class.java)
        }, 1000)
    }

    private fun getCategoryListConfig() {
        NewsAgent.getCategoryListConfig(object : JsonResponseHandler<CategoryListData>() {
            override fun success(data: CategoryListData) {
                CategoryManager.getInstance().saveCategoryListData(context, data.categoryList)
                initTags()
            }
        })
    }

    private fun initTags() {
        // 读取配置中的分类列表
        val tmpTabsInfo = CategoryManager.getInstance().readCategoryListData(activity)
        if (tmpTabsInfo.size != 0) {
            tabsInfo.clear()
            tabsInfo.addAll(tmpTabsInfo)
            llNoNet.visibility = View.GONE
        } else {
            llNoNet.visibility = View.VISIBLE
        }

        if (tabsInfo.size > 4) {
            tabLayout.tabGravity = TabLayout.GRAVITY_CENTER
        } else {
            tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        }

        val adapter = HomePagerAdapter(childFragmentManager)
        adapter.setPairs(tabsInfo)

        // 不超过4个，固定宽度
        // 超过4个，滚动
        if (tabsInfo.size <= 4) {
            tabLayout.tabMode = TabLayout.MODE_FIXED
        } else {
            tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        }
        viewPager.adapter = adapter

        if (ViewCompat.isLaidOut(tabLayout)) {
            tabLayoutSetWithViewPager()
        } else {
            tabLayout.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                    tabLayoutSetWithViewPager()
                    tabLayout.removeOnLayoutChangeListener(this)
                }
            })
        }
    }

    private fun tabLayoutSetWithViewPager() {
        val pagerAdapter = viewPager.adapter as HomePagerAdapter
        tabLayout.setupWithViewPager(viewPager)
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.setOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                super.onTabReselected(tab)
                if (tab!!.position == selectedNum) {
                    /**
                     * 再次点击刷新页面
                     * 获取fragment，直接拷贝并修改了[android.support.v4.app.FragmentPagerAdapter]
                     * 下的instantiateItem方法
                     * 在当前v4包没问题，当升级v4包后，可能有隐患
                     */
                    val fragment = pagerAdapter.getItemAtPosition(tab.position)
                    if (fragment != null && fragment is NewsFragment) {
                        fragment.refreshData()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                super.onTabUnselected(tab)
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                super.onTabSelected(tab)
                selectedNum = tab.position
            }
        })
        if (tabLayout.selectedTabPosition !== viewPager.currentItem) {
            tabLayout.getTabAt(viewPager.currentItem)?.select()
        }
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
}
