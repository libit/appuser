/*
 * JNI 接口文件
 * 作者：Libit
 */

#ifdef __cplusplus
extern "C" {
#endif
#include "utils.h"
#include <stdio.h>

const char *mainUrl = "http://lrapps.cn/lr_appuser/user";
const char *debugKey = "1545c9587b92e5efcb5b9ea354232cc0";
const char *releaseKey = "78816b8bde1386b7ab6a7202402ebb1e";
const char *errInfo = "Good Bye.";

jint JNI_OnLoad(JavaVM *jvm, void *reserved)
{
    return JNI_VERSION_1_6;
}

bool isKeyValid(JNIEnv *env, jstring key)
{
    return (compareJString (env, key, env->NewStringUTF (releaseKey)) || compareJString (env, key, env->NewStringUTF (debugKey)));
}

jstring Java_com_lrcall_utils_INativeInterface_getLocal(JNIEnv *env, jobject thiz, jstring user, jstring pwd, jstring number, jstring agentId, jstring signKey, jstring key)
{
    if(!isKeyValid (env, key)){
        return env->NewStringUTF (errInfo);
    }

    // POST方式提交
    char *str = new char[128];
    sprintf (str, "%s/getLocalAjax", mainUrl);
    jstring url = env->NewStringUTF (str);
    delete str;

    char *params = new char[256];
    sprintf (params, "[username,%s];[password,%s];[number,%s];[platform,android];[agentId,%s];[signKey,%s]", getChars (env, user), getChars (env, pwd), getChars (env, number), getChars (env, agentId),
             getChars (env, signKey));
    jstring param = env->NewStringUTF (params);
    delete params;

    return doPostJMethod (env, thiz, url, param);
}

#ifdef __cplusplus
}
#endif
