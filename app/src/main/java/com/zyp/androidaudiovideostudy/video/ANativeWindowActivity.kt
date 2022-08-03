package com.zyp.androidaudiovideostudy.video

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.zyp.androidaudiovideostudy.databinding.ActivitySurfaceViewBinding
import com.zyp.androidaudiovideostudy.pref.ECGPref
import com.zyp.androidaudiovideostudy.util.Const
import com.zyp.ffmpeglib.ANativeWindowRender
import java.io.File

/**
 * https://blog.csdn.net/u012459903/article/details/118224506
 * ffmpeg -i test_video.mp4 -pix_fmt rgba -s 640x360 test_video.rgb
 * ffmpeg -i dongfengpo_352x240.mp4 -t 5 -s 320x240 -pix_fmt rgba dongfengpo_320x240_rgba.rgb
 */
class ANativeWindowActivity : AppCompatActivity() {
    private var _binding: ActivitySurfaceViewBinding? = null
    private val mBinding get() = _binding!!
    private val renderer = ANativeWindowRender()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySurfaceViewBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initData()


        mBinding.etUrl.setText(ECGPref.native_window_agba_url)
        renderer.setSurface(mBinding.surfaceView.holder.surface)
        mBinding.btnPlay.setOnClickListener {
            try {
                val url = mBinding.etUrl.text.toString()
                val width = mBinding.etWidth.text.toString().toInt()
                val height = mBinding.etHeight.text.toString().toInt()
                val frameRate = mBinding.etFramerate.text.toString().toInt()
                mBinding.surfaceView.layoutParams = LinearLayout.LayoutParams(width, height)
                renderer.start(url, width, height, frameRate)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        mBinding.btnStop.setOnClickListener {
            try {
                renderer.stop()
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

    private fun initData() {
        try {
            val url = ECGPref.native_window_agba_url
            if (url.isBlank()) {
                return
            }
            val file = File(url)
            val splits = file.name.removeSuffix(".rgb").split("_")
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
                    ECGPref.native_window_agba_url = url
                    initData()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            renderer.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}