#include <jni.h>
#include <string>
#include <aacdecoder_lib.h>
#include "android/log.h"

#define LOAS_LEN (3)
#define LOAS_SYNC_WORD 0x2b7


#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , "fdk-aac", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO  , "fdk-aac", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , "fdk-aac", __VA_ARGS__)


extern "C" JNIEXPORT jstring JNICALL
Java_com_zyp_fdkaaclib_FdkAACLib_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_zyp_fdkaaclib_FdkAACLib_aacDecoder(JNIEnv *env, jobject thiz, jstring aac_path,
                                            jstring pcm_path) {
    const char *aacfile = env->GetStringUTFChars(aac_path, JNI_FALSE);
    const char *pcmfile = env->GetStringUTFChars(pcm_path, JNI_FALSE);

    if (!aacfile) {
        LOGE("aacfile cannot be null");
        return -1;
    }
    if (!pcmfile) {
        LOGE("pcmfile cannot be null");
        return -1;
    }
    LOGI("aacfile: %s, pcmfile: %s", aacfile, pcmfile);
    HANDLE_AACDECODER handle;
    handle = aacDecoder_Open(TT_MP4_LOAS, 1);
    AAC_DECODER_ERROR err;
    unsigned int size = 1024;
    unsigned int valid;
    unsigned int loasflag;

    FILE *aacHandle;
    FILE *pcmHandle;

    aacHandle = fopen(aacfile, "rb");
    pcmHandle = fopen(pcmfile, "wb");
    unsigned char *data = (unsigned char *) malloc(size);
    unsigned int decsize = 8 * 2048 * sizeof(INT_PCM);
    unsigned char *decdata = (unsigned char *) malloc(decsize);
    LOGI("start");
    do {
        int ret = fread(data, 1, LOAS_LEN, aacHandle);//loas header is 3 chars.
        if (ret < 1)
            break;
        loasflag = data[0] << 3 | ((data[1] & 0xe0)
                >> 5);//data[0](8bits) | data[1]  (pre 3bits, e0 1110 0000)  = 11 bits
        if (loasflag != LOAS_SYNC_WORD) //LOAS_SYNC_WORD is 2b7
        {
            LOGI("loasflag %x\n", loasflag);
            break;
        }

        size = (data[1] & 0x1f) << 8 | data[2]; //1f is 1 1111(5bits) | data[2] (8bits)  = 13 bits
        LOGI("size %d\n", size);
        ret = fread(data + LOAS_LEN, 1, size, aacHandle);
        size += LOAS_LEN;
        if (ret < 1)
            break;
        valid = size;
        err = aacDecoder_Fill(handle, &data, &size, &valid);
        if (err > 0)
            LOGE("fill err:  %s", err);
        err = aacDecoder_DecodeFrame(handle, (INT_PCM *) decdata, decsize / sizeof(INT_PCM), 0);
        if (err > 0)
            LOGE("dec err:  %s", err);
        CStreamInfo *info = aacDecoder_GetStreamInfo(handle);
        LOGI("channels: %d, sampleRate: %d, frameSize: %d", info->numChannels, info->sampleRate,
             info->frameSize);
        fwrite(decdata, 1, info->numChannels * info->frameSize * 2, pcmHandle);
    } while (true);

    fflush(pcmHandle);
    fclose(aacHandle);
    fclose(pcmHandle);

    aacDecoder_Close(handle);
    return 0;
}