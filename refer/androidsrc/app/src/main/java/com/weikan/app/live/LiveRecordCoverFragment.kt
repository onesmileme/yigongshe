package com.weikan.app.live

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.*
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import com.alibaba.fastjson.JSON
import com.jakewharton.rxbinding.view.RxView
import com.weikan.app.R
import com.weikan.app.account.AccountManager
import com.weikan.app.base.BaseFragment
import com.weikan.app.common.net.JsonArrayResponseHandler
import com.weikan.app.common.net.Page
import com.weikan.app.common.util.TickTimer
import com.weikan.app.live.adapter.LiveEventListAdapter
import com.weikan.app.live.adapter.OnlineUserListAdapter
import com.weikan.app.live.bean.GiftObject
import com.weikan.app.live.bean.LiveEventObject
import com.weikan.app.live.bean.OnlineUserListDataObject
import com.weikan.app.live.widget.InputDialog
import com.weikan.app.personalcenter.UserHomeActivity
import com.weikan.app.util.DensityUtil
import com.weikan.app.util.LToast
import com.weikan.app.view.GiftView
import platform.http.responsehandler.ConfusedJsonResponseHandler
import platform.http.responsehandler.JsonResponseHandler
import platform.http.responsehandler.SimpleJsonResponseHandler
import platform.http.result.FailedResult
import rx.functions.Action0
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 直播，推流界面的覆盖层
 * @author kailun on 16/9/10.
 */
class LiveRecordCoverFragment : BaseFragment() {

    // UI
    private lateinit var tvOnlineUserCount: TextView
    private lateinit var ivCamera: ImageView
    private lateinit var ivExit: ImageView

    private lateinit var rvOnlineUser: RecyclerView
    private lateinit var ivEvent: ImageView
    private lateinit var lvEvent: ListView
    private lateinit var giftLl: LinearLayout
    private var giftList : List<GiftObject> ?= null
    private var inputDialog: InputDialog? = null
    // Adapter
    private lateinit var onlineUserListAdapter: OnlineUserListAdapter
    private lateinit var liveEventListAdapter: LiveEventListAdapter

    // fields
    private var liveId: Long = 0
    private var tickTimer: TickTimer? = null
    private var giftTimer: TickTimer? = null;
    /* package */ var actionClose: Action0? = null
    /* package */ var actionSwitchCamera: Action0? = null
    private  var lasttime = 0L

    var isRefreshing = false

    private fun parseArguments() {
        val arguments = this.arguments
        liveId = arguments?.getLong(BUNDLE_LIVE_ID) ?: 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArguments()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        onlineUserListAdapter = OnlineUserListAdapter(context)
        liveEventListAdapter = LiveEventListAdapter(context)

        val view = inflater!!.inflate(R.layout.fragment_live_record_cover, container, false)
        assignViews(view)
        initViews(view)

        getOnlineUsers()
        getEvents()

        initTickTimer()
        return view
    }

    override fun onPause() {
        super.onPause()
        tickTimer?.stop()
        giftTimer?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        tickTimer?.stop()
        giftTimer?.stop()
    }
    override fun onResume() {
        super.onResume()
        tickTimer?.start()
        giftTimer?.start()
    }

    private fun onEventClick() {
        if (!AccountManager.getInstance().isLogin) {
            AccountManager.getInstance().gotoLogin(activity)
            return
        }

        val dialog = inputDialog ?: InputDialog(context, false)
        dialog.setOnConfirmFunc({ s ->
            val isEmpty = !TextUtils.isEmpty(s?.trim())
            if (isEmpty) {
                postNewMessageEvent(s)
            }
            isEmpty
        })
        dialog.show()
        inputDialog = dialog
    }

    private fun assignViews(view: View) {
        tvOnlineUserCount = view.findViewById(R.id.tv_online_user_count) as TextView
        rvOnlineUser = view.findViewById(R.id.rv_online_user) as RecyclerView
        giftLl = view.findViewById(R.id.gift) as LinearLayout
        ivCamera = view.findViewById(R.id.iv_camera) as ImageView
        ivExit = view.findViewById(R.id.iv_exit) as ImageView

        ivEvent = view.findViewById(R.id.iv_event) as ImageView
        lvEvent = view.findViewById(R.id.lv_event) as ListView
    }

    private fun initViews(view: View) {
        tvOnlineUserCount.text = "在线人数：" + 0

        val layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        rvOnlineUser.layoutManager = layoutManager
        rvOnlineUser.adapter = onlineUserListAdapter

        onlineUserListAdapter.setOnItemClickListener { itemView ->
            val obj = itemView.get()
            if (obj != null) {
                val intent = UserHomeActivity.makeIntent(activity, obj.uid)
                activity.startActivity(intent)
            }
        }

        RxView.clicks(ivCamera)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe { actionSwitchCamera?.call() }
        RxView.clicks(ivExit)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe { actionClose?.call() }

        lvEvent.adapter = liveEventListAdapter

        ivEvent.setOnClickListener { onEventClick() }
    }

