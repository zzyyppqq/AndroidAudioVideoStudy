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
Java_com_zyp_yuvlib_YuvLib_i420ToABGR(JNIEnv *env, jclass clazz, jbyteArray src,
                                       jbyteArray dst, jint width, jint height) {
    if (src == NULL || dst == NULL) {
        throw_exception(env, "java/lang/RuntimeException", "Src or dst byte array cannot be NULL!");
    }
    if (width <= 0 || height <= 0) {
        throw_exception(env, "java/lang/RuntimeException",
                        "Width and height must be greater than 0!");
    }

    jbyte *i420_data = env->GetByteArrayElements(src, JNI_FALSE);
    jbyte *abgr_data = env->GetByteArrayElements(dst, JNI_FALSE);

    i420_to_abgr((char *) i420_data, (char *) abgr_data, width, height);
    env->ReleaseByteArrayElements(src, i420_data, 0);
    env->ReleaseByteArrayElements(dst, abgr_data, 0);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_zyp_yuvlib_YuvLib_i420ToRGB24(JNIEnv *env, jclass clazz, jbyteArray src,
                                       jbyteArray dst, jint width, jint height) {
    if (src == NULL || dst == NULL) {
        throw_exception(env, "java/lang/RuntimeException", "Src or dst byte array cannot be NULL!");
    }
    if (width <= 0 || height <= 0) {
        throw_exception(env, "java/lang/RuntimeException",
                        "Width and height must be greater than 0!");
    }

    jbyte *i420_data = env->GetByteArrayElements(src, JNI_FALSE);
    jbyte *rgb24_data = env->GetByteArrayElements(dst, JNI_FALSE);

    i420_to_rgb24((char *) i420_data, (char *) rgb24_data, width, height);

    env->ReleaseByteArrayElements(src, i420_data, 0);
    env->ReleaseByteArrayElements(dst, rgb24_data, 0);
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


extern "C"
JNIEXPORT void JNICALL
Java_com_zyp_yuvlib_YuvLib_getBitmap(JNIEnv *env, jobject thiz, jobject jbitmap) {

    AndroidBitmapInfo info;
    int result = AndroidBitmap_getInfo(env, jbitmap, &info);
    if (result < 0) {
        LOGE("Cannot get bitmap info");
        return;
    }
    // Lock the pixels.
    void *ptr;
    result = AndroidBitmap_lockPixels(env, jbitmap, &ptr);
    if (result < 0) {
        LOGE("Cannot lock bitmap pixels");
        return;
    }
    unsigned short *pixels = (unsigned short *) ptr;
    unsigned short color_table[] = {
            0x0000, 0x0020, 0x0041, 0x0441, 0x0462, 0x0482, 0x04a2, 0x08a3,

            0x08c3, 0x08e3, 0x0904, 0x0924, 0x0d25, 0x0d45, 0x0d65, 0x0d86,

            0x11a6, 0x11a6, 0x11c7, 0x11e7, 0x1208, 0x1608, 0x1628, 0x1649,

            0x1669, 0x1689, 0x1a8a, 0x1aaa, 0x1acb, 0x1aeb, 0x1eeb, 0x1f0c,

            0x1f2c, 0x1f4c, 0x1f6d, 0x236d, 0x238e, 0x23ae, 0x23ce, 0x27cf,

            0x27ef, 0x240f, 0x2430, 0x2450, 0x2851, 0x2871, 0x2891, 0x28b2,

            0x2cd2, 0x2cd2, 0x2cf3, 0x2d13, 0x2d34, 0x3134, 0x3154, 0x3175,

            0x3195, 0x31b5, 0x35b6, 0x35d6, 0x35f7, 0x3617, 0x3a17, 0x3a38,

            0x3a58, 0x3a78, 0x3a99, 0x3e99, 0x3eba, 0x3eda, 0x3efa, 0x42fb,

            0x431b, 0x433b, 0x435c, 0x437c, 0x477d, 0x479d, 0x47bd, 0x47de,

            0x4bfe, 0x4bfe, 0x481f, 0x483f, 0x4840, 0x4c40, 0x4c60, 0x4c81,

            0x4ca1, 0x50c1, 0x50c2, 0x50e2, 0x5103, 0x5123, 0x5523, 0x5544,

            0x5564, 0x5584, 0x55a5, 0x59a5, 0x59c6, 0x59e6, 0x5a06, 0x5e07,

            0x5e27, 0x5e47, 0x5e68, 0x5e88, 0x6289, 0x62a9, 0x62c9, 0x62ea,

            0x670a, 0x670a, 0x672b, 0x674b, 0x676c, 0x6b6c, 0x6b8c, 0x6bad,

            0x6bcd, 0x6fed, 0x6fee, 0x6c0e, 0x6c2f, 0x6c4f, 0x704f, 0x7070,

            0x7090, 0x70b0, 0x70d1, 0x74d1, 0x74f2, 0x7512, 0x7532, 0x7933,

            0x7953, 0x7973, 0x7994, 0x79b4, 0x7db5, 0x7dd5, 0x7df5, 0x7e16,

            0x0236, 0x0236, 0x0257, 0x0277, 0x0298, 0x0698, 0x06b8, 0x06d9,

            0x06f9, 0x0b19, 0x0b1a, 0x0b3a, 0x0b5b, 0x0b7b, 0x0f7b, 0x0f9c,

            0x0fbc, 0x0fdc, 0x0ffd, 0x13fd, 0x101e, 0x103e, 0x105e, 0x145f,

            0x147f, 0x149f, 0x14a0, 0x14c0, 0x18c1, 0x18e1, 0x1901, 0x1922,

            0x1d42, 0x1d42, 0x1d63, 0x1d83, 0x1da4, 0x21a4, 0x21c4, 0x21e5,

            0x2205, 0x2625, 0x2626, 0x2646, 0x2667, 0x2687, 0x2a87, 0x2aa8,

            0x2ac8, 0x2ae8, 0x2f09, 0x2f09, 0x2f2a, 0x2f4a, 0x2f6a, 0x336b,

            0x338b, 0x33ab, 0x33cc, 0x33ec, 0x37ed, 0x340d, 0x342d, 0x344e,

            0x386e, 0x386e, 0x388f, 0x38af, 0x38d0, 0x3cd0, 0x3cf0, 0x3d11,

            0x3d31, 0x4151, 0x4152, 0x4172, 0x4193, 0x41b3, 0x45b3, 0x45d4,

            0x45f4, 0x4614, 0x4a35, 0x4a35, 0x4a56, 0x4a76, 0x4a96, 0x4e97,

            0x4eb7, 0x4ed7, 0x4ef8, 0x4f18, 0x5319, 0x5339, 0x5359, 0x537a,

            0x579a, 0x579a, 0x57bb, 0x57db, 0x57fc, 0x5bfc, 0x581c, 0x583d,

            0x585d, 0x5c7d, 0x5c7e, 0x5c9e, 0x5cbf, 0x5cdf, 0x60df, 0x60e0,
    };
    for (int i = 0; i < (int) info.height; i++) {
        for (int j = 0; j < (int) info.width / 3; j++) {
//            *(pixels + info.width * (info.height - 1 - i) + j * 3 + 0) = color_table[j];
//            *(pixels + info.width * (info.height - 1 - i) + j * 3 + 1) = color_table[j];
//            *(pixels + info.width * (info.height - 1 - i) + j * 3 + 2) = color_table[j];

            /**
             * https://blog.csdn.net/weixin_31281027/article/details/117616171
             * 在bitmap文件或者是windows中的CreateDIBSection(BI_RGB, 16)创建的位图，pixel中r, g, b排列为：
             * f e d c b a 9 8 7 6 5 4 3 2 1 0
             * --------- --------- ---------
             * r g b
             *
             * 而Android中的RGB_565， pixel中的r, g, b排列为：
             * f e d c b a 9 8 7 6 5 4 3 2 1 0
             * --------- ----------- ---------
             * r g b
             *
             * 都是16位色，但是不能直接copy。如果想直接保存这个位图，可以指定compression为BI_BITFIELDS，
             * 指明alpha, red, green, blue的mask。
             */
            unsigned short B = color_table[j];
            unsigned short B_r = (B >> 10) & 0x1f;
            unsigned short B_g = (B >> 5) & 0x1f;
            unsigned short B_b = (B >> 0) & 0x1f;
            B = B_r << 11 | B_g << 6 | B_b;
            *(pixels + info.width * (info.height - 1 - i) + j * 3 + 0) = B;
            *(pixels + info.width * (info.height - 1 - i) + j * 3 + 1) = B;
            *(pixels + info.width * (info.height - 1 - i) + j * 3 + 2) = B;
        }
    }
    // Unlock the pixels.
    result = AndroidBitmap_unlockPixels(env, jbitmap);
    if (result < 0) {
        LOGE("Cannot unlock bitmap pixels");
    }
}