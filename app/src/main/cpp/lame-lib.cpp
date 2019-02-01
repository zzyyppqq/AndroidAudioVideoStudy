#include <jni.h>
#include <string>
#include <cstring>

#include "lame.h"


extern "C"
JNIEXPORT jstring JNICALL
Java_com_zyp_androidaudiovideostudy_LameNative_getLameVersion(JNIEnv *env, jclass type) {

    // TODO
//    std::string hello = "Hello from C++";

    return env->NewStringUTF(get_lame_version());

}

static lame_global_flags *glf = NULL;

extern "C"
JNIEXPORT void JNICALL
Java_com_zyp_androidaudiovideostudy_LameNative_close(JNIEnv *env, jclass type) {

    lame_close(glf);
    glf = NULL;

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_zyp_androidaudiovideostudy_LameNative_encode(JNIEnv *env, jclass type,
                                                      jshortArray buffer_l_,
                                                      jshortArray buffer_r_, jint samples,
                                                      jbyteArray mp3buf_) {
    jshort *buffer_l = env->GetShortArrayElements(buffer_l_, NULL);
    jshort *buffer_r = env->GetShortArrayElements(buffer_r_, NULL);
    jbyte *mp3buf = env->GetByteArrayElements(mp3buf_, NULL);


    const jsize mp3buf_size = env->GetArrayLength(mp3buf_);

    int result = lame_encode_buffer(glf, buffer_l, buffer_r, samples, (u_char *) mp3buf,
                                    mp3buf_size);


    env->ReleaseShortArrayElements(buffer_l_, buffer_l, 0);
    env->ReleaseShortArrayElements(buffer_r_, buffer_r, 0);
    env->ReleaseByteArrayElements(mp3buf_, mp3buf, 0);

    return result;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_zyp_androidaudiovideostudy_LameNative_flush(JNIEnv *env, jclass type,
                                                     jbyteArray mp3buf_) {
    jbyte *mp3buf = env->GetByteArrayElements(mp3buf_, NULL);

    const jsize mp3buf_size = env->GetArrayLength(mp3buf_);

    int result = lame_encode_flush(glf, (u_char *) mp3buf, mp3buf_size);

    env->ReleaseByteArrayElements(mp3buf_, mp3buf, 0);

    return result;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_zyp_androidaudiovideostudy_LameNative_init__IIIII(JNIEnv *env, jclass type,
                                                           jint inSampleRate, jint outChannel,
                                                           jint outSampleRate, jint outBitrate,
                                                           jint quality) {

    if (glf != NULL) {
        lame_close(glf);
        glf = NULL;
    }
    glf = lame_init();
    lame_set_in_samplerate(glf, inSampleRate);
    lame_set_num_channels(glf, outChannel);
    lame_set_out_samplerate(glf, outSampleRate);
    lame_set_brate(glf, outBitrate);
    lame_set_quality(glf, quality);
    lame_init_params(glf);

}

int flag = 0;

extern "C"
JNIEXPORT jint JNICALL
Java_com_zyp_androidaudiovideostudy_LameNative_convertWavToMp3__Ljava_lang_String_2Ljava_lang_String_2IIIIII(JNIEnv *env, jclass type,
                                                              jstring wavPath_, jstring mp3Path_,
                                                              jint inSamplerate, jint outSamplerate,
                                                              jint numChannels, jint brate,
                                                              jint quality, jint vbrModel) {
    const char *wavPath = env->GetStringUTFChars(wavPath_, 0);
    const char *mp3Path = env->GetStringUTFChars(mp3Path_, 0);

    //1.´ò¿ª wav,MP3ÎÄ¼þ
    FILE *fwav = fopen(wavPath, "rb");
    fseek(fwav, 44, SEEK_CUR);//跳过wav的头44byte
    FILE *fmp3 = fopen(mp3Path, "wb");

    short int wav_buffer[8192 * 2];
    unsigned char mp3_buffer[8192];

    //1.³õÊ¼»¯lameµÄ±àÂëÆ÷
    lame_t lame = lame_init();

    //2.ÉèÖÃlame mp3±àÂëµÄ²ÎÊý
    if (inSamplerate >= 0) {
        lame_set_in_samplerate(lame, inSamplerate);
    }
    if (outSamplerate >= 0) {
        lame_set_out_samplerate(lame, outSamplerate);
    }
    if (numChannels >= 0) {
        lame_set_num_channels(lame, numChannels);
    }
    if (brate >= 0) {
        lame_set_brate(lame, brate);
    }
    if (quality >= 0) {
        lame_set_quality(lame, quality);
    }
    if (vbrModel >= 0) {
        switch (vbrModel) {
            case 0:
                lame_set_VBR(lame, vbr_default);
                break;
            case 1:
                lame_set_VBR(lame, vbr_off);
                break;
            case 2:
                lame_set_VBR(lame, vbr_abr);
                break;
            case 3:
                lame_set_VBR(lame, vbr_mtrh);
                break;
            default:
                break;
        }
    }

    lame_init_params(lame);
    //3.¿ªÊ¼Ð´Èë
    int read;
    int write; //´ú±í¶ÁÁË¶àÉÙ¸ö´Î ºÍÐ´ÁË¶àÉÙ´Î
    int total = 0; // µ±Ç°¶ÁµÄwavÎÄ¼þµÄbyteÊýÄ¿
    do {
        if (flag == 404) {
            return -1;
        }
        read = fread(wav_buffer, sizeof(short int) * 2, 8192, fwav);
        total += read * sizeof(short int) * 2;
        if (read != 0) {

            write = lame_encode_buffer_interleaved(lame, wav_buffer, read, mp3_buffer, 8192);
            //°Ñ×ª»¯ºóµÄmp3Êý¾ÝÐ´µ½ÎÄ¼þÀï
            fwrite(mp3_buffer, sizeof(unsigned char), write, fmp3);
        }
        if (read == 0) {
            lame_encode_flush(lame, mp3_buffer, 8192);
        }

    } while (read != 0);
    lame_mp3_tags_fid(lame, fmp3);//向一个文件指针中写入规范的VBRTAG
    lame_close(lame);
    fclose(fwav);
    fclose(fmp3);

    env->ReleaseStringUTFChars(wavPath_, wavPath);
    env->ReleaseStringUTFChars(mp3Path_, mp3Path);

    return 1;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_zyp_androidaudiovideostudy_LameNative_convertPcmToMp3(
        JNIEnv *env, jclass type, jstring pcmPath_, jstring mp3Path_, jint inSamplerate,
        jint outSamplerate, jint numChannels, jint brate, jint quality, jint vbrModel) {
    const char *pcmPath = env->GetStringUTFChars(pcmPath_, 0);
    const char *mp3Path = env->GetStringUTFChars(mp3Path_, 0);

    //1.´ò¿ª wav,MP3ÎÄ¼þ
    FILE *fpcm = fopen(pcmPath, "rb");
    FILE *fmp3 = fopen(mp3Path, "wb");

    short int wav_buffer[8192 * 2];
    unsigned char mp3_buffer[8192];

    //1.³õÊ¼»¯lameµÄ±àÂëÆ÷
    lame_t lame = lame_init();

    //2.ÉèÖÃlame mp3±àÂëµÄ²ÎÊý
    if (inSamplerate >= 0) {
        lame_set_in_samplerate(lame, inSamplerate);
    }
    if (outSamplerate >= 0) {
        lame_set_out_samplerate(lame, outSamplerate);
    }
    if (numChannels >= 0) {
        lame_set_num_channels(lame, numChannels);
    }
    if (brate >= 0) {
        lame_set_brate(lame, brate);
    }
    if (quality >= 0) {
        lame_set_quality(lame, quality);
    }
    if (vbrModel >= 0) {
        switch (vbrModel) {
            case 0:
                lame_set_VBR(lame, vbr_default);
                break;
            case 1:
                lame_set_VBR(lame, vbr_off);
                break;
            case 2:
                lame_set_VBR(lame, vbr_abr);
                break;
            case 3:
                lame_set_VBR(lame, vbr_mtrh);
                break;
            default:
                break;
        }
    }

    lame_init_params(lame);
    //3.¿ªÊ¼Ð´Èë
    int read;
    int write; //´ú±í¶ÁÁË¶àÉÙ¸ö´Î ºÍÐ´ÁË¶àÉÙ´Î
    int total = 0; // µ±Ç°¶ÁµÄwavÎÄ¼þµÄbyteÊýÄ¿
    do {
        if (flag == 404) {
            return -1;
        }
        read = fread(wav_buffer, sizeof(short int) * 2, 8192, fpcm);
        total += read * sizeof(short int) * 2;
        if (read != 0) {

            write = lame_encode_buffer_interleaved(lame, wav_buffer, read, mp3_buffer, 8192);

            //°Ñ×ª»¯ºóµÄmp3Êý¾ÝÐ´µ½ÎÄ¼þÀï
            fwrite(mp3_buffer, sizeof(unsigned char), write, fmp3);
        }
        if (read == 0) {
            lame_encode_flush(lame, mp3_buffer, 8192);
        }

    } while (read != 0);
    lame_mp3_tags_fid(lame, fmp3);//向一个文件指针中写入规范的VBRTAG
    lame_close(lame);
    fclose(fpcm);
    fclose(fmp3);

    env->ReleaseStringUTFChars(pcmPath_, pcmPath);
    env->ReleaseStringUTFChars(mp3Path_, mp3Path);

    return 1;
}