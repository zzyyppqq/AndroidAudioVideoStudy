package com.zyp.androidaudiovideostudy.ffmpeg;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.view.Surface;

/**
 * 此模块主要是演示 播放视频 播放音频的方法技巧。
 * ffmpeg代码都运行在主线程上，会阻塞主线程。
 * Created by zzr on 2018/12/11.
 */
public class FFPlayer {

    public native void init(String media_input_str,Surface surface);
    public native int play();
    public native void release();

    public native int playMusic(String media_input_str);

    /**
     * 创建一个AudioTrack对象，用于播放
     * @param sampleRateInHz 采样率
     * @param nb_channels 声道数
     * @return AudioTrack_obj
     *
     * // 使用流程
     * AudioTrack audioTrack = new AudioTrack
     * audioTrack.play();
     * audioTrack.write(audioData, offsetInBytes, sizeInBytes);
     */
    public AudioTrack createAudioTrack(int sampleRateInHz, int nb_channels){
        //固定格式的音频码流
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        //声道布局
        int channelConfig;
        if(nb_channels == 1){
            channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        } else {
            channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
        }

        int bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        //此方法已经deprecated，正式的可参考下方的代码。
        AudioTrack audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRateInHz, channelConfig,
                audioFormat,
                bufferSizeInBytes, AudioTrack.MODE_STREAM);

        //AudioManager mAudioManager = (AudioManager) Context.getSystemService(Context.AUDIO_SERVICE);
        //int bufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        //int sessionId = mAudioManager.generateAudioSessionId();
        //AudioAttributes audioAttributes = new AudioAttributes.Builder()
        //        .setUsage(AudioAttributes.USAGE_MEDIA)
        //        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        //        .build();
        //AudioFormat audioFormat = new AudioFormat.Builder().setSampleRate(sampleRateInHz)
        //        .setEncoding(audioFormat)
        //        .setChannelMask(channelConfig)
        //        .build();
        //AudioTrack mAudioTrack = new AudioTrack(audioAttributes, audioFormat, bufferSize * 2, AudioTrack.MODE_STREAM, sessionId);
        return audioTrack;
    }

    static
    {
        // Try loading libraries...
        try {
            // yuv为静态库， 已经打包到libzzr-ffmpeg-player.so动态库中
//            System.loadLibrary("yuv");

            System.loadLibrary("avutil");
            System.loadLibrary("swscale");
            System.loadLibrary("swresample");
            System.loadLibrary("avcodec");
            System.loadLibrary("avformat");

            System.loadLibrary("postproc");
            System.loadLibrary("avfilter");
            System.loadLibrary("avdevice");

            System.loadLibrary("zzr-ffmpeg-player");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
