package com.zyp.androidaudiovideostudy.video

import android.media.*
import android.os.Bundle
import android.util.Log
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import com.zyp.androidaudiovideostudy.databinding.ActivityMediaCodecBinding
import java.io.IOException

/**
 * MediaCodec params learn
 */
class MediaCodecActivity : AppCompatActivity() {

    companion object {
        val TAG = MediaCodecActivity::class.java.simpleName
    }

    private var _binding: ActivityMediaCodecBinding? = null
    private val mBinding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMediaCodecBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        mBinding.btnMediaFormat.setOnClickListener {
            val extractor = MediaExtractor();
            extractor.setDataSource("/sdcard/test_video.mp4");
            dumpFormat(extractor);

            displayDecoders()

            val mediaFormat = chooseVideoTrack(extractor)
            val mime = mediaFormat?.getString(MediaFormat.KEY_MIME)
            val mediaCodec = MediaCodec.createDecoderByType(mediaFormat?.getString(MediaFormat.KEY_MIME)!!)

            val codec = createCodec(mediaFormat, null)

            showSupportedColorFormat(mediaCodec.getCodecInfo().getCapabilitiesForType(mime));
        }
    }

    private fun showSupportedColorFormat(caps: MediaCodecInfo.CodecCapabilities) {
        print("supported color format: ")
        for (c in caps.colorFormats) {
            print(c.toString() + "\t")
        }
        println()
    }

    /**
     * track count: 2
     * track 0:video/avc size:640x360
     * track 1:audio/mp4a-latm samplerate: 22050, channel count:2
     * 这是一个H264(avc)的360P视频，包含一条22K采样的双通道音频
     */
    private fun dumpFormat(extractor: MediaExtractor) {
        val count = extractor.trackCount
        Log.i(TAG, "playVideo: track count: $count")
        for (i in 0 until count) {
            val format = extractor.getTrackFormat(i)
            Log.i(TAG, "playVideo: track " + i + ":" + getTrackInfo(format))
        }
    }

    private fun getTrackInfo(format: MediaFormat): String? {
        var info = format.getString(MediaFormat.KEY_MIME)
        if (info!!.startsWith("audio/")) {
            info += (" samplerate: " + format.getInteger(MediaFormat.KEY_SAMPLE_RATE) + ", channel count:" + format.getInteger(
                MediaFormat.KEY_CHANNEL_COUNT
            ))
        } else if (info.startsWith("video/")) {
            info += " size:" + format.getInteger(MediaFormat.KEY_WIDTH) + "x" + format.getInteger(
                MediaFormat.KEY_HEIGHT
            )
        }
        return info
    }

    /**
     * OMX.google.aac.decoder
     * OMX.google.amrnb.decoder
     * OMX.google.amrwb.decoder
     * OMX.qti.audio.decoder.flac
     * OMX.google.g711.alaw.decoder
     * OMX.google.g711.mlaw.decoder
     * OMX.google.gsm.decoder
     * OMX.google.mp3.decoder
     * OMX.google.opus.decoder
     * OMX.google.raw.decoder
     * OMX.google.vorbis.decoder
     * OMX.qcom.video.decoder.avc
     * OMX.google.h264.decoder
     * OMX.qcom.video.decoder.divx
     * OMX.qcom.video.decoder.divx311
     * OMX.qcom.video.decoder.divx4
     * OMX.qcom.video.decoder.h263
     * OMX.google.h263.decoder
     * OMX.qcom.video.decoder.hevc
     * OMX.google.hevc.decoder
     * OMX.qcom.video.decoder.mpeg2
     * OMX.qcom.video.decoder.mpeg4
     * OMX.google.mpeg4.decoder
     * OMX.qcom.video.decoder.vc1
     * OMX.qcom.video.decoder.vp8
     * OMX.google.vp8.decoder
     * OMX.qcom.video.decoder.vp9
     * OMX.google.vp9.decoder
     */
    private fun displayDecoders() {
        val list = MediaCodecList(MediaCodecList.REGULAR_CODECS) //REGULAR_CODECS参考api说明
        val codecs = list.codecInfos
        for (codec in codecs) {
            if (codec.isEncoder) continue
            Log.i(TAG, "displayDecoders: " + codec.name)
        }
    }

    private fun chooseVideoTrack(extractor: MediaExtractor): MediaFormat? {
        val count = extractor.trackCount
        for (i in 0 until count) {
            val format = extractor.getTrackFormat(i)
            if (format.getString(MediaFormat.KEY_MIME)!!.startsWith("video/")) {
                extractor.selectTrack(i) //选择轨道
                return format
            }
        }
        return null
    }

    @Throws(IOException::class)
    private fun createCodec(format: MediaFormat, surface: Surface?): MediaCodec? {
        val codecList = MediaCodecList(MediaCodecList.REGULAR_CODECS)
        // MediaCodec.createByCodecName("OMX.qcom.video.decoder.avc")
        val codec = MediaCodec.createByCodecName(codecList.findDecoderForFormat(format))
        codec.configure(format, surface, null, 0)
        return codec
    }

}