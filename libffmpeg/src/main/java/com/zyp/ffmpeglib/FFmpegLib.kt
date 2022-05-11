package com.zyp.ffmpeglib

import android.view.Surface

class FFmpegLib {

    /**
     * A native method that is implemented by the 'ffmpeglib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    external fun urlProtocolInfo(): String

    external fun avformatInfo(): String

    external fun avcodecInfo(): String

    external fun avcodecConfiguration(): String

    external fun avfilterInfo(): String

    external fun render(url: String, surface: Surface)

    /**
     * 解码 mp4->yuv
     */
    external fun decode(inputPath: String, outputPath: String): Int

    /**
     * 推流器
     */
    external fun stream(inputUrl: String, outputUrl: String): Int

    /**
     * 转码器
     */
    external fun ffmpegcore(cmdnum: Int, cmdline: Array<String>): Int

    companion object {
        // Used to load the 'ffmpeglib' library on application startup.
        init {
            System.loadLibrary("ffmpeglib")
        }
    }
}