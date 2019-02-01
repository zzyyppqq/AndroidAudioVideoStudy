package com.zyp.androidaudiovideostudy;

public class LameNative {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("lame-lib");
    }

    public static int convertWavToMp3(String wavPath, String mp3Path, int samplerate){
        return convertWavToMp3(wavPath, mp3Path, samplerate,-1,2,-1,5,1);

    }
    public static int convertPcmToMp3(String pcmPath, String mp3Path, int samplerate){
        return convertPcmToMp3(pcmPath, mp3Path, samplerate,-1,2,-1,5,1);

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native static String getLameVersion();

    public native static void close();

    public native static int encode(short[] buffer_l, short[] buffer_r, int samples, byte[] mp3buf);

    public native static int flush(byte[] mp3buf);

    public native static void init(int inSampleRate, int outChannel, int outSampleRate, int outBitrate, int quality);

    public static void init(int inSampleRate, int outChannel, int outSampleRate, int outBitrate) {
        init(inSampleRate, outChannel, outSampleRate, outBitrate, 7);
    }

    /**
     * @param wavPath wav路径
     * @param mp3Path MP3 路径
     * @param inSamplerate 采样率 不设置传-1
     * @param outSamplerate 采样率 不设置传-1
     * @param numChannels 文件的声道数 不设置传-1
     * @param brate 比特率 不设置传-1
     * @param quality 0-9  2=high  5 = medium  7=low
     * @param vbrModel  0 = vbr_default  1 = vbr_off  2 = vbr_abr  3 = vbr_mtrh
     *
     * 可参考 https://blog.csdn.net/xjwangliang/article/details/7065985
     * @return
     */

    public static native int convertWavToMp3(String wavPath, String mp3Path, int inSamplerate, int outSamplerate, int numChannels, int brate, int quality, int vbrModel);
    public static native int convertPcmToMp3(String pcmPath, String mp3Path, int inSamplerate, int outSamplerate, int numChannels, int brate, int quality, int vbrModel);




}
