package com.zyp.androidaudiovideostudy.video

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zyp.androidaudiovideostudy.databinding.ActivityCameraGlFilterActivityBinding
import com.zyp.androidaudiovideostudy.databinding.ActivityCameraOpenGlactivityBinding
import com.zyp.androidaudiovideostudy.gles.CameraOpenGLRenderer
import com.zyp.androidaudiovideostudy.util.Const
import com.zyp.androidaudiovideostudy.util.ToastUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * OpenGL显示相机数据, 并进行滤波、保存相机数据
 */
class CameraGLFilterActivity : AppCompatActivity() {
    private var _binding: ActivityCameraGlFilterActivityBinding? = null
    private val mBinding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCameraGlFilterActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

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