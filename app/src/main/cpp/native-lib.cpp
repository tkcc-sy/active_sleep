#include <jni.h>
#include <string>
#include "libSnoreDetective.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_paramount_bed_nativewrapper_SnoreDetectiveLibrary_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_paramount_bed_nativewrapper_SnoreDetectiveLibrary_SDL_1SnoreInitialize(
        JNIEnv* env,
        jobject /* this */) {
    auto ret = SDL_SnoreInitialize();
    printf("call SnoreInitialize( %d\n", (int)ret);
    return ret;
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_paramount_bed_nativewrapper_SnoreDetectiveLibrary_SDL_1SnoreAnalysis(
        JNIEnv* env,
        jobject /* this */, jstring filePath,  jstring outPath,
        jint snoTime, jint snoreTh, jint outInterval, jint outSnoreFileTime, jint outSnoreFileCount) {
    auto c1 = env->GetStringUTFChars(filePath, 0);
    auto c2 = env->GetStringUTFChars(outPath, 0);
    auto ret = SDL_SnoreAnalysis(c1, c2, snoTime, snoreTh, outInterval, outSnoreFileTime, outSnoreFileCount);
    env->ReleaseStringUTFChars(filePath, c1);
    env->ReleaseStringUTFChars(outPath, c2);
    printf("call SnoreCalc %d\n", (int)ret);
    return ret;
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_paramount_bed_nativewrapper_SnoreDetectiveLibrary_SDL_1SnoreAnalysisCancel(
        JNIEnv* env,
        jobject /* this */) {
    auto ret = SDL_SnoreAnalysisCancel();
    printf("call SnoreCalcStop %d\n", (int)ret);
    return ret;
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_paramount_bed_nativewrapper_SnoreDetectiveLibrary_SDL_1GetErrorCode(
        JNIEnv* env,
        jobject /* this */) {
    auto ret = SDL_GetErrorCode();
    printf("call GetErrorCode 0x%X\n", (unsigned int)ret);
    return ret;
}