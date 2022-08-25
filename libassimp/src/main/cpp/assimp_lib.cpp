#include <jni.h>
#include <string>

#include <assimp/port/AndroidJNI/AndroidJNIIOSystem.h>


extern "C" JNIEXPORT jstring JNICALL
Java_com_zyp_assimp_AssimpLib_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";

    Assimp::Importer* importer = new Assimp::Importer();
    Assimp::AndroidJNIIOSystem *ioSystem = new Assimp::AndroidJNIIOSystem(app->activity);
    if ( nullptr != iosSystem ) {
        importer->SetIOHandler(ioSystem);
    }
    return env->NewStringUTF(hello.c_str());
}