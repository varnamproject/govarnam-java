#include <jni.h>
#include <string.h>
#include <android/log.h>
#include <libgovarnam.h>

JNIEXPORT jint JNICALL
Java_com_varnamproject_govarnam_Varnam_varnam_1init(JNIEnv *env, jobject thiz, jstring vst_file,
                                                    jstring learnings_file) {
    const char* vstFileConst = (*env)->GetStringUTFChars(env, vst_file, JNI_FALSE);
    const char* learningsFileConst = (*env)->GetStringUTFChars(env, learnings_file, JNI_FALSE);

    char* vstFile = strdup(vstFileConst);
    char* learningsFile = strdup(learningsFileConst);

    int handle = 0;
    int status = varnam_init(vstFile, learningsFile, &handle);

    // Get a reference to this object's class
    jclass thisClass = (*env)->GetObjectClass(env, thiz);

    // Get the Field ID of the instance variables "handle"
    jfieldID fidHandle = (*env)->GetFieldID(env, thisClass, "handle", "I");
    if (fidHandle == NULL) return VARNAM_ERROR;
    (*env)->SetIntField(env, thiz, fidHandle, handle);

    return status;
}

JNIEXPORT jstring JNICALL
Java_com_varnamproject_govarnam_Varnam_varnam_1get_1last_1error(JNIEnv *env, jobject thiz,
                                                                jint handle) {
    jstring err = (*env)->NewStringUTF(env, varnam_get_last_error(handle));
    return err;
}

JNIEXPORT jint JNICALL
Java_com_varnamproject_govarnam_Varnam_varnam_1close(JNIEnv *env, jobject thiz, jint handle) {
    return varnam_close(handle);
}

JNIEXPORT void JNICALL
Java_com_varnamproject_govarnam_Varnam_varnam_1set_1dictionary_1suggestions_1limit(JNIEnv *env,
                                                                                   jobject thiz,
                                                                                   jint handle,
                                                                                   jint limit) {
    varnam_set_dictionary_suggestions_limit(handle, limit);
}

JNIEXPORT void JNICALL
Java_com_varnamproject_govarnam_Varnam_varnam_1set_1tokenizer_1suggestions_1limit(JNIEnv *env,
                                                                                  jobject thiz,
                                                                                  jint handle,
                                                                                  jint limit) {
    varnam_set_tokenizer_suggestions_limit(handle, limit);
}

JNIEXPORT jint JNICALL
Java_com_varnamproject_govarnam_Varnam_varnam_1learn(JNIEnv *env, jobject thiz, jint handle,
                                                     jstring word) {
    const char* wordConst = (*env)->GetStringUTFChars(env, word, JNI_FALSE);
    char* wordChar = strdup(wordConst);
    int status = varnam_learn(handle, wordChar, 0);
    (*env)->ReleaseStringUTFChars(env, word, wordConst);
    free(wordChar);
    return status;
}

jobjectArray makeJavaSuggestionArray (JNIEnv* env, varray* sugs) {
    jclass jSugClass = (*env)->FindClass(env, "com/varnamproject/govarnam/Suggestion");
    jobjectArray sugArray = (*env)->NewObjectArray(env, varray_length(sugs), jSugClass, NULL);

    jfieldID WordID = (*env)->GetFieldID(env, jSugClass , "Word", "Ljava/lang/String;");
    jfieldID WeightID = (*env)->GetFieldID(env, jSugClass , "Weight", "I");

    jmethodID constructorID = (*env)->GetMethodID(env, jSugClass, "<init>", "()V");

    jobject obj;
    jint ji;
    for (int i = 0; i < varray_length(sugs); i++) {
        ji = i;
        Suggestion* sug = (Suggestion*) varray_get(sugs, i);

        obj = (*env)->NewObject(env, jSugClass, constructorID);
        (*env)->SetObjectField(env, obj, WordID, (*env)->NewStringUTF(env, sug->Word));
        (*env)->SetIntField(env, obj, WeightID, (jint) sug->Weight);
        (*env)->SetObjectArrayElement(env, sugArray, ji, obj);
    }

    return sugArray;
}

jobject JNICALL
Java_com_varnamproject_govarnam_Varnam_varnam_1transliterate_1with_1id(JNIEnv *env, jobject thiz,
                                                                       jint handle, jint id,
                                                                       jstring word) {
    const char* wordConst = (*env)->GetStringUTFChars(env, word, JNI_FALSE);

    char* wordChar = strdup(wordConst);
//  __android_log_print(ANDROID_LOG_DEBUG, "LOG_TAG", "Need to print : %s", wordChar);
    TransliterationResult* result = varnam_transliterate_with_id(handle, id, wordChar);
    free(wordChar);
    (*env)->ReleaseStringUTFChars(env, word, wordConst);

    if (result == NULL) {
        return NULL;
    }

    jobjectArray ExactMatches = makeJavaSuggestionArray(env, result->ExactMatches);
    jobjectArray DictionarySuggestions = makeJavaSuggestionArray(env, result->DictionarySuggestions);
    jobjectArray PatternDictionarySuggestions = makeJavaSuggestionArray(env, result->PatternDictionarySuggestions);
    jobjectArray TokenizerSuggestions = makeJavaSuggestionArray(env, result->TokenizerSuggestions);
    jobjectArray GreedyTokenized = makeJavaSuggestionArray(env, result->GreedyTokenized);

    jclass jTRclass = (*env)->FindClass(env, "com/varnamproject/govarnam/TransliterationResult");
    jmethodID constructorID = (*env)->GetMethodID(env, jTRclass, "<init>",
                                        "([Lcom/varnamproject/govarnam/Suggestion;[Lcom/varnamproject/govarnam/Suggestion;[Lcom/varnamproject/govarnam/Suggestion;[Lcom/varnamproject/govarnam/Suggestion;[Lcom/varnamproject/govarnam/Suggestion;)V");

    int size = 5;
    jvalue* args = malloc(size * sizeof(jvalue));
    args[0].l = ExactMatches;
    args[1].l = DictionarySuggestions;
    args[2].l = PatternDictionarySuggestions;
    args[3].l = TokenizerSuggestions;
    args[4].l = GreedyTokenized;

    return (*env)->NewObjectA(env, jTRclass, constructorID, args);
}

JNIEXPORT jint JNICALL
Java_com_varnamproject_govarnam_Varnam_varnam_1cancel(JNIEnv *env, jobject thiz, jint id) {
    return varnam_cancel(id);
}