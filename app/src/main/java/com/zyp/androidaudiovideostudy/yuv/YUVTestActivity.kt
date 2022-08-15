package com.zyp.androidaudiovideostudy.yuv

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zyp.androidaudiovideostudy.databinding.ActivityYuvTestBinding
import com.zyp.androidaudiovideostudy.util.Const
import com.zyp.androidaudiovideostudy.util.FileUtils
import com.zyp.androidaudiovideostudy.util.LooperThread
import com.zyp.androidaudiovideostudy.yuv.util.BitmapToBmp
import com.zyp.androidaudiovideostudy.yuv.util.RgbToBitmap
import com.zyp.androidaudiovideostudy.yuv.util.YUV420To888
import com.zyp.yuvlib.YuvLib
import java.io.FileInputStream
import java.util.concurrent.Executors


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

        mBinding.btnRgbToBitmapTest.setOnClickListener {
            val rgbUrl = "${Const.sdPath}/sintel_480x272_frame_rgb24.rgb"
            val datas = FileUtils.readBytes(rgbUrl);
            val bitmap = RgbToBitmap.rgb2Bitmap(datas, 480, 272)
            BitmapToBmp.save2Bmp(bitmap)
            mBinding.iv.setImageBitmap(bitmap)
        }

        mBinding.btnRgbaToBitmapTest.setOnClickListener {
            val rgbUrl = "${Const.sdPath}/sintel_480x272_frame_rgba.rgb"
            val datas = FileUtils.readBytes(rgbUrl);
            val bitmap = RgbToBitmap.rgbaToBitmap(datas, 480, 272)
           // val bitmap = RgbToBitmap.bitmapFromRgba(datas, 480, 272)
            mBinding.iv.setImageBitmap(bitmap)
        }

        val yuvLib = YuvLib()
        mBinding.btnGetBitmapTest.setOnClickListener {
            val bitmap = Bitmap.createBitmap(1000, 500, Bitmap.Config.RGB_565)
            yuvLib.getBitmap(bitmap)
            mBinding.iv.setImageBitmap(bitmap)
        }
    }
}