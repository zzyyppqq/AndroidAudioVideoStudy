package com.zyp.androidaudiovideostudy.video

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.zyp.androidaudiovideostudy.databinding.ActivityCameraOpenglFilterActivityBinding
import com.zyp.androidaudiovideostudy.util.Const
import com.zyp.androidaudiovideostudy.util.ToastUtil

/**
 * OpenGL显示相机数据, 并进行滤波、保存相机数据
 */
class CameraOpenGLFilterActivity : AppCompatActivity() {
    private var _binding: ActivityCameraOpenglFilterActivityBinding? = null
    private val mBinding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCameraOpenglFilterActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        val rotation = windowManager.getDefaultDisplay().getRotation();
        mBinding.cameraSurfaceView.init(rotation)

        mBinding.btStartFilterRecord.setOnClickListener {
            mBinding.cameraSurfaceView.startRecord("${Const.sdPath}/${System.currentTimeMillis()}.mp4", 1.0f)
            ToastUtil.show("start record")
        }

        mBinding.btStopFilterRecord.setOnClickListener {
            mBinding.cameraSurfaceView.stopRecord()
            ToastUtil.show("stop record")
        }
    }
}