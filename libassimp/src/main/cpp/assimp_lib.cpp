#include <jni.h>
#include <string>

#include <assimp/port/AndroidJNI/AndroidJNIIOSystem.h>


extern "C" JNIEXPORT jstring JNICALL
Java_com_zyp_assimp_AssimpLib_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";



    return env->NewStringUTF(hello.c_str());
}