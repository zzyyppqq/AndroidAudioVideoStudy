package com.zyp.androidaudiovideostudy.ffmpeg

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.zyp.androidaudiovideostudy.databinding.ActivityFfmpegBinding
import com.zyp.androidaudiovideostudy.util.Const
import com.zyp.ffmpeglib.FFmpegLib
import java.io.File
import java.util.concurrent.Executors

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

        mBinding.btnAvcodecConfig.setOnClickListener {
            mBinding.tvInfo.text = ffmpegLib.avcodecConfiguration()
        }

        mBinding.btnPlayer.setOnClickListener {
            // mBinding.ffVideoPlayer.play("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
            mBinding.ffVideoPlayer.play("${Const.sdPath}/test_video.mp4")
        }

        mBinding.btnDecode.setOnClickListener {
            Executors.newSingleThreadExecutor().execute {
                ffmpegLib.decode("${Const.sdPath}/sintel.mp4", "${Const.sdPath}/sintel.yuv")
                runOnUiThread{
                    Toast.makeText(applicationContext,"decode success", Toast.LENGTH_LONG).show()
                }
            }
        }
        mBinding.btnStream.setOnClickListener {
            Executors.newSingleThreadExecutor().execute {
                ffmpegLib.stream("${Const.sdPath}/sintel.mp4", "rtmp://192.168.1.102/mytv")
                runOnUiThread{
                    Toast.makeText(applicationContext,"decode success", Toast.LENGTH_LONG).show()
                }
            }
        }
        mBinding.btnFfmpegCore.setOnClickListener {
            Executors.newSingleThreadExecutor().execute {
                val outFile = File("${Const.sdPath}/sintel.mkv")
                if (outFile.exists()) {
                    outFile.delete()
                }
                val cmdStr = "ffmpeg -i ${Const.sdPath}/sintel.mp4 ${Const.sdPath}/sintel.mkv"
                val cmdLine = cmdStr.split(" ").toTypedArray()
                ffmpegLib.ffmpegcore(cmdLine.size, cmdLine)
                runOnUiThread{
                    Toast.makeText(applicationContext,"ffmpeg success", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}