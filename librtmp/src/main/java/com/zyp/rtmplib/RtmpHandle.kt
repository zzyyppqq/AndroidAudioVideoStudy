package com.zyp.rtmplib

object RtmpHandle {

    init {
        System.loadLibrary("rtmp-lib")
    }

    external fun pushFile(path: String?)

    external fun connect(url: String?): Int

    external fun push(buf: ByteArray?, length: Int): Int

    external fun close(): Int
}