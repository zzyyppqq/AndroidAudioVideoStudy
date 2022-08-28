package com.zyp.androidaudiovideostudy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.BaseAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.zyp.androidaudiovideostudy.adapter.MainAdapter
import com.zyp.androidaudiovideostudy.adapter.MainItem
import com.zyp.androidaudiovideostudy.assimp.AssimpActivity
import com.zyp.androidaudiovideostudy.audio.AudioActivity
import com.zyp.androidaudiovideostudy.audio.LiblameActivity
import com.zyp.androidaudiovideostudy.audio.LibmadActivity
import com.zyp.androidaudiovideostudy.base.BaseActivity
import com.zyp.androidaudiovideostudy.camera.*
import com.zyp.androidaudiovideostudy.databinding.ActivityMainBinding
import com.zyp.androidaudiovideostudy.fdkaac.FdkAACActivity
import com.zyp.androidaudiovideostudy.ffmpeg.FFmpDecoderActivity
import com.zyp.androidaudiovideostudy.ffmpeg.FFmpegActivity
import com.zyp.androidaudiovideostudy.ffmpeg.NativeAVEncodeActivity
import com.zyp.androidaudiovideostudy.fmod.EffectActivity
import com.zyp.androidaudiovideostudy.fmod.FmodActivity
import com.zyp.androidaudiovideostudy.gpuimage.GpuImageActivity
import com.zyp.androidaudiovideostudy.opengl.CubeActivity
import com.zyp.androidaudiovideostudy.opengl.HockeyActivity
import com.zyp.androidaudiovideostudy.opengl.OpenGLRendererTestActivity
import com.zyp.androidaudiovideostudy.opengl.PanoramaActivity
import com.zyp.androidaudiovideostudy.rtmp.CameraMediaCodecRtmpActivity
import com.zyp.androidaudiovideostudy.video.*
import com.zyp.androidaudiovideostudy.x264.X264Activity
import com.zyp.androidaudiovideostudy.yuv.RGBViewActivity
import com.zyp.androidaudiovideostudy.yuv.YUVTestActivity
import com.zyp.androidaudiovideostudy.yuv.YUViewActivity
import com.zyp.androidaudiovideostudy.yuv.YuvCameraActivity

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
        MainItem(FFmpDecoderActivity::class.java),
        MainItem(NativeAVEncodeActivity::class.java),
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

