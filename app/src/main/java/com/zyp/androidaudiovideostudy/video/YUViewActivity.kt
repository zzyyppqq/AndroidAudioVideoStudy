package com.zyp.androidaudiovideostudy.video

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.zyp.androidaudiovideostudy.databinding.ActivityGlsurfaceviewBinding
import com.zyp.androidaudiovideostudy.opengl.MyGLRender
import com.zyp.androidaudiovideostudy.pref.ECGPref
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

/**
 * https://blog.csdn.net/u012459903/article/details/118224506
 * ffmpeg -i test_video.mp4 -pix_fmt rgba -s 640x360 test_video.rgb
 * ffmpeg -i dongfengpo_352x240.mp4 -t 5 -s 320x240 -pix_fmt rgba dongfengpo_320x240_rgba.rgb
 */
class YUViewActivity : AppCompatActivity() {
    private var _binding: ActivityGlsurfaceviewBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var mRenderer: MyGLRender
    private var mThread: MyThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityGlsurfaceviewBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initData()

        mRenderer = MyGLRender(mBinding.surfaceView)
        mBinding.surfaceView.setEGLContextClientVersion(2);
        mBinding.surfaceView.setRenderer(mRenderer)
        mBinding.surfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY);

        mBinding.etUrl.setText(ECGPref.yuv_view_url)

        mBinding.btnPlay.setOnClickListener {
            try {
                val url = mBinding.etUrl.text.toString()
                val width = mBinding.etWidth.text.toString().toInt()
                val height = mBinding.etHeight.text.toString().toInt()
                val frameRate = mBinding.etFramerate.text.toString().toInt()
                mBinding.surfaceView.layoutParams = LinearLayout.LayoutParams(width, height)
                if (mThread == null) {
                    mThread = MyThread(url, mRenderer, width, height)
                }
                mRenderer.update(width, height)
                mThread?.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        mBinding.btnStop.setOnClickListener {
            try {
                mThread?.stopRun()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        mBinding.btnSelectFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, 1)
        }
    }

    class MyThread(val url: String, val render: MyGLRender, val width: Int, val height: Int) :
        Thread() {
        var isStop = false
        override fun run() {
            super.run()

            val yuvFile = File(url)
            var fis: FileInputStream? = null
            try {
                fis = FileInputStream(yuvFile)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            val size: Int = width * height * 3 / 2
            val input = ByteArray(size)
            var hasRead = 0
            while (!isStop) {
                try {
                    hasRead = fis!!.read(input)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (hasRead == -1) {
                    break
                }
                render.update(input)
                Log.i("thread", "thread is executing hasRead: $hasRead")
                try {
                    sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }

        fun stopRun() {
            isStop = true
        }
    }

    private fun initData() {
        try {
            val url = ECGPref.yuv_view_url
            if (url.isBlank()) {
                return
            }
            val file = File(url)
            val splits = file.name.let { it.substring(0, it.lastIndexOf(".")) }.split("_")
            val wh = splits.filter {
                it.contains("x")
            }[0].split("x")
            val w = wh[0]
            val h = wh[1]
            mBinding.etWidth.setText(w.toString())
            mBinding.etHeight.setText(h.toString())
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                val uri = data!!.data
                if (uri != null) {
                    if (ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    val url = uri.path.toString()
                    mBinding.etUrl.setText(url.toString())
                    ECGPref.yuv_view_url = url
                    initData()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mThread?.stopRun()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}