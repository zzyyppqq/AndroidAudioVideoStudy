#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>
#include <jni.h>
#include<android/native_window.h>
#include<android/native_window_jni.h>
#include <pthread.h>
#include <unistd.h>
#include <android/log.h>
#include <cassert>


#ifndef __MY_LOGS_HEADER__
#define __MY_LOGS_HEADER__
#ifdef __cplusplus
extern "C" {
#endif
#include <android/log.h>
// 宏定义类似java 层的定义,不同级别的Log LOGI, LOGD, LOGW, LOGE, LOGF。 对就Java中的 Log.i log.d
#define LOG_TAG    "JNILOG" // 这个是自定义的LOG的标识
//#undef LOG // 取消默认的LOG
#define ALOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG, __VA_ARGS__)
#define ALOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG, __VA_ARGS__)
#define ALOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG, __VA_ARGS__)
#define ALOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG, __VA_ARGS__)
#define ALOGF(...)  __android_log_print(ANDROID_LOG_FATAL,LOG_TAG, __VA_ARGS__)

#ifdef __cplusplus
}
#endif

#endif

struct RenderContext {
    char *file_in;
    bool bRun;
    ANativeWindow *pWind;
    int w;
    int h;
    int fps;
};
RenderContext gContext = {0};

int64_t getNowUs() {
#if 1
    timeval tv;
    gettimeofday(&tv, 0);
    return (int64_t) tv.tv_sec * 1000000 + (int64_t) tv.tv_usec;
#else
    return ALooper::GetNowUs();
#endif
}

static char *jstringToChar(JNIEnv *env, jstring jstr) {
    char *rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char *) malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}


void rbga2bgraLittle(char *buffer, int width, int height)
{
    unsigned int *bgra = (unsigned int *)buffer;
    for (int i = 0; i < width * height; i++) {
        bgra[i] = (bgra[i] & 0xFF000000) |         // ______AA
                  ((bgra[i] & 0x00FF0000) >> 16) | // BB______
                  (bgra[i] & 0x0000FF00) |         // __GG____
                  ((bgra[i] & 0x000000FF) << 16);  // ____RR__
    }
}

void *rendthread(void *pram) {
    RenderContext *pContext = (RenderContext *) pram;
/*------------------*/
    //开始
    int32_t ret = 0;
    //添加个引用，避免自动释放
    ANativeWindow_acquire(pContext->pWind);
    int windowWidth = ANativeWindow_getWidth(pContext->pWind);
    int windowHeight = ANativeWindow_getHeight(pContext->pWind);


    ret = ANativeWindow_setBuffersGeometry(pContext->pWind, pContext->w, pContext->h,
                                           WINDOW_FORMAT_RGBA_8888);

    ALOGD("[%s%d] rendthread windowWidth:%d windowHeight:%d", __FUNCTION__, __LINE__, windowWidth, windowHeight);
    ALOGD("[%s%d] rendthread w:%d h:%d", __FUNCTION__, __LINE__, pContext->w, pContext->h);
    if (ret != 0) {
        ALOGD("[%s%d] setbufferGeometry err,ret:%d", __FUNCTION__, __LINE__, ret);
        return NULL;
    }
    int framelen = pContext->w * pContext->h * 4;
    char *buf = (char *) malloc(framelen);
    if (buf == NULL) {
        ALOGD("[%s%d] malloc err", __FUNCTION__, __LINE__);
        return NULL;
    }

    FILE *fp_in = fopen(pContext->file_in, "rb");
    if (fp_in == NULL) {
        ALOGD("[%s%d] fopen err ,file:%s,errno:%d(%s)", __FUNCTION__, __LINE__, pContext->file_in,
              errno, strerror(errno));
        free(buf);
        return NULL;
    }

    /*------------------*/
    //渲染
    ANativeWindow_Buffer outBuffer;
    int64_t duration = 1000 * 1000 / pContext->fps;
    while (gContext.bRun) {
        int64_t time1 = getNowUs();
        ret = fread(buf, 1, framelen, fp_in);
        if (ret < framelen) {
            fseek(fp_in, SEEK_SET, 0);
            ret = fread(buf, 1, framelen, fp_in);
        }
        if (ret < framelen) {
            ALOGD("[%s%d] fread err", __FUNCTION__, __LINE__);
            break;
        }
        // rbga2bgraLittle(buf, pContext->w, pContext->h);
        ANativeWindow_lock(pContext->pWind, &outBuffer, nullptr);
        // memcpy(outBuffer.bits, buf, framelen);

        uint8_t *dstBuffer = static_cast<uint8_t *>(outBuffer.bits);
        int srcLineSize = pContext->w * 4;//RGBA
        // stride：缓冲区中的一行在内存中占用的像素数。stride可能 >= 宽度。所以不能直接使用【memcpy(outBuffer.bits, buf, framelen);】
        int dstLineSize = outBuffer.stride * 4;
        ALOGD("[%s%d] rendthread srcLineSize:%d dstLineSize:%d", __FUNCTION__, __LINE__, srcLineSize, dstLineSize);
        for (int i = 0; i < pContext->h; ++i) {
            // 第 i 行 处理
            memcpy(dstBuffer + i * dstLineSize, buf + i * srcLineSize, srcLineSize);
        }

        ANativeWindow_unlockAndPost(pContext->pWind);
        int64_t time2 = getNowUs();
        int64_t taketimes = time2 - time1;
        //休眠，控制帧率
        ALOGD("[%s%d] take times:%ld", __FUNCTION__, __LINE__, taketimes);
        usleep(duration - taketimes > 0 ? duration - taketimes : 0);
    }

/*------------------*/
    //结束
    //释放引用，
    ANativeWindow_release(pContext->pWind);

    free(buf);
    fclose(fp_in);
    return NULL;
}

void jStart(JNIEnv *env, jobject obj, jobject jsurface, jstring srcfile, jint w, jint h, int fps) {
    gContext.file_in = jstringToChar(env, srcfile);
    gContext.pWind = ANativeWindow_fromSurface(env, jsurface);
    gContext.w = w;
    gContext.h = h;
    gContext.fps = fps;
    gContext.bRun = true;
    pthread_t pid;
    if (pthread_create(&pid, NULL, rendthread, (void *) &gContext) < 0) {
        ALOGD("[%s%d] pthread_create err\n", __FUNCTION__, __LINE__);
        return;
    } else {
        pthread_detach(pid);
    }

}

void jStop(JNIEnv *env, jobject obj) {
    gContext.bRun = false;
    free(gContext.file_in);
}

static JNINativeMethod gMethods[] = {
        {"native_start", "(Landroid/view/Surface;Ljava/lang/String;III)V", (void *) jStart},
        {"native_stop",  "()V",                                            (void *) jStop},
};


static const char *const kClassPathName = "com/zyp/ffmpeglib/ANativeWindowRender";

static int registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *gMethods,
                                 int numMethods) {
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

// This function only registers the native methods
static int registerFunctios(JNIEnv *env) {
    ALOGD("register [%s]%d", __FUNCTION__, __LINE__);
    return registerNativeMethods(env,
                                 kClassPathName, gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    ALOGD("onloader");
    JNIEnv *env = NULL;
    jint result = -1;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        ALOGD("ERROR: GetEnv failed\n");
        goto bail;
    }
    assert(env != NULL);
    if (registerFunctios(env) < 0) {
        ALOGE(" onloader ERROR: MediaPlayer native registration failed\n");
        goto bail;
    }
    ALOGD("onloader register ok ![%s]%d", __FUNCTION__, __LINE__);
    result = JNI_VERSION_1_4;
    bail:
    return result;
}