    private fun initTickTimer() {
        tickTimer = TickTimer(5000, 5000, Runnable {
            getOnlineUsers()
            getEvents()
        })
       giftTimer = TickTimer(1000,1000, Runnable {
           getGift()
       })
    }
   private fun getGift(){
       giftTimer!!.stop()
       if(giftList == null || giftList!!.size==0){
           if(lasttime == 0L){
               lasttime = Date().time
           }
       }else{
           lasttime = giftList!!.get(giftList!!.size-1).timestamp
       }
       if(!isDetached){
           LiveAgent.getGiftList(liveId,lasttime,object : JsonArrayResponseHandler<GiftObject>() {
               override fun success(data:ArrayList<GiftObject>) {
                   giftList = data
                   if(context == null){
                       giftTimer!!.stop()
                       return
                   }
                   if(data != null && data.size != 0){
                       var i = 0
                       giftLl.removeAllViews()
                       for(gif in data){
                           var view = GiftView.creatGiftView(context,gif.user.headimgurl,gif.user.nickname,"送了一个"+gif.gift.name,gif.gift.url)
                           if(i == 0){
                               giftLl.addView(view)
                           }else{
                               val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                       LinearLayout.LayoutParams.WRAP_CONTENT)
                               lp.setMargins(0, DensityUtil.dip2px(context,10f), 0, 0)
                               view.setLayoutParams(lp)
                               giftLl.addView(view)
                           }
                           i++
                       }
                       giftLl.invalidate()
                       var start = -DensityUtil.dip2px(context,206f)
                       val animator = ValueAnimator.ofFloat(start.toFloat(), 0f)
                       animator.setDuration(500).start()
                       animator.addUpdateListener { animation -> giftLl.setTranslationX(animation.animatedValue as Float) }
                       var listener = object : Animator.AnimatorListener{
                           override fun onAnimationRepeat(animation: Animator?) {
                           }

                           override fun onAnimationStart(animation: Animator?) {
                           }

                           override fun onAnimationCancel(animation: Animator?) {
                           }

                           override fun onAnimationEnd(animation: Animator?) {
                               Handler().postDelayed(Runnable {
                                   if(context != null){
                                       var start = 0-DensityUtil.dip2px(context,206f)
                                       val animator = ValueAnimator.ofFloat(0f,start.toFloat())
                                       animator.addUpdateListener { animation -> giftLl.setTranslationX(animation.animatedValue as Float) }
                                       animator.setDuration(500).start()
                                       animator.addListener(object : Animator.AnimatorListener{
                                           override fun onAnimationRepeat(animation: Animator?) {
                                           }

                                           override fun onAnimationStart(animation: Animator?) {
                                           }

                                           override fun onAnimationCancel(animation: Animator?) {
                                           }

                                           override fun onAnimationEnd(animation: Animator?) {
                                               giftTimer!!.start()
                                           }
                                       })
                                   }
                               },1000)
                           }
                       }
                       animator.addListener(listener)
                   }else{
                       if(context != null && !isDetached){
                           giftTimer!!.start()
                       }
                   }
               }
               override fun failed(r: FailedResult?) {
                   giftTimer!!.start()
                   r?.setIsHandled(true)
               }
           })
       }
   }

    fun rotateyAnimRun(view: View) {
        val anim = ObjectAnimator//
                .ofFloat(view, "zhy", 1.0f, 0.0f)//
                .setDuration(500)//
        anim.start()
        anim.addUpdateListener { animation ->
            val cVal = animation.animatedValue as Float
            view.alpha = cVal
            view.scaleX = cVal
            view.scaleY = cVal
        }
    }

    private fun getOnlineUsers() {
        LiveAgent.getOnlineUsers(liveId, object : JsonResponseHandler<OnlineUserListDataObject>() {
            override fun success(data: OnlineUserListDataObject) {
                tvOnlineUserCount.text = "在线人数：" + data.num
                onlineUserListAdapter.items = data.list
                onlineUserListAdapter.notifyDataSetChanged()

//                val count = liveEventListAdapter.count
//                lvEvent.setSelection(count - 1)
            }

            override fun failed(r: FailedResult?) {
                r?.setIsHandled(true)
            }
        })
    }

    private fun getEvents() {
        if (isRefreshing) {
            return
        } else {
            isRefreshing = true
        }
        val last = liveEventListAdapter.last()

        val lastTime = last?.timestamp ?: 0L
        val page = if (lastTime == 0L) Page.NEW else Page.NEXT

        LiveAgent.getEvents(liveId, page, lastTime, object : JsonArrayResponseHandler<LiveEventObject>() {
            override fun success(data: ArrayList<LiveEventObject>) {
                if (page == Page.NEW) {
                    liveEventListAdapter.clear()
                }
                for(d in data){
                    d.liveUid = AccountManager.getInstance().userId
                }
                if(data != null && data.size != 0){
                    liveEventListAdapter.addItems(data)
                    liveEventListAdapter.notifyDataSetChanged()

                    val count = liveEventListAdapter.count
                    lvEvent.setSelection(count - 1)
                }
            }

            override fun failed(r: FailedResult?) {
                r?.setIsHandled(true)
            }

            override fun end() {
                super.end()
                isRefreshing = false
            }
        })
    }

    private fun postNewMessageEvent(content: String) {
        val uid = AccountManager.getInstance().userId
        LiveAgent.postNewEvent(uid, liveId, content, object : SimpleJsonResponseHandler() {
            override fun success() {
                inputDialog?.clearText()
                getEvents()
            }
        })
    }

    companion object {

        private val BUNDLE_LIVE_ID = "bundle_live_id"

        /** package  */
        @JvmStatic
        fun makeFragment(
                liveId: Long): LiveRecordCoverFragment {
            val fragment = LiveRecordCoverFragment()
            val arguments = Bundle()
            arguments.putLong(BUNDLE_LIVE_ID, liveId)
            fragment.arguments = arguments
            return fragment
        }
    }
}
