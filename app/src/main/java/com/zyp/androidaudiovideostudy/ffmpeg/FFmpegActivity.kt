package com.zyp.androidaudiovideostudy.ffmpeg

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zyp.androidaudiovideostudy.R
import com.zyp.androidaudiovideostudy.databinding.ActivityFfmpegBinding
import com.zyp.androidaudiovideostudy.databinding.ActivityMediaCodecBinding
import com.zyp.androidaudiovideostudy.util.Const
import com.zyp.ffmpeglib.FFmpegLib

class FFmpegActivity : AppCompatActivity() {
    private var _binding: ActivityFfmpegBinding? = null
    private val mBinding get() = _binding!!
    private val ffmpegLib = FFmpegLib()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFfmpegBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        mBinding.btnProtocol.setOnClickListener {
            mBinding.tvInfo.text = ffmpegLib.urlProtocolInfo()
        }

        mBinding.btnFormat.setOnClickListener {
            mBinding.tvInfo.text = ffmpegLib.avformatInfo()
        }

        mBinding.btnCodec.setOnClickListener {
            mBinding.tvInfo.text = ffmpegLib.avcodecInfo()
        }

        mBinding.btnFilter.setOnClickListener {
            mBinding.tvInfo.text = ffmpegLib.avfilterInfo()
        }

        mBinding.btnPlayer.setOnClickListener {
            // mBinding.ffVideoPlayer.play("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
            mBinding.ffVideoPlayer.play("${Const.sdPath}/test_video.mp4")
        }
    }
}