package com.zyp.ffmpeglib

import android.view.Surface

class ANativeWindowRender {

    private var mSurface: Surface? = null

    init {
        System.loadLibrary("ffmpeglib")
    }

    fun setSurface(surface: Surface?) {
        mSurface = surface
    }

    fun start(file: String, w: Int, h: Int, fps: Int) {
        native_start(mSurface, file, w, h, fps)
    }

    fun stop() {
        native_stop()
    }

    private external fun native_start(surface: Surface?, file: String, w: Int, h: Int, fps: Int)

    private external fun native_stop()

}