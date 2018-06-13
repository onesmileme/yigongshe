package com.weikan.app.live

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.res.ResourcesCompat
import android.view.KeyEvent
import android.view.WindowManager
import com.qiniu.pili.droid.streaming.StreamingEnv
import com.weikan.app.R
import com.weikan.app.base.BaseActivity
import rx.functions.Action0

/**
 * 直播 - 录制（或者说推流）页面
 * @author kailun on 16/8/17.
 */
class LiveRecordActivity : BaseActivity() {

    private lateinit var fragmentLiveRecord: LiveRecordFragment
    private lateinit var fragmentLiveCoverRecord: LiveRecordCoverFragment

    private var liveId: Long = 0
    private var liveInfo = ""

    companion object {

        private val BUNDLE_LIVE_INFO = "bundle_live_info"
        private val BUNDLE_LIVE_ID = "bundle_live_id"

        @JvmStatic
        fun makeIntent(context: Context, liveId: Long, json: String): Intent {
            val intent = Intent(context, LiveRecordActivity::class.java)
            intent.putExtra(BUNDLE_LIVE_ID, liveId)
            intent.putExtra(BUNDLE_LIVE_INFO, json)
            return intent
        }
    }

    private fun parseIntent() {
        liveId = intent.getLongExtra(BUNDLE_LIVE_ID, 0)
        liveInfo = intent.getStringExtra(BUNDLE_LIVE_INFO)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // 这个必须在直播相关组件使用之前初始化
        StreamingEnv.init(applicationContext)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_record)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

        // 5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ResourcesCompat.getColor(resources, android.R.color.transparent, null)
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) // 保持屏幕常亮
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 设置横竖屏

        parseIntent()
        makeLiveRecordFragment()
        makeLiveRecordCoverFragment()
    }

    override fun openTranslucentStatus(): Boolean {
        return false
    }

    private fun makeLiveRecordFragment() {
        fragmentLiveRecord = LiveRecordFragment.makeFragment(liveInfo)

        val fragmentManager = this.supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fl_live_record, fragmentLiveRecord)
        transaction.commitAllowingStateLoss()
    }

    private fun makeLiveRecordCoverFragment() {
        fragmentLiveCoverRecord = LiveRecordCoverFragment.makeFragment(liveId)
        fragmentLiveCoverRecord.actionClose = Action0 {
            // 在Stop的Handler里面关闭Activity
            fragmentLiveRecord.stopStreaming()
        }
        fragmentLiveCoverRecord.actionSwitchCamera = Action0 { fragmentLiveRecord.switchCamera() }

        val fragmentManager = this.supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fl_live_record_cover, fragmentLiveCoverRecord)
        transaction.commitAllowingStateLoss()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (fragmentLiveRecord.mMediaStreamingManager != null) {
            fragmentLiveRecord.mMediaStreamingManager.stopStreaming()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
//            fragmentLiveRecord.stop()
            fragmentLiveRecord.stopStreaming()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun isSupportSwipBack(): Boolean {
        return false
    }
}
