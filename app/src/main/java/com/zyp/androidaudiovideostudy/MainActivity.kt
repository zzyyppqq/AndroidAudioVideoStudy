package com.zyp.androidaudiovideostudy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.zyp.androidaudiovideostudy.adapter.MainAdapter
import com.zyp.androidaudiovideostudy.adapter.MainItem
import com.zyp.androidaudiovideostudy.audio.AudioActivity
import com.zyp.androidaudiovideostudy.audio.LiblameActivity
import com.zyp.androidaudiovideostudy.audio.LibmadActivity
import com.zyp.androidaudiovideostudy.camera.Camera2Activity
import com.zyp.androidaudiovideostudy.camera.CameraActivity
import com.zyp.androidaudiovideostudy.databinding.ActivityMainBinding
import com.zyp.androidaudiovideostudy.fdkaac.FdkAACActivity
import com.zyp.androidaudiovideostudy.ffmpeg.FFmpegActivity
import com.zyp.androidaudiovideostudy.gpuimage.GpuImageActivity
import com.zyp.androidaudiovideostudy.rtmp.CameraMediaCodecRtmpActivity
import com.zyp.androidaudiovideostudy.video.*
import com.zyp.androidaudiovideostudy.x264.X264Activity
import com.zyp.androidaudiovideostudy.yuv.RGBViewActivity
import com.zyp.androidaudiovideostudy.yuv.YuvCameraActivity
import com.zyp.androidaudiovideostudy.yuv.YUVTestActivity
import com.zyp.androidaudiovideostudy.yuv.YUViewActivity

class MainActivity : AppCompatActivity() {

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
        MainItem(CameraMediaCodecActivity::class.java),
        MainItem(MediaPlayerActivity::class.java),
        MainItem(VideoViewActivity::class.java),
        MainItem(MediaCodecVideoActivity::class.java),
        MainItem(CameraOpenGLActivity::class.java),
        MainItem(CameraMediaCodecRtmpActivity::class.java),
        MainItem(FFmpegActivity::class.java),
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

