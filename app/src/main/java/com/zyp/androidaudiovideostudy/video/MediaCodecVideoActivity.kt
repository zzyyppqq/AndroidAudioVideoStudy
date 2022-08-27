package com.zyp.androidaudiovideostudy.video

import android.R.string.cancel
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Bundle
import android.util.Log
import com.zyp.androidaudiovideostudy.base.BaseActivity
import com.zyp.androidaudiovideostudy.databinding.ActivityMediaCodecVideoBinding
import com.zyp.androidaudiovideostudy.util.Const
import com.zyp.androidaudiovideostudy.util.SimplePlayer
import java.io.IOException


class MediaCodecVideoActivity : BaseActivity() {

    companion object {
        private val TAG = MediaCodecVideoActivity::class.java.simpleName
    }
    private var _binding: ActivityMediaCodecVideoBinding? = null
    private val mBinding get() = _binding!!

    private var player: SimplePlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMediaCodecVideoBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        player = SimplePlayer(mBinding.surfaceView.holder.surface, "${Const.sdPath}/test_video.mp4")

        mBinding.btnPlay.setOnClickListener {
            if (player?.isPlaying?:false) {
                player?.stop()
            } else {
                player?.play()
            }
        }
    }


    override fun onDestroy() {
        player?.destroy()
    }
}