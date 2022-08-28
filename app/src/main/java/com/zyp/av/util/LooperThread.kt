package com.zyp.av.util

import android.os.Handler
import android.os.HandlerThread

object LooperThread {

    private val mHandlerThread: HandlerThread
    private var mHandler: Handler

    init {
        mHandlerThread = HandlerThread("LooperThread")
        mHandlerThread.start()
        mHandler = Handler(mHandlerThread.looper)
    }

    fun post(runnable: Runnable) {
        mHandler.post(runnable)
    }
}