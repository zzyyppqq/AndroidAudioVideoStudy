package com.zyp.av.video

import android.os.Bundle
import com.zyp.av.base.BaseActivity
import com.zyp.av.databinding.ActivityMediaCodecVideoBinding
import com.zyp.av.util.Const
import com.zyp.av.util.SimplePlayer


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