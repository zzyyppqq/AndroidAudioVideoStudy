package com.zyp.opencv;

public class NativeLib {

    // Used to load the 'opencv' library on application startup.
    static {
        System.loadLibrary("opencv");
    }

    /**
     * A native method that is implemented by the 'opencv' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}