package com.weikan.app.news.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

/**
 * 一个简单的Timer
 *
 * @author kailun on 15/12/10.
 */
public class SimpleTimer {
    @NonNull
    Class eventClass;
    Timer timer;
    long delay;
    long period;
    volatile boolean isRunning = false;

    public SimpleTimer(@NonNull Class<?> eventClass, long delay, long period) {
        this.eventClass = eventClass;
        this.delay = delay;
        this.period = period;
    }

    public void start() {
        synchronized (this) {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }

            isRunning = true;
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    tick();
                }
            };
            timer.schedule(task, delay, period);
        }
    }

    public void stop() {
        synchronized (this) {
            isRunning = false;

            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    @SuppressWarnings("TryWithIdenticalCatches")
    private void tick() {
        Log.e(SimpleTimer.class.getSimpleName(), "tick tick tick");

        // 如果Timer已经取消了，那么什么都不做
        if (!isRunning) {
            return;
        }

        Object obj;
        try {
            obj = eventClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("" + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("" + e.getMessage());
        }
        EventBus.getDefault().post(obj);
    }
}