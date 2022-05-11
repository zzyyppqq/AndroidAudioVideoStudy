/*
 * Copyright (C) 2019 NaLong. All Rights Reserved.
 * NaLong group reserve all right of the client.
 * Without authorization, no individual or organization may copy, extract or modify it.
 * If you find any infringement, please contact us.
 */
package com.zyp.androidaudiovideostudy

import android.app.Application

/**
 * 描述：
 * 作者：@author alex
 * 创建时间：2022/5/11 3:34 下午
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    companion object {
        lateinit var INSTANCE: App
    }
}

fun app(): App = App.INSTANCE