#include <jni.h>
#include "udp.h"

JNIEXPORT jstring JNICALL
Java_cn_etsoft_smarthomephone_jniUtils_udpServer(JNIEnv *env, jclass type) {

    // TODO
    udp_server("");

    return (*env)->NewStringUTF(env, "");
}