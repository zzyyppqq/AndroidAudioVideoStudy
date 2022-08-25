package com.zyp.assimp

class AssimpLib {

    /**
     * A native method that is implemented by the 'assimp' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'assimp' library on application startup.
        init {
            System.loadLibrary("assimp-lib")
        }
    }
}