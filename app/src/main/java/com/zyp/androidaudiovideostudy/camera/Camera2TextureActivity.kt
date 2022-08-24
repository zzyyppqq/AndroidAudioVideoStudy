package com.zyp.androidaudiovideostudy.camera

import android.content.pm.ActivityInfo
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.TextureView
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.zyp.androidaudiovideostudy.camera.helper.Camera2Helper
import com.zyp.androidaudiovideostudy.databinding.ActivityCamera2TextureActivityBinding

/**
 * Camera2TextureActivity
 * https://juejin.cn/post/6844903534610087943
 * https://juejin.cn/post/6844903966556291079
 */
class Camera2TextureActivity : AppCompatActivity() {
    private var _binding: ActivityCamera2TextureActivityBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var mCamera2Helper: Camera2Helper
    private var isMirror = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCamera2TextureActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        val rotation = windowManager.getDefaultDisplay().getRotation();
        mCamera2Helper = Camera2Helper(this, Camera2Helper.CAMERA_ID_BACK, rotation)

        if (isMirror) {
            mBinding.textureView.setScaleX(-1.0f);
        }


        mBinding.textureView.setSurfaceTextureListener(mSurfaceTextureListener)
    }

    public fun startCamera() {
        if (mCamera2Helper.cameraDevice != null) {
            return
        }
        // 当屏幕关闭并重新打开时，SurfaceTexture 已经可用，
        // 并且不会调用“onSurfaceTextureAvailable”。在这种情况下，我们可以打开
        // 一个相机并从这里开始预览（否则，我们等到表面准备好SurfaceTextureListener）。
        if (mBinding.textureView.isAvailable()) {
            mCamera2Helper.openCamera(mBinding.textureView.surfaceTexture)
        } else {
            mBinding.textureView.setSurfaceTextureListener(mSurfaceTextureListener)
        }
    }

    override fun onResume() {
        super.onResume()
        if (mCamera2Helper != null) {
            startCamera()
        }
    }

    override fun onPause() {
        if (mCamera2Helper != null) {
            mCamera2Helper.stop()
        }
        super.onPause()
    }



    private val mSurfaceTextureListener = object :TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(
            surfaceTexture: SurfaceTexture,
            width: Int,
            height: Int
        ) {
            mCamera2Helper.openCamera(surfaceTexture)
            val matrix = mCamera2Helper.getTransform(width, height)
            mBinding.textureView.setTransform(matrix)
        }

        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture,
            width: Int,
            height: Int
        ) {
            val matrix = mCamera2Helper.getTransform(width, height)
            mBinding.textureView.setTransform(matrix)
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            return true
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

        }

    }
}