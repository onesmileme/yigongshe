package com.weikan.app.personalcenter.adapter

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.weikan.app.R
import com.weikan.app.account.AccountManager
import com.weikan.app.face.FaceConversionUtil
import com.weikan.app.personalcenter.UserHomeActivity
import com.weikan.app.personalcenter.bean.TalkObject
import com.weikan.app.util.DateUtils
import com.weikan.app.util.ImageLoaderUtil
import com.weikan.app.widget.roundedimageview.CircleImageView
import java.util.*

/**
 * Created by liujian on 16/11/13.
 */
class TalkAdapter(private val context: Context, private val data: List<TalkObject.TalkContent>?) : BaseAdapter() {
    private val inflater: LayoutInflater
    private val TIME_SPLIT_SPACE: Long = 60 // 显示时间的间隔，60秒

    val last_read_mid: String? = null

    init {
        this.inflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return if (data != null) data.size else 0
    }

    override fun getItem(i: Int): Any? {
        return if (data != null) data[i] else null
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, convertView: View?, viewGroup: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder
        if (convertView == null) {
            holder = ViewHolder()
            convertView = inflater.inflate(R.layout.talk_item, null)


            holder.time_line = convertView.findViewById(R.id.time_line) as TextView
            holder.photo_left = convertView.findViewById(R.id.iv_talk_item_header_left) as ImageView
            holder.text_left = convertView.findViewById(R.id.tv_talk_item_text_left) as TextView
            //                holder.text_left.setMovementMethod(LinkMovementMethod.getInstance());
            holder.photo_right = convertView.findViewById(R.id.iv_talk_item_header_right) as ImageView
            holder.text_right = convertView.findViewById(R.id.tv_talk_item_text_right) as TextView
            //                holder.text_right.setMovementMethod(LinkMovementMethod.getInstance());
            holder.right_layout = convertView.findViewById(R.id.rl_talk_item_right)
            holder.left_layout = convertView.findViewById(R.id.rl_talk_item_left)

            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        val talkContent = getItem(i) as TalkObject.TalkContent?

        if (talkContent != null) {

            if (!TextUtils.isEmpty(talkContent.from_uid) && talkContent.from_uid == AccountManager.getInstance().userId) {
                //是我发出的聊天
                holder.left_layout!!.visibility = View.GONE
                holder.right_layout!!.visibility = View.VISIBLE

                ImageLoaderUtil.updateImage(holder.photo_right, AccountManager.getInstance().userData?.headimgurl)
                holder.text_right!!.text = FaceConversionUtil.getInstace().getExpressionString(context, talkContent.content)

            } else {
                //是他发给我的聊天
                holder.left_layout!!.visibility = View.VISIBLE
                holder.right_layout!!.visibility = View.GONE

                ImageLoaderUtil.updateImage(holder.photo_left, talkContent.headimgurl)
                holder.text_left!!.text = FaceConversionUtil.getInstace().getExpressionString(context, talkContent.content)
            }
            val listener = View.OnClickListener { context.startActivity(UserHomeActivity.makeIntent(context, talkContent.from_uid!!)) }
            holder.photo_right!!.setOnClickListener(listener)
            holder.photo_left!!.setOnClickListener(listener)

            val lastCreateTime:Long
            if(i==0){
                lastCreateTime = 0
            } else {
                val lastContent =  getItem(i-1) as TalkObject.TalkContent
                lastCreateTime = lastContent.ctime
            }

            val lastspace = talkContent.ctime - lastCreateTime
            val curspace = System.currentTimeMillis()/1000 - talkContent.ctime
            if ( lastspace > TIME_SPLIT_SPACE) {
                holder.time_line!!.visibility = View.VISIBLE
                if(curspace> 24 * 60 * 60){
                    holder.time_line!!.text = DateUtils.formatDate(Date(talkContent.ctime * 1000), "MM-dd HH:mm")
                } else {
                    holder.time_line!!.text = DateUtils.formatDate(Date(talkContent.ctime * 1000), "HH:mm")
                }

            } else {
                holder.time_line!!.visibility = View.GONE
            }

        }
        return convertView!!
    }

    inner class ViewHolder {
        internal var time_line: TextView? = null

        internal var left_layout: View? = null
        internal var photo_left: ImageView? = null
        internal var text_left: TextView? = null

        internal var right_layout: View? = null
        internal var photo_right: ImageView? = null
        internal var text_right: TextView? = null
    }

}