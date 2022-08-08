package com.zyp.androidaudiovideostudy.yuv

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.zyp.androidaudiovideostudy.R
import com.zyp.androidaudiovideostudy.databinding.ActivityYuvTestBinding
import com.zyp.androidaudiovideostudy.util.LooperThread
import com.zyp.androidaudiovideostudy.yuv.util.YUV420To888
import java.io.FileInputStream
import java.util.concurrent.Executors
import kotlin.concurrent.thread


class YUVTestActivity : AppCompatActivity() {
    private var _binding: ActivityYuvTestBinding? = null
    private val mBinding get() = _binding!!

    private val singeExecutor = Executors.newSingleThreadExecutor()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityYuvTestBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnImagereadTest.setOnClickListener {
            LooperThread.post {
                val bitmap = BitmapFactory.decodeStream(FileInputStream("/sdcard/bdd.jpeg"))
                YUV420To888.createImage(bitmap)
            }
        }
    }
}