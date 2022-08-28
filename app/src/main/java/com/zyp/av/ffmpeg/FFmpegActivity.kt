package com.zyp.av.ffmpeg

import com.zyp.av.base.BaseActivity
import android.os.Bundle
import com.zyp.av.databinding.ActivityFfmpegBinding
import com.zyp.av.util.Const
import com.zyp.av.util.ToastUtil
import com.zyp.ffmpeglib.FFmpegLib
import java.io.File
import java.util.concurrent.Executors

class FFmpegActivity : BaseActivity() {
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

        mBinding.btnPlayer.setOnClickListener { // mBinding.ffVideoPlayer.play("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
            mBinding.ffVideoPlayer.play("${Const.sdPath}/test_video.mp4")
        }

        mBinding.btnDecode.setOnClickListener {
            Executors.newSingleThreadExecutor().execute {
                ffmpegLib.decode("${Const.sdPath}/sintel.mp4", "${Const.sdPath}/sintel.yuv")
                ToastUtil.show("ffmpeg decode success")
            }
        }
        mBinding.btnStream.setOnClickListener {
            Executors.newSingleThreadExecutor().execute {
                ffmpegLib.stream("${Const.sdPath}/sintel.mp4", "rtmp://192.168.1.102/mytv")
                ToastUtil.show("ffmpeg rtmp push success")
            }
        }
        mBinding.btnFfmpegCore.setOnClickListener {
            Executors.newSingleThreadExecutor().execute {
                val inFile = File("${Const.sdPath}/sintel.mp4")
                if (!inFile.exists()) {
                    ToastUtil.show("ffmpeg input file not exists")
                    return@execute
                }
                val outFile = File("${Const.sdPath}/sintel.mkv")
                if (outFile.exists()) { // 输出文件存在则删除，ffmpegcore 方法不会删除或覆盖已生成的文件，会导致报错，故需要提前判断
                    outFile.delete()
                }
                val cmdStr = "ffmpeg -i ${inFile.absoluteFile} ${outFile.absoluteFile}"
                val cmdLine = cmdStr.split(" ").toTypedArray()
                ffmpegLib.ffmpegcore(cmdLine.size, cmdLine)
                ToastUtil.show("ffmpeg transcoding success")
            }
        }
    }
}