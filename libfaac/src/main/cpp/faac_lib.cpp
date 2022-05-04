#include <jni.h>
#include <string>
#include "android/log.h"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , "faac", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO  , "faac", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , "faac", __VA_ARGS__)




extern "C" JNIEXPORT jstring JNICALL
Java_com_zyp_faaclib_FaacLib_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
