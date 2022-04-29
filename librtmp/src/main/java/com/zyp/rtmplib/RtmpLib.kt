package com.zyp.rtmplib

class RtmpLib {

    /**
     * A native method that is implemented by the 'nativelib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String


    companion object {
        // Used to load the 'nativelib' library on application startup.
        init {
            System.loadLibrary("rtmp-lib")
        }
    }

    external fun rtmp_pusher_open(url: String?, width: Int, height: Int): Int

    external fun rtmp_pusher_close(): Int

    external fun rtmp_pusher_is_connected(): Int

    external fun rtmp_pusher_push_video(bytes: ByteArray?, size: Int, timestamp: Long): Int

    external fun rtmp_pusher_push_audio(bytes: ByteArray?, size: Int, timestamp: Long): Int
}