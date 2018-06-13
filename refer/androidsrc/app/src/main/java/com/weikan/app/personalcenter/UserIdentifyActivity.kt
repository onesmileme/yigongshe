package com.weikan.app.personalcenter

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import com.jakewharton.rxbinding.view.RxView
import com.squareup.picasso.Picasso
import com.weikan.app.R
import com.weikan.app.account.AccountManager
import com.weikan.app.base.BaseActivity
import com.weikan.app.common.widget.SimpleNavigationView
import com.weikan.app.original.OriginalAgent
import com.weikan.app.original.bean.UploadImageObject
import com.weikan.app.util.LToast
import platform.http.responsehandler.JsonResponseHandler
import platform.http.responsehandler.SimpleJsonResponseHandler
import platform.http.result.FailedResult
import platform.photo.util.PhotoUtils
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * 身份认证
 *
 * @author kailun on 16/11/27.
 */
class UserIdentifyActivity : BaseActivity() {

    private lateinit var navigation: SimpleNavigationView
    private lateinit var etRealName: EditText
    private lateinit var tvIdentify: TextView
    private lateinit var etCompany: TextView
    private lateinit var etDuty: EditText
    private lateinit var tvCity: TextView
    private lateinit var rlUploadCard: RelativeLayout
    private lateinit var ivThumb: ImageView
    private lateinit var tvUploadCard: TextView
    private lateinit var btnConfirm: Button

    var currentPhoto: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_identify)
        assignViews()
        initViews()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ModifyCityActivity.processActivityResult(requestCode, resultCode, data, {
            tvCity.text = it
            return@processActivityResult
        })

        PhotoUtils.processActivityResult(requestCode, resultCode, data, { mutableList ->
            currentPhoto = mutableList.firstOrNull() ?: ""
            Picasso.with(this@UserIdentifyActivity)
                    .load(File(currentPhoto))
                    .into(ivThumb)
            return@processActivityResult
        })
    }

    private fun assignViews() {
        navigation = findViewById(R.id.navigation) as SimpleNavigationView
        etRealName = findViewById(R.id.et_real_name) as EditText
        tvIdentify = findViewById(R.id.tv_identify) as TextView
        etCompany = findViewById(R.id.et_company) as TextView
        etDuty = findViewById(R.id.et_duty) as EditText
        tvCity = findViewById(R.id.tv_city) as TextView
        rlUploadCard = findViewById(R.id.rl_upload_card) as RelativeLayout
        ivThumb = findViewById(R.id.iv_thumb) as ImageView
        tvUploadCard = findViewById(R.id.tv_upload_card) as TextView
        btnConfirm = findViewById(R.id.btn_confirm) as Button
    }

    private fun initViews() {
        navigation.setLeftOnClickListener { onBackPressed() }

        RxView.clicks(tvIdentify)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe { onChooseIdentify() }

        RxView.clicks(tvCity)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe { onChooseCity() }

        RxView.clicks(rlUploadCard)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe { PhotoUtils.gotoPending(this, 1, false) }

        RxView.clicks(btnConfirm)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe { onConfirm() }
    }

    private fun onChooseIdentify() {
        val items = arrayOf("P2P从业人员", "记者", "自媒体工作者")
        @Suppress("DEPRECATION")
        AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("选择认证身份")
                .setItems(items, { dialogInterface, i ->
                    tvIdentify.text = items[i]
                })
                .show()
    }

    private fun onChooseCity() {
        ModifyCityActivity.newActivityForResult(this, tvCity.text.toString())
    }

    private fun onConfirm() {
        // http://123.57.30.63:8041/user_verify/submit
        val uid = AccountManager.getInstance().userId

        // real_name    : 真实姓名
        // identify     : 认证身份
        // company      : 所在公司
        // title        : 职位
        // city         : 市
        // uid          : 用户id
        // pics         : 图片信息        json格式
        val realName = etRealName.text.toString()
        if (realName == "") {
            LToast.showToast("真实姓名未填写")
            return
        }

        val identify = tvIdentify.text.toString()
        if (identify == "") {
            LToast.showToast("认证身份未填写")
            return
        }

        val company = etCompany.text.toString()
        if (company == "") {
            LToast.showToast("所在公司未填写")
            return
        }

        val duty = etDuty.text.toString()
        if (duty == "") {
            LToast.showToast("职位未填写")
            return
        }

        val city = tvCity.text.toString()
        if (city == "") {
            LToast.showToast("所在城市未填写")
            return
        }

        if (currentPhoto == "") {
            LToast.showToast("名片或者工作证未选择")
            return
        }

        OriginalAgent.uploadImage(File(currentPhoto), object: JsonResponseHandler<UploadImageObject>() {
            override fun success(data: UploadImageObject) {
                postIdentifyInfo(uid, realName, identify, company, duty, city, data)
            }

            override fun begin() {
                showLoadingDialog()
            }

            override fun failed(r: FailedResult?) {
                super.failed(r)
                hideLoadingDialog()
            }
        })
    }

    private fun postIdentifyInfo(uid: String, realName: String, identify: String,
                                 company: String, duty: String, city: String,
                                 uploadImageObject: UploadImageObject) {
        PersonalAgent.postUserVerify(uid, realName, identify, company, duty, city, uploadImageObject,
                object: SimpleJsonResponseHandler() {
                    override fun success() {
                        LToast.showToast("提交成功")

                        // 修改用户的身份验证状态为“待认证”
                        val userData = AccountManager.getInstance().userData
                        userData.verifyStatus = 1
                        AccountManager.getInstance().userData = userData
                        finish()
                    }

                    override fun end() {
                        hideLoadingDialog()
                    }
                })
    }

}