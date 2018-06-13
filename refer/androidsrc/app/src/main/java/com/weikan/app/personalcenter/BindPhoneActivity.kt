package com.weikan.app.personalcenter

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.jakewharton.rxbinding.view.RxView
import com.weikan.app.R
import com.weikan.app.account.AccountManager
import com.weikan.app.account.bean.LoginResult
import com.weikan.app.base.BaseActivity
import com.weikan.app.util.CheckLegalUtils
import com.weikan.app.util.IntentUtils
import com.weikan.app.util.KeyBoardUtils
import com.weikan.app.util.URLDefine
import platform.http.HttpUtils
import platform.http.responsehandler.JsonResponseHandler
import platform.http.responsehandler.SimpleJsonResponseHandler
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * Created by liujian on 16/12/4.
 */
class BindPhoneActivity: BaseActivity() {

    private var etPhone: EditText? = null
    private var etPwd: EditText? = null
    private var uid: String? = null

    private var subscriber: Subscription? = null

    private var ELLIPSE_TIME:Int = 60

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bind_phone)
        initTitle()
        etPhone = findViewById(R.id.et_mine_bind_phone) as EditText
        etPwd = findViewById(R.id.et_mine_bind_pwd) as EditText
        uid = intent.getStringExtra(URLDefine.UID)
    }

    private fun initTitle() {
        val titleText = findViewById(R.id.tv_titlebar_title) as TextView
        titleText.text = "绑定手机"
        findViewById(R.id.iv_titlebar_back).setOnClickListener { finish() }

        findViewById(R.id.btn_mine_bind_phone).setOnClickListener { sendBindRequest() }
        val verifyBtn = findViewById(R.id.tv_mine_bind_get_verigy) as TextView
        verifyBtn.setOnClickListener {
            if(!sendPhoneVerifyRequest()){
                return@setOnClickListener
            }

            subscriber = Observable.interval(0, 1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .limit(ELLIPSE_TIME +1)
                    .map { ELLIPSE_TIME - it }
                    .doOnSubscribe { verifyBtn.isEnabled = false }
                    .doOnCompleted {
                        verifyBtn.isEnabled = true
                        verifyBtn.text = "获取验证码"
                    }
                    .doOnError { it.printStackTrace() }
                    .subscribe { verifyBtn.text = "剩余 $it 秒" }
        }

        RxView.clicks(findViewById(R.id.tv_bind_phone_deal))
            .throttleFirst(500,TimeUnit.MILLISECONDS)
            .subscribe { IntentUtils.to(this, MineDealActivity::class.java) }
    }


    private fun sendBindRequest() {
        val phoneString = etPhone!!.text.toString()
        val verifyString = etPwd!!.text.toString()
        if (CheckLegalUtils.checkPhone(phoneString) && CheckLegalUtils.checkCode(verifyString)) {
            val builder = Uri.Builder()
            builder.scheme(URLDefine.SCHEME)
            builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API)
            builder.encodedPath(URLDefine.USER_BIND)

            val params = HashMap<String, String>()
            params.put(URLDefine.UID, uid!!)
            params.put("mobile", phoneString)
            params.put("verify_code", verifyString)
            HttpUtils.get(builder.build().toString(), params, object : JsonResponseHandler<LoginResult.UserInfoContent>() {
                override fun success(data:LoginResult.UserInfoContent) {
                    Toast.makeText(applicationContext, "提交成功。", Toast.LENGTH_SHORT).show()
                    AccountManager.getInstance().onUserLoginSuccess(this@BindPhoneActivity, data)
                    finish()
                }
            })
            KeyBoardUtils.hide(this)
        }
    }

    private fun sendPhoneVerifyRequest():Boolean {
        val phoneString = etPhone!!.text.toString()
        if (!CheckLegalUtils.checkPhone(phoneString)) {
            return false
        }
        val builder = Uri.Builder()
        builder.scheme(URLDefine.SCHEME)
        builder.encodedAuthority(URLDefine.HUN_WATER_HOST_API)
        builder.encodedPath(URLDefine.SMS_SEND)

        val params = HashMap<String, String>()
        params.put(URLDefine.UID, AccountManager.getInstance().userId)
        params.put(URLDefine.TOKEN, AccountManager.getInstance().session)
        params.put("mobile", phoneString)
        params.put("type", "3")
        HttpUtils.get(builder.build().toString(), params, object : SimpleJsonResponseHandler() {
            override fun success() {

            }
        })
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriber?.unsubscribe()
    }
}
