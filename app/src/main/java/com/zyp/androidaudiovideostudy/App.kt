/*
 * Copyright (C) 2019 NaLong. All Rights Reserved.
 * NaLong group reserve all right of the client.
 * Without authorization, no individual or organization may copy, extract or modify it.
 * If you find any infringement, please contact us.
 */
package com.zyp.androidaudiovideostudy

import android.app.Application
import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * 描述：
 * 作者：@author alex
 * 创建时间：2022/5/11 3:34 下午
 */
class App : Application() {
    open lateinit var screenSize: Size
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        initScreenSize()
    }

    /**
     * 初始化屏幕尺寸
     */
    private fun initScreenSize() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        val displaymetrics = DisplayMetrics()
        windowManager?.defaultDisplay?.getRealMetrics(displaymetrics)
        val width = displaymetrics.heightPixels
        val height = displaymetrics.widthPixels
        screenSize = Size(width, height)
    }

    fun getViewSize(width: Int, height: Int): Size {
        val w = if (width > screenSize.width) {
            return Size(screenSize.width, (screenSize.width * (height / width.toFloat())).toInt())
        } else {
            return Size(width, height)
        }
    }

    companion object {
        lateinit var INSTANCE: App
    }
}

fun app(): App = App.INSTANCE

data class Size(val width: Int, val height: Int)