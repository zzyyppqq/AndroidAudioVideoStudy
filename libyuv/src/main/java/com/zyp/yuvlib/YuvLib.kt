package com.zyp.yuvlib

import android.graphics.Bitmap

class YuvLib {

    /**
     * A native method that is implemented by the 'nativelib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String


    external fun i420ToRGBA(src: ByteArray?, dst: ByteArray?, width: Int, height: Int)

    external fun i420ToNv21(src: ByteArray?, dst: ByteArray?, width: Int, height: Int)

    external fun nv21ToI420(src: ByteArray?, dst: ByteArray?, width: Int, height: Int)

    external fun rgb24ToI420(src: ByteArray?, dst: ByteArray?, width: Int, height: Int)

    external fun rotateI420(src: ByteArray?, dst: ByteArray?, width: Int, height: Int, degree: Int)

    external fun bitmapToI420(src: ByteArray?, bitmap: Bitmap, width: Int, height: Int)

    external fun argbToI420(src: ByteArray?, width: Int, height: Int, size: Int)

    companion object {
        // Used to load the 'nativelib' library on application startup.
        init {
            System.loadLibrary("yuv-lib")
        }
    }
}