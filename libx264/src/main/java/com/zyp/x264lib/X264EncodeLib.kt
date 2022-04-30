package com.zyp.x264lib

class X264EncodeLib {

    /**
     * A native method that is implemented by the 'nativelib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    external fun encode(width: Int, height: Int, yuvPath: String, h264Path: String, format: Int)

    companion object {
        // Used to load the 'nativelib' library on application startup.
        init {
            System.loadLibrary("x264-encoder")
        }
    }
}