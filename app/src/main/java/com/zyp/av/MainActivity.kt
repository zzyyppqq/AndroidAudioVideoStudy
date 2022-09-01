package com.zyp.av

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.zyp.av.adapter.MainAdapter
import com.zyp.av.adapter.MainItem
import com.zyp.av.assimp.AssimpActivity
import com.zyp.av.audio.AudioActivity
import com.zyp.av.audio.LiblameActivity
import com.zyp.av.audio.LibmadActivity
import com.zyp.av.base.BaseActivity
import com.zyp.av.camera.*
import com.zyp.av.databinding.ActivityMainBinding
import com.zyp.av.fdkaac.FdkAACActivity
import com.zyp.av.ffmpeg.FFmpegActivity
import com.zyp.av.fmod.EffectActivity
import com.zyp.av.fmod.FmodActivity
import com.zyp.av.gpuimage.GpuImageActivity
import com.zyp.av.opengl.CubeActivity
import com.zyp.av.opengl.HockeyActivity
import com.zyp.av.opengl.OpenGLRendererTestActivity
import com.zyp.av.opengl.PanoramaActivity
import com.zyp.av.rtmp.CameraMediaCodecRtmpActivity
import com.zyp.av.video.*
import com.zyp.av.x264.X264Activity
import com.zyp.av.yuv.RGBViewActivity
import com.zyp.av.yuv.YUVTestActivity
import com.zyp.av.yuv.YUViewActivity
import com.zyp.av.yuv.YuvCameraActivity

class MainActivity : BaseActivity() {

    private var _binding: ActivityMainBinding? = null
    private val mBinding get() = _binding!!

    private var mAdapter: MainAdapter? = null

    private val datas = listOf<MainItem>(
        MainItem(AudioActivity::class.java),
        MainItem(LiblameActivity::class.java),
        MainItem(LibmadActivity::class.java),
        MainItem(MediaCodecActivity::class.java),
        MainItem(CameraActivity::class.java),
        MainItem(Camera2Activity::class.java),
        MainItem(CameraTextureActivity::class.java),
        MainItem(Camera2TextureActivity::class.java),
        MainItem(CameraOpenGLActivity::class.java),
        MainItem(CameraOpenGLFilterActivity::class.java),
        MainItem(CameraOpenGLLookupActivity::class.java),
        MainItem(CameraMediaCodecActivity::class.java),
        MainItem(OpenGLRendererTestActivity::class.java),
        MainItem(FFmpegActivity::class.java),
        MainItem(FmodActivity::class.java),
        MainItem(EffectActivity::class.java),
        MainItem(CubeActivity::class.java),
        MainItem(HockeyActivity::class.java),
        MainItem(PanoramaActivity::class.java),
        MainItem(AssimpActivity::class.java),
        MainItem(MediaPlayerActivity::class.java),
        MainItem(VideoViewActivity::class.java),
        MainItem(MediaCodecVideoActivity::class.java),
        MainItem(CameraMediaCodecRtmpActivity::class.java),
        MainItem(X264Activity::class.java),
        MainItem(FdkAACActivity::class.java),
        MainItem(ANativeWindowActivity::class.java),
        MainItem(RgbaPlayerActivity::class.java),
        MainItem(YuvCameraActivity::class.java),
        MainItem(YUViewActivity::class.java),
        MainItem(RGBViewActivity::class.java),
        MainItem(YUVTestActivity::class.java),
        MainItem(GpuImageActivity::class.java),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.recyclerView.let {
            mAdapter = MainAdapter(datas)
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = mAdapter
            mAdapter?.setOnRecyclerViewItemClickListener(object :AbstractAdapter.OnRecyclerItemClickListener<MainItem> {
                override fun onItemClick(itemView: View, position: Int, data: MainItem) {
                    itemClick(position, data)
                }
            })
        }
    }

    private fun itemClick(position: Int, data: MainItem) {
        startActivity(Intent(this, data.clazz))
    }


    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}

