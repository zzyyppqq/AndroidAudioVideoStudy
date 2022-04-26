package com.zyp.fdkaaclib

class FdkAACLib {

    /**
     * A native method that is implemented by the 'nativelib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    external fun aacDecoder(aacPath: String, pcmPath: String): Int

    companion object {
        // Used to load the 'nativelib' library on application startup.
        init {
            System.loadLibrary("fdk-aac-lib")
        }
    }
}