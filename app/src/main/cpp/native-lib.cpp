#include <jni.h>
#include <string>


extern "C"
JNIEXPORT jstring JNICALL
Java_com_zyp_androidaudiovideostudy_AudioLameNative_stringFromJNI(JNIEnv *env, jclass type) {

    // TODO
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());

}