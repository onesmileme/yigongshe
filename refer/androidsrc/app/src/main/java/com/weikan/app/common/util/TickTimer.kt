package com.weikan.app.common.util

import android.os.Handler
import android.os.Message
import java.util.*

private val DEFAULT_HANDLER_TYPE = 1

/**
 *
 * @author kailun on 16/9/5.
 */
class TickTimer {

    private val period: Int

    private val delay: Int
    private val handler: Handler
    private val messageType: Int

    private var timer: Timer? = null

    constructor(delay: Int, period: Int, runnable: Runnable) {
        this.delay = delay
        this.period = period
        this.handler = DefaultHandler(runnable)
        this.messageType = DEFAULT_HANDLER_TYPE
    }

    constructor(delay: Int, period: Int, handler: Handler, messageType: Int) {
        this.delay = delay
        this.period = period
        this.handler = handler
        this.messageType = messageType
    }

    fun start() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }

        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                handler.obtainMessage(messageType).sendToTarget()
            }
        }, delay.toLong(), period.toLong())
    }

    fun stop() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }

        handler.removeMessages(messageType)
    }

    private class DefaultHandler(val runnable: Runnable) : Handler() {

        override fun handleMessage(msg: Message) {
            if (msg.what == DEFAULT_HANDLER_TYPE) {
                runnable.run()
            }
        }
    }
}