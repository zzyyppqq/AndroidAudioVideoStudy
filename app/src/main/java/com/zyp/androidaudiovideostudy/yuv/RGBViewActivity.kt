package com.zyp.androidaudiovideostudy.yuv

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.zyp.androidaudiovideostudy.app
import com.zyp.androidaudiovideostudy.databinding.ActivityRgbViewBinding
import com.zyp.androidaudiovideostudy.opengl.MyGLRender
import com.zyp.androidaudiovideostudy.opengl.MyGLRender.*
import com.zyp.androidaudiovideostudy.pref.ECGPref
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

/**
 * https://blog.csdn.net/leixiaohua1020/article/details/50534150
 * https://blog.csdn.net/u012459903/article/details/118224506
 * ffmpeg -i test_video.mp4 -pix_fmt rgba -s 640x360 test_video.rgb
 * ffmpeg -i dongfengpo_352x240.mp4 -t 5 -s 320x240 -pix_fmt rgba dongfengpo_320x240_rgba.rgb
 */
class RGBViewActivity : AppCompatActivity() {
    private var _binding: ActivityRgbViewBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var mRenderer: MyGLRender
    private lateinit var mRendererY: MyGLRender
    private lateinit var mRendererU: MyGLRender
    private lateinit var mRendererV: MyGLRender
    private var mThread: MyThread? = null
    private var mThreadY: MyThread? = null
    private var mThreadU: MyThread? = null
    private var mThreadV: MyThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRgbViewBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initData()
        initListener()
    }

    private fun initData() {
        initRGB()
        initRender()
        mBinding.etUrl.setText(ECGPref.rgb_view_url)
    }

    private fun initListener() {
        mBinding.btnPlayRgb.setOnClickListener {
            playRGB(RGB_TYPE) {
                mRenderer.updateRGB(it)
            }
        }

        mBinding.btnPlayRgba.setOnClickListener {
            playRGB(RGBA_TYPE) {
                mRenderer.updateRGBA(it)
            }
        }

        mBinding.btnStopRgb.setOnClickListener {
            try {
                mThread?.stopRun()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        mBinding.btnSelectRgbFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, 1)
        }
    }

    private fun initRender() {
        mRenderer = MyGLRender(mBinding.surfaceView)
        mBinding.surfaceView.setEGLContextClientVersion(2);
        mBinding.surfaceView.setRenderer(mRenderer)
        mBinding.surfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY);
    }


    private fun playRGB(type: Int, renderBlock: (ByteArray) -> Unit) {
        try {
            val url = mBinding.etUrl.text.toString()
            val width = mBinding.etWidth.text.toString().toInt()
            val height = mBinding.etHeight.text.toString().toInt()
            val viewSize = app().getViewSize(width, height)
            mBinding.surfaceView.layoutParams = LinearLayout.LayoutParams(viewSize.width, viewSize.height)
            val size = if (type == RGB_TYPE ) {
                width * height * 3
            } else {
                width * height * 4
            }
            mThread?.stopRun()
            mThread = MyThread(url, size, renderBlock)
            mBinding.surfaceView.queueEvent {
                mRenderer.updateProgram(type)
            }
            mRenderer.update(width, height)
            mThread?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    class MyThread(val url: String, val size: Int, val renderBlock: (ByteArray) -> Unit) :
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
                renderBlock(input)
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

    private fun initRGB() {
        try {
            val url = ECGPref.rgb_view_url
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
                    ECGPref.rgb_view_url = url
                    initRGB()
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