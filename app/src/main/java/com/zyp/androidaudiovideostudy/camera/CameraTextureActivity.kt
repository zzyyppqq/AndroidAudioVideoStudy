package com.zyp.androidaudiovideostudy.camera

import android.content.pm.ActivityInfo
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Bundle
import android.view.TextureView
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.zyp.androidaudiovideostudy.databinding.ActivityCameraTextureActivityBinding
import com.zyp.androidaudiovideostudy.camera.helper.CameraHelper

/**
 * CameraTextureActivity
 */
class CameraTextureActivity : AppCompatActivity() {
    private var _binding: ActivityCameraTextureActivityBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var mCameraHelper: CameraHelper
    private var isMirror = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCameraTextureActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        val rotation = windowManager.getDefaultDisplay().getRotation();
        mCameraHelper =
            CameraHelper(
                Camera.CameraInfo.CAMERA_FACING_BACK,
                rotation
            )

        if (isMirror) {
            mBinding.textureView.setScaleX(-1.0f);
        }

        mBinding.textureView.setSurfaceTextureListener(mSurfaceTextureListener)
    }


    private val mSurfaceTextureListener = object :TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(
            surfaceTexture: SurfaceTexture,
            width: Int,
            height: Int
        ) {
            mCameraHelper.startPreview(surfaceTexture, width, height)
        }

        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture,
            width: Int,
            height: Int
        ) {

        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            return true
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

        }

    }
}