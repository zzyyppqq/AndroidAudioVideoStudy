package com.zyp.androidaudiovideostudy.audio;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author vveng
 * @version version 1.0.0
 * @date 2018/7/24 16:03.
 * @email vvengstuggle@163.com
 * @instructions 说明
 * @descirbe 描述
 * @features 功能
 */
public class AudioRecordManager {

    private static final String TAG = "AudioRecordManager";
    private static final String DIR_NAME = "arm";
    private static String AudioFolderFile; //音频文件路径
    private static AudioRecordManager mAudioRecordManager;
    private File PcmFile = null ; //pcm音频文件
    private File WavFile = null;  //wav格式的音频文件
    private AudioRecordThread mAudioRecordThead; //录制线程
    private AudioRecordPlayThead mAudioRecordPlayThead;//播放线程
    private boolean isRecord = false;
    /**
     * 采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用。
     */
    public static final int SAMPLE_RATE_HERTZ = 44100;

    /**
     * 声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的。
     */
    public static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;

    /**
     * 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
     */
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;


    public static AudioRecordManager getInstance() {
        if (mAudioRecordManager == null) {
            synchronized (AudioRecordManager.class) {
                if (mAudioRecordManager == null) {
                    mAudioRecordManager = new AudioRecordManager();
                }
            }
        }
        return mAudioRecordManager;
    }


    /**
     * 播放音频
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public synchronized void playRecord() {
        //可防止重复点击录制
        if (true == isRecord) {
            Log.d(TAG, "无法开始播放，当前状态为：" + isRecord);
            return;
        }
        isRecord = true;
        mAudioRecordPlayThead = new AudioRecordPlayThead(PcmFile);
        mAudioRecordPlayThead.start();
    }

    /**
     * 停止播放
     */
    public void stopPlayRecord() {
        if (null != mAudioRecordPlayThead) {
            mAudioRecordPlayThead.interrupt();
            mAudioRecordPlayThead = null;
        }
        isRecord = false;
    }


    /**
     * 播放音频线程
     */
    private class AudioRecordPlayThead extends Thread {
        AudioTrack mAudioTrack;
        int BufferSize = 10240;
        File autoFile = null; //要播放的文件

