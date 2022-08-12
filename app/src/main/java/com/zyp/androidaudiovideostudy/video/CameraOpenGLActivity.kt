package com.zyp.androidaudiovideostudy.video

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zyp.androidaudiovideostudy.databinding.ActivityCameraOpenGlactivityBinding
import com.zyp.androidaudiovideostudy.gles.OpenGLHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraOpenGLActivity : AppCompatActivity() {
    private var _binding: ActivityCameraOpenGlactivityBinding? = null
    private val mBinding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCameraOpenGlactivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.glSurfaceView.setEGLContextClientVersion(2)
        mBinding.glSurfaceView.setRenderer(MyRenderer(mBinding.glSurfaceView))

    }
}

class MyRenderer(private val glSurfaceView: GLSurfaceView): GLSurfaceView.Renderer {
    private val openGLHelper = OpenGLHelper()
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        openGLHelper.surfaceCreate(glSurfaceView, OpenGLHelper.FilterType.BLACK_WHITE)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        openGLHelper.surfaceChanged(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        openGLHelper.drawFrame()
    }

}