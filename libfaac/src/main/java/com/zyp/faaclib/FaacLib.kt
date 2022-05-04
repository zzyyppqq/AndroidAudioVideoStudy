package com.zyp.faaclib

class FaacLib {

    /**
     * A native method that is implemented by the 'nativelib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'nativelib' library on application startup.
        init {
            System.loadLibrary("faac-lib")
        }
    }
}