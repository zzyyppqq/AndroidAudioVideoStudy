package com.zyp.fmod;

public class NativeLib {

    // Used to load the 'fmod' library on application startup.
    static {
        System.loadLibrary("fmod");
    }

    /**
     * A native method that is implemented by the 'fmod' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}