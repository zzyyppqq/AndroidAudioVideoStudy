package com.zyp.androidaudiovideostudy.camera

import android.opengl.GLSurfaceView
import android.os.Bundle
import com.zyp.androidaudiovideostudy.base.BaseActivity
import com.zyp.androidaudiovideostudy.databinding.ActivityCameraOpenglActivityBinding
import com.zyp.androidaudiovideostudy.gles.OpenGLHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 黑白滤镜切换
 */
class CameraOpenGLActivity : BaseActivity() {
    private var _binding: ActivityCameraOpenglActivityBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var mRenderer: MyRenderer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCameraOpenglActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val rotation = windowManager.getDefaultDisplay().getRotation();
        mRenderer = MyRenderer(mBinding.glSurfaceView, rotation)
        mBinding.glSurfaceView.setEGLContextClientVersion(2)
        mBinding.glSurfaceView.setRenderer(mRenderer)

        initListener()
    }

    private fun initListener() {
        mBinding.btFilterNone.setOnClickListener {
            mRenderer.updateProgram(OpenGLHelper.FilterType.NONE)
        }

        mBinding.btFilterBlackWhite.setOnClickListener {
            mRenderer.updateProgram(OpenGLHelper.FilterType.BLACK_WHITE)
        }
    }
}

class MyRenderer(private val glSurfaceView: GLSurfaceView, private val rotation: Int): GLSurfaceView.Renderer {
    private val openGLHelper = OpenGLHelper()
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        openGLHelper.surfaceCreate(glSurfaceView, rotation)
        openGLHelper.createProgram(OpenGLHelper.FilterType.NONE)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        openGLHelper.surfaceChanged(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        openGLHelper.drawFrame()
    }

    fun updateProgram(filterType: OpenGLHelper.FilterType) {
        glSurfaceView.queueEvent {
            openGLHelper.createProgram(filterType)
        }
    }
}