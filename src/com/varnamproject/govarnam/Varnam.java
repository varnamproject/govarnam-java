/*
 * Varnam JNI Interface
 * Copyright 2021, Subin Siby
 * https://gitlab.com/indicproject/varnam/libvarnam-java
 * Licensed under LGPL-3.0
 */

package com.varnamproject.govarnam;

import java.util.Arrays;
import java.util.List;

public final class Varnam {
    int VARNAM_SUCCESS = 0;
    int VARNAM_MISUSE = 1;
    int VARNAM_ERROR = 2;

    static {
        System.loadLibrary("govarnam_jni");
    }

    private native int varnam_init(String vstFile, String learningsFile);

    private native String varnam_get_last_error(int handle);

    private native int varnam_close(int handle);

    private native Suggestion[] varnam_transliterate(int handle, int id, String word);
    private native TransliterationResult varnam_transliterate_advanced(int handle, int id, String word);

    private native int varnam_learn(int handle, String word);

    private native int varnam_cancel(int id);

    private native void varnam_set_dictionary_suggestions_limit(int handle, int limit);

    private native void varnam_set_tokenizer_suggestions_limit(int handle, int limit);

    private int handle;

    public Varnam(String vstFile, String learningsFile) throws VarnamException {
        // handle value will be set by JNI C
        int status = varnam_init(vstFile, learningsFile);
        if (status != VARNAM_SUCCESS) {
            throw new VarnamException("Error initializing varnam." + getLastError());
        }
    }

    public String getLastError() {
        return varnam_get_last_error(this.handle);
    }

    public boolean close() {
        return varnam_close(this.handle) == VARNAM_SUCCESS;
    }

    public void setDictionarySuggestionsLimit(int limit) {
        varnam_set_dictionary_suggestions_limit(this.handle, limit);
    }

    public void setTokenizerSuggestionsLimit(int limit) {
        varnam_set_tokenizer_suggestions_limit(this.handle, limit);
    }

    public Suggestion[] transliterate(int id, String word) throws VarnamException {
        return varnam_transliterate(this.handle, id, word);
    }

    public TransliterationResult transliterateAdvanced(int id, String word) throws VarnamException {
        return varnam_transliterate_advanced(this.handle, id, word);
    }

    public boolean cancel(int id) {
        return varnam_cancel(id) == VARNAM_SUCCESS;
    }

    public void learn(String word) throws VarnamException {
        int status = varnam_learn(handle, word);
        if (status != 0) {
            throw new VarnamException(varnam_get_last_error(handle));
        }
    }

//  public static class LearnStatus extends Structure {
//    public static class ByReference extends LearnStatus implements Structure.ByReference {}
//
//    public int total_sugs;
//    public int failed;
//
//    protected List<String> getFieldOrder() {
//      return Arrays.asList("total_sugs", "failed");
//    }
//  }

//  public LearnStatus.ByReference learnFromFile(String path, VarnamLibrary.LearnCallback callback) throws VarnamException {
//    VarnamLibrary library = VarnamLibrary.INSTANCE;
//    LearnStatus.ByReference learnStatus = new LearnStatus.ByReference();
//
//    int status = library.varnam_learn_from_file(handle, path, learnStatus, callback, null);
//    if (status != 0) {
//      throw new VarnamException(library.varnam_get_last_error(handle));
//    }
//
//    return learnStatus;
//  }
//
//  /**
//   * Import a varnam exported/trained file
//   * @param path
//   * @return
//   * @throws VarnamException
//   */
//  public void importFromFile(String path) throws VarnamException {
//    VarnamLibrary library = VarnamLibrary.INSTANCE;
//
//    int status = library.varnam_import_learnings_from_file(handle, path);
//    if (status != 0) {
//      throw new VarnamException(library.varnam_get_last_error(handle));
//    }
//  }
//
//  /**
//   * Export sugs to a folder
//   * @param dirPath
//   * @param callback
//   * @throws VarnamException
//   */
//  public void exportFull(String dirPath, VarnamLibrary.ExportCallback callback) throws VarnamException {
////    VarnamLibrary library = VarnamLibrary.INSTANCE;
////
////    int status = library.varnam_export(handle, 30000, dirPath, VarnamLibrary.VARNAM_EXPORT_FULL, callback);
////    if (status != 0) {
////      throw new VarnamException(status + ": " + library.varnam_get_last_error(handle));
////    }
//  }
//
//  public String getVstFile() {
//    return vstFile;
//  }
}
