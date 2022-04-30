#include <jni.h>
#include <string>
#include "android/log.h"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , "yuv", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO  , "yuv", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , "yuv", __VA_ARGS__)


extern "C" {
#include <jni.h>
#include "yuv_convert.h"
#include "yuv_rotate.h"
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_zyp_yuvlib_YuvLib_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}


void throw_exception(JNIEnv *env, const char *exception, const char *message) {
    jclass clazz = env->FindClass(exception);
    if (NULL != clazz) {
        env->ThrowNew(clazz, message);
    }
}


extern "C"
JNIEXPORT void JNICALL
Java_com_zyp_yuvlib_YuvLib_i420ToNv21(JNIEnv *env, jclass clazz, jbyteArray src,
                                                jbyteArray dst, jint width, jint height) {
    if (src == NULL || dst == NULL) {
        throw_exception(env, "java/lang/RuntimeException", "Src or dst byte array cannot be NULL!");
    }
    if (width <= 0 || height <= 0) {
        throw_exception(env, "java/lang/RuntimeException",
                        "Width and height must be greater than 0!");
    }

    jbyte *i420_data = env->GetByteArrayElements(src, JNI_FALSE);
    jbyte *nv21_data = env->GetByteArrayElements(dst, JNI_FALSE);
    i420_to_nv21((char *) i420_data, (char *) nv21_data, width, height);

    env->ReleaseByteArrayElements(src, i420_data, 0);
    env->ReleaseByteArrayElements(dst, nv21_data, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_zyp_yuvlib_YuvLib_i420ToRGBA(JNIEnv *env, jclass clazz, jbyteArray src,
                                                jbyteArray dst, jint width, jint height) {
    if (src == NULL || dst == NULL) {
        throw_exception(env, "java/lang/RuntimeException", "Src or dst byte array cannot be NULL!");
    }
    if (width <= 0 || height <= 0) {
        throw_exception(env, "java/lang/RuntimeException",
                        "Width and height must be greater than 0!");
    }

    jbyte *i420_data = env->GetByteArrayElements(src, JNI_FALSE);
    jbyte *rgba_data = env->GetByteArrayElements(dst, JNI_FALSE);

    i420_to_rgba((char *) i420_data, (char *) rgba_data, width, height);

    env->ReleaseByteArrayElements(src, i420_data, 0);
    env->ReleaseByteArrayElements(dst, rgba_data, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_zyp_yuvlib_YuvLib_nv21ToI420(JNIEnv *env, jclass clazz, jbyteArray src,
                                                jbyteArray dst, jint width, jint height) {
    if (src == NULL || dst == NULL) {
        throw_exception(env, "java/lang/RuntimeException", "Src or dst byte array cannot be NULL!");
    }
    if (width <= 0 || height <= 0) {
        throw_exception(env, "java/lang/RuntimeException",
                        "Width and height must be greater than 0!");
    }

    jbyte *nv21_data = env->GetByteArrayElements(src, JNI_FALSE);
    jbyte *i420_data = env->GetByteArrayElements(dst, JNI_FALSE);

    nv21_to_i420((char *) nv21_data, (char *) i420_data, width, height);

    env->ReleaseByteArrayElements(src, nv21_data, 0);
    env->ReleaseByteArrayElements(dst, i420_data, 0);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_zyp_yuvlib_YuvLib_rgb24ToI420(JNIEnv *env, jclass clazz, jbyteArray src,
                                                 jbyteArray dst, jint width, jint height) {
    if (src == NULL || dst == NULL) {
        throw_exception(env, "java/lang/RuntimeException", "Src or dst byte array cannot be NULL!");
    }
    if (width <= 0 || height <= 0) {
        throw_exception(env, "java/lang/RuntimeException",
                        "Width and height must be greater than 0!");
    }

    jbyte *rgb24_data = env->GetByteArrayElements(src, JNI_FALSE);
    jbyte *i420_data = env->GetByteArrayElements(dst, JNI_FALSE);

    rgb24_to_i420((char *) rgb24_data, (char *) i420_data, width, height);

    env->ReleaseByteArrayElements(src, rgb24_data, 0);
    env->ReleaseByteArrayElements(dst, i420_data, 0);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_zyp_yuvlib_YuvLib_rotateI420(JNIEnv *env, jclass clazz, jbyteArray src,
                                                jbyteArray dst, jint width, jint height,
                                                jint degree) {
    if (src == NULL || dst == NULL) {
        throw_exception(env, "java/lang/RuntimeException", "Src or dst byte array cannot be NULL!");
    }
    if (width <= 0 || height <= 0) {
        throw_exception(env, "java/lang/RuntimeException",
                        "Width and height must be greater than 0!");
    }

    if (degree != 0 && degree != 90 && degree != 180 && degree != 270) {
        throw_exception(env, "java/lang/RuntimeException",
                        "The degree of rotation must be one of 0, 90, 180, 270!");
    }

    jbyte *i420_src = env->GetByteArrayElements(src, JNI_FALSE);
    jbyte *i420_dst = env->GetByteArrayElements(dst, JNI_FALSE);
    rotate_i420((char *) i420_src, (char *) i420_dst, width, height, degree);

    env->ReleaseByteArrayElements(src, i420_src, 0);
    env->ReleaseByteArrayElements(dst, i420_dst, 0);
}