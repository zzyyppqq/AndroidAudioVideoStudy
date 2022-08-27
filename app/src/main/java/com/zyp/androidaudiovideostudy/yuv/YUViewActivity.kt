package com.zyp.androidaudiovideostudy.yuv

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import com.zyp.androidaudiovideostudy.base.BaseActivity
import androidx.core.app.ActivityCompat
import com.zyp.androidaudiovideostudy.app
import com.zyp.androidaudiovideostudy.databinding.ActivityYuvViewBinding
import com.zyp.androidaudiovideostudy.yuv.opengl.MyGLRender
import com.zyp.androidaudiovideostudy.yuv.opengl.MyGLRender.*
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
class YUViewActivity : BaseActivity() {
    private var _binding: ActivityYuvViewBinding? = null
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
        _binding = ActivityYuvViewBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initData()
        initListener()
    }

    private fun initData() {
        initDataYUV()
        initDataY()
        initRender()
        mBinding.etUrl.setText(ECGPref.yuv_view_url)
        mBinding.etUrlY.setText(ECGPref.y_view_url)
    }

    private fun initListener() {
        mBinding.btnPlayYuv.setOnClickListener {
            playYUV(YUV_TYPE) {
                mRenderer.update(it)
            }
        }

        mBinding.btnPlayGrayYuv.setOnClickListener {
            playYUV(YUV_GRAY_TYPE) {
                mRenderer.update(it)
            }
        }

        mBinding.btnPlayY.setOnClickListener {
            playY {
                mRendererY.update(it, null, null)
            }
        }
        mBinding.btnPlayU.setOnClickListener {
            playU {
                mRendererU.update(null, it, null)
            }
        }
        mBinding.btnPlayV.setOnClickListener {
            playV {
                mRendererV.update(null, null, it)
            }
        }

        mBinding.btnPlayAll.setOnClickListener {
            playY {
                mRendererY.update(it, null, null)
            }
            playU {
                mRendererU.update(null, it, null)
            }
            playV {
                mRendererV.update(null, null, it)
            }
        }

        mBinding.btnStopYuv.setOnClickListener {
            try {
                mThread?.stopRun()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        mBinding.btnSelectYuvFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, 1)
        }

        mBinding.btnSelectYFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, 2)
        }
    }

    private fun initRender() {
        mRenderer = MyGLRender(mBinding.surfaceView)
        mBinding.surfaceView.setEGLContextClientVersion(2);
        mBinding.surfaceView.setRenderer(mRenderer)
        mBinding.surfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY);

        mRendererY = MyGLRender(mBinding.surfaceViewY)
        mBinding.surfaceViewY.setEGLContextClientVersion(2);
        mBinding.surfaceViewY.setRenderer(mRendererY)
        mBinding.surfaceViewY.setRenderMode(RENDERMODE_WHEN_DIRTY);

        mRendererU = MyGLRender(mBinding.surfaceViewU)
        mBinding.surfaceViewU.setEGLContextClientVersion(2);
        mBinding.surfaceViewU.setRenderer(mRendererU)
        mBinding.surfaceViewU.setRenderMode(RENDERMODE_WHEN_DIRTY);

        mRendererV = MyGLRender(mBinding.surfaceViewV)
        mBinding.surfaceViewV.setEGLContextClientVersion(2);
        mBinding.surfaceViewV.setRenderer(mRendererV)
        mBinding.surfaceViewV.setRenderMode(RENDERMODE_WHEN_DIRTY);
    }


    private fun playYUV(type: Int, renderBlock: (ByteArray) -> Unit) {
        try {
            val url = mBinding.etUrl.text.toString()
            val width = mBinding.etWidth.text.toString().toInt()
            val height = mBinding.etHeight.text.toString().toInt()
            val viewSize = app().getViewSize(width, height)
            mBinding.surfaceView.layoutParams = LinearLayout.LayoutParams(viewSize.width, viewSize.height)
            val size = width * height * 3 / 2
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

    private fun playY(renderBlock: (ByteArray) -> Unit) {
        try {
            val url = mBinding.etUrlY.text.toString()
            val width = mBinding.etWidthY.text.toString().toInt()
            val height = mBinding.etHeightY.text.toString().toInt()
            mBinding.surfaceViewY.layoutParams = LinearLayout.LayoutParams(width, height)
            val size = width * height
            val urlPath = url.substring(0, url.length - 5 - width.toString().length - height.toString().length) + "${width}x${height}_y.y"
            mThreadY?.stopRun()
            mThreadY = MyThread(urlPath, size, renderBlock)
            mBinding.surfaceViewY.queueEvent {
                mRendererY.updateProgram(Y_TYPE)
            }
            mRendererY.update(width, height)
            mThreadY?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playU(renderBlock: (ByteArray) -> Unit) {
        try {
            val url = mBinding.etUrlY.text.toString()
            val width = mBinding.etWidthY.text.toString().toInt()
            val height = mBinding.etHeightY.text.toString().toInt()
            mBinding.surfaceViewU.layoutParams = LinearLayout.LayoutParams(width, height)
            val size = width / 2 * height / 2
            val urlPath = url.substring(0, url.length - 5 - width.toString().length - height.toString().length) + "${width / 2}x${height / 2}_u.y"
            mThreadU?.stopRun()
            mThreadU = MyThread(urlPath, size, renderBlock)
            mBinding.surfaceViewU.queueEvent {
                mRendererU.updateProgram(U_TYPE)
            }
            mRendererU.update(width, height)
            mThreadU?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playV(renderBlock: (ByteArray) -> Unit) {
        try {
            val url = mBinding.etUrlY.text.toString()
            val width = mBinding.etWidthY.text.toString().toInt()
            val height = mBinding.etHeightY.text.toString().toInt()
            mBinding.surfaceViewV.layoutParams = LinearLayout.LayoutParams(width, height)
            val size = width / 2 * height / 2
            val urlPath = url.substring(0, url.length - 5 - width.toString().length - height.toString().length) + "${width / 2}x${height / 2}_v.y"
            mThreadV?.stopRun()
            mThreadV = MyThread(urlPath, size, renderBlock)
            mBinding.surfaceViewV.queueEvent {
                mRendererV.updateProgram(V_TYPE)
            }
            mRendererV.update(width, height)
            mThreadV?.start()
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

    private fun initDataYUV() {
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

    private fun initDataY() {
        try {
            val url = ECGPref.y_view_url
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
            mBinding.etWidthY.setText(w.toString())
            mBinding.etHeightY.setText(h.toString())
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
                    initDataYUV()
                }
            } else if (requestCode == 2) {
                val uri = data!!.data
                if (uri != null) {
                    if (ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    val url = uri.path.toString()
                    mBinding.etUrlY.setText(url.toString())
                    ECGPref.y_view_url = url
                    initDataY()
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