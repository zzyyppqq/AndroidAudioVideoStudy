package com.zyp.av.video

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Toast
import com.zyp.av.base.BaseActivity
import androidx.core.content.FileProvider
import com.zyp.av.databinding.ActivityMediaPlayerBinding
import com.zyp.av.util.Const
import java.io.File

class MediaPlayerActivity : BaseActivity() {
    //媒体播放控制器
    private var mediaPlayer: MediaPlayer? = null

    //视频路径
    private var videoPath: String? = null

    private var _binding: ActivityMediaPlayerBinding? = null
    private val mBinding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMediaPlayerBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.etText.setText("${Const.sdPath}/test_video.mp4")

        ////把输送给surfaceView的视频画面，直接显示到屏幕上,不要维持它自身的缓冲区
        val holder =
            mBinding.surfaceView.holder //
         holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        holder.setKeepScreenOn(true)
        holder.addCallback(callback)

        initMedia()

        mBinding.btnPlay.setOnClickListener {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                pause()
            } else {
                start()
            }
        }
    }

    /**
     * 初始化媒体播放
     */
    private fun initMedia() {
        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC);

            val sharedFile = File(mBinding.etText.text.toString())
            val sharedFileUri =
                FileProvider.getUriForFile(this, "com.zyp.androidaudiovideostudy.provider", sharedFile);
            mediaPlayer?.setDataSource(this, sharedFileUri)
            //mediaPlayer?.setDataSource(this, Uri.parse("android.resource://com.zyp.androidaudiovideostudy/" + R.raw.test_video))

            mediaPlayer?.setOnPreparedListener {
                start() //缓冲完，播放
            }
            mediaPlayer?.setOnCompletionListener {
                Toast.makeText(this, "播放完毕", Toast.LENGTH_SHORT).show()
                mBinding.btnPlay.text = "重新播放"
            }
        } catch (e: Exception) {
            Log.e("Test", "出错了", e)
        }
    }

    private val callback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            mediaPlayer?.setDisplay(holder) //设置播放的容器
            mediaPlayer?.prepareAsync()
        }

        override fun surfaceChanged(
            holder: SurfaceHolder,
            format: Int,
            width: Int,
            height: Int
        ) { //            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            //                mediaPlayer!!.stop()
            //            }
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {

        }
    }

    private fun start() {
        mBinding.btnPlay.text = "暂停"
        mediaPlayer?.start()
    }

    private fun pause() {
        mBinding.btnPlay.text = "播放"
        mediaPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}