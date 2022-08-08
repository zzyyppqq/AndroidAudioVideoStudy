#include <jni.h>
#include <string>
#include "android/log.h"
#include "libyuv/basic_types.h"
#include <android/bitmap.h>

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , "yuv", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO  , "yuv", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , "yuv", __VA_ARGS__)


extern "C" {
#include <jni.h>
#include "libyuv.h"
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
extern "C"
JNIEXPORT void JNICALL
Java_com_zyp_yuvlib_YuvLib_bitmapToI420(JNIEnv *env, jobject thiz, jbyteArray dst_argb_,
                                        jobject bitmap,
                                        jint width, jint height) {
    uint8_t *dst = (uint8_t *) env->GetByteArrayElements(dst_argb_, 0);


    jint uv_size = (width + 1) / 2 * (height + 1) / 2;
    jint y_size = width * height;
    jint yuv_size = y_size + uv_size * 2;


    AndroidBitmapInfo info;
    AndroidBitmap_getInfo(env, bitmap, &info);


    int src_width = info.width;
    int src_height = info.height;

    int src_stride_argb = src_width * 4;//info.stride
    int dst_plane1_size = src_width * src_height;

    uint8 *dst_buffer_y = dst;
    uint8 *dst_buffer_u = dst_buffer_y + dst_plane1_size;
//    uint8 *dst_buffer_v = dst_buffer_u + dst_plane1_size / 4;
    uint8 *dst_buffer_v = dst_buffer_u + (src_width + 1) / 2 * (src_height + 1) / 2;


    int dst_stride_y = width;
//    int dst_stride_u = width / 2;
//    int dst_stride_v = width / 2;
    int dst_stride_u = (width + 1) / 2;
    int dst_stride_v = (width + 1) / 2;

    void *dst_argb;
    AndroidBitmap_lockPixels(env, bitmap, &dst_argb);
    if (NULL != dst_argb) {
        const uint8 *src_frame = static_cast<const uint8 *>(dst_argb);
        argb_to_i420(src_frame, src_stride_argb,
                     dst_buffer_y, dst_stride_y,
                     dst_buffer_u, dst_stride_u,
                     dst_buffer_v, dst_stride_v,
                     width, height);
    }

    AndroidBitmap_unlockPixels(env, bitmap);
    env->ReleaseByteArrayElements(dst_argb_, reinterpret_cast<jbyte *>(dst), 0);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_zyp_yuvlib_YuvLib_argbToI420(JNIEnv *env, jobject thiz, jbyteArray buffer, jint width,
                                      jint height, jint size) {

    uint8_t *rgbBuffer = (unsigned char *) env->GetByteArrayElements(buffer, NULL);
    int y_length = width * height;
    int u_length = (width * height) / 4;
    int v_length = u_length;
    uint8_t *y = (unsigned char *) malloc(y_length);
    uint8_t *u = (unsigned char *) malloc(u_length);
    uint8_t *v = (unsigned char *) malloc(v_length);
    LOGE("y:%d \r u:%d \r v:%d \r size:%d", y_length, u_length, v_length, size);
    libyuv::ARGBToI420(rgbBuffer, width * 4, y, width, u, (width + 1) / 2, v,
                       (width + 1) / 2, width, height);

}