        @RequiresApi(api = Build.VERSION_CODES.M)
        AudioRecordPlayThead(File file) {
            setPriority(MAX_PRIORITY);
            autoFile = file;
            //播放缓冲的最小大小
            BufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE_HERTZ,
                    AudioFormat.CHANNEL_OUT_STEREO, AUDIO_FORMAT);
            // 创建用于播放的 AudioTrack
            mAudioTrack = new AudioTrack.Builder()
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build())
                    .setAudioFormat(new AudioFormat.Builder()
                            .setEncoding(AUDIO_FORMAT)
                            .setSampleRate(SAMPLE_RATE_HERTZ)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                            .build())
                    .setBufferSizeInBytes(BufferSize)
                    .build();

        }

        @Override
        public void run() {

            Log.d(TAG, "播放开始");
            try {
                FileInputStream fis = new FileInputStream(autoFile);
                mAudioTrack.play();
                byte[] bytes = new byte[BufferSize];

                while(true == isRecord) {
                    int read = fis.read(bytes);
                    //若读取有错则跳过
                    if (AudioTrack.ERROR_INVALID_OPERATION == read
                            || AudioTrack.ERROR_BAD_VALUE == read) {
                        continue;
                    }

                    if (read != 0 && read != -1) {
                        mAudioTrack.write(bytes, 0, BufferSize);
                    }
                }
                mAudioTrack.stop();
                mAudioTrack.release();//释放资源
                fis.close();//关流

            } catch (Exception e) {
                e.printStackTrace();
            }

            isRecord = false;
            Log.d(TAG, "播放停止");
        }
    }


    /**
     * 开始录制
     */
    public synchronized void startRecord() {
        //可防止重复点击录制
        if (true == isRecord) {
            Log.d(TAG, "无法开始录制，当前状态为：" + isRecord);
            return;
        }
        isRecord = true;
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd_HHmmss", Locale.CHINA);
        //源pcm数据文件
        PcmFile = new File(AudioFolderFile + File.separator + sdf.format(new Date())+".pcm");
        //wav文件
        WavFile = new File(PcmFile.getPath().replace(".pcm",".wav"));

        Log.d(TAG, "PcmFile:"+ PcmFile.getName()+" , WavFile:"+WavFile.getName());

        if (null != mAudioRecordThead) {
            //若线程不为空,则中断线程
            mAudioRecordThead.interrupt();
            mAudioRecordThead = null;
        }
        mAudioRecordThead = new AudioRecordThread();
        mAudioRecordThead.start();
    }

    /**
     * 停止录制
     */
    public synchronized void stopRecord() {
//        if (null != mAudioRecordThead) {
//            mAudioRecordThead.interrupt();
//            mAudioRecordThead = null;
//        }

        isRecord = false;
    }

    /**
     * 录制线程
     */
    private class AudioRecordThread extends Thread {
        AudioRecord mAudioRecord;
        int BufferSize = 10240;

        AudioRecordThread() {
            /**
             * 获取音频缓冲最小的大小
             */
            BufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_HERTZ,
                    CHANNEL_CONFIG, AUDIO_FORMAT);
            /**
             * 参数1：音频源
             * 参数2：采样率 主流是44100
             * 参数3：声道设置 MONO单声道 STEREO立体声
             * 参数4：编码格式和采样大小 编码格式为PCM,主流大小为16BIT
             * 参数5：采集数据需要的缓冲区大小
             */
            mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE_HERTZ, CHANNEL_CONFIG, AUDIO_FORMAT, BufferSize);
        }

        @Override
        public void run() {
            //将状态置为录制

            Log.d(TAG, "录制开始");
            try {
                byte[] bytes = new byte[BufferSize];

                FileOutputStream pcmFos = new FileOutputStream(PcmFile);

                //开始录制
                mAudioRecord.startRecording();

                while (true == isRecord && !isInterrupted()) {
                    int read = mAudioRecord.read(bytes, 0, bytes.length);
                    //若读取数据没有出现错误，将数据写入文件
                    if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                        pcmFos.write(bytes, 0, read);
                        pcmFos.flush();
                    }
                }
                mAudioRecord.stop();//停止录制
                pcmFos.close();//关流
                Log.d(TAG, "run: close");
            } catch (Exception e) {
                e.printStackTrace();

            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isRecord = false;
            //当录制完成就将Pcm编码数据转化为wav文件，也可以直接生成.wav
//            PcmUtil.pcmtoWav(PcmFile.getPath(),WavFile.getPath(),new byte[BufferSize],SAMPLE_RATE_HERTZ,CHANNEL_CONFIG);
            PcmUtil.convertPcm2Wav(PcmFile.getPath(),WavFile.getPath(),SAMPLE_RATE_HERTZ,CHANNEL_CONFIG,BufferSize);
            Log.d(TAG, "录制结束");
        }

    }


    public void pcmToWav() {
       int BufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_HERTZ,
                CHANNEL_CONFIG, AUDIO_FORMAT);
        PcmUtil.pcmtoWav(PcmFile.getPath(),WavFile.getPath(),new byte[BufferSize],SAMPLE_RATE_HERTZ,CHANNEL_CONFIG);
    }



    /**
     * 初始化目录
     */
    public static void init() {
        //文件目录
        AudioFolderFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
                + File.separator + DIR_NAME;
        File WavDir = new File(AudioFolderFile);
        if (!WavDir.exists()) {
            boolean flag = WavDir.mkdirs();
            Log.d(TAG, "文件路径:" + AudioFolderFile + "创建结果:" + flag);
        } else {
            Log.d(TAG, "文件路径:" + AudioFolderFile + "创建结果: 已存在");
        }
    }



}
