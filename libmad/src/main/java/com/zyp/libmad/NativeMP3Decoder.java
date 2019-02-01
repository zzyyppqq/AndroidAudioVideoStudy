package com.zyp.libmad;

public class NativeMP3Decoder {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("mad-lib");
    }



    public native int initAudioPlayer(String file, int startAddress);

    public native int getAudioBuf(short[] audioBuffer, int numSamples);

    public native void closeAduioFile();

    public native int getAudioSamplerate();


}
