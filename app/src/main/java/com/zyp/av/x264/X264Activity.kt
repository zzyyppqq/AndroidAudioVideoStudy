package com.zyp.av.x264

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.zyp.av.util.Const.sdPath
import com.zyp.av.base.BaseActivity
import com.zyp.x264lib.X264EncodeLib
import com.zyp.av.annotation.YUVFormat
import com.googlecode.mp4parser.authoring.tracks.h264.H264TrackImpl
import com.googlecode.mp4parser.FileDataSourceImpl
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.zyp.av.R
import com.zyp.av.util.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.util.concurrent.Executors

/**
 * YUV转h264
 */
class X264Activity : BaseActivity() {
    // yuv 和 h264路径
    private var yuvPath = sdPath + File.separator + "test.yuv"
    private var h264Path = sdPath + File.separator + "test.h264"

    // 最终的mp4路径
    private var mp4Path = sdPath + File.separator + "test.mp4"

    // 视频宽高
    private var width = 480
    private var height = 272
    private var mExecuteBtn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_x264)
        findViewById<Button>(R.id.execute_btn).setOnClickListener {
            Executors.newSingleThreadExecutor().execute {
                FileUtils.copyFilesAssets(this, "test.yuv", yuvPath)
                X264EncodeLib().encode(width, height, yuvPath, h264Path, YUVFormat.YUV_420)
                h264ToMp4()
                toast("X264EncodeLib encode complete")
            }
        }
    }

    private fun toast(msg: String) {
        runOnUiThread {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * 将h264转成mp4
     */
    private fun h264ToMp4() {
        try {
            val h264Track = H264TrackImpl(FileDataSourceImpl(h264Path))
            val movie = Movie()
            movie.addTrack(h264Track)
            val mp4file = DefaultMp4Builder().build(movie)
            var fc: FileChannel = FileOutputStream(mp4Path).channel
            mp4file.writeContainer(fc)
            fc.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val REQUEST_CODE = 0x01
    }
}