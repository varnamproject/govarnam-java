/*
 * Varnam Java Interface
 * Copyright 2013, Navaneeth K.N
 * Copyright 2020, Subin Siby
 * https://github.com/navaneeth/libvarnam-java
 * https://gitlab.com/indicproject/varnam/libvarnam-java
 */

package com.varnamproject.varnam;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public interface VarnamLibrary extends Library {
  VarnamLibrary INSTANCE = (VarnamLibrary) Native.loadLibrary("varnam", VarnamLibrary.class);

  int varnam_init(String scheme_file, PointerByReference handle, PointerByReference message);
  String varnam_version();
  String varnam_get_scheme_language_code(Pointer handle);

  String varnam_get_last_error(Pointer handle);
  int varnam_transliterate(Pointer handle, String input, PointerByReference output);
  int varnam_learn(Pointer handle, String word);

  interface LearnCallback extends Callback {
    void invoke(Pointer handle, String word, int status_code, Pointer object);
  }
  int varnam_learn_from_file(Pointer handle, String path, Varnam.LearnStatus status, LearnCallback callback, Pointer object);

  int varnam_import_learnings_from_file(Pointer handle, String path);

  int VARNAM_EXPORT_WORDS = 0;
  int VARNAM_EXPORT_FULL = 1;
  interface ExportCallback extends Callback {
    void invoke(int total_words, int total_processed, String current_word);
  }
  int varnam_export_words(Pointer handle, int words_per_file, String dirPath, int export_type, ExportCallback callback);

  int varnam_config(Pointer handle, int type, Object... args);
  
  // varray related functions
  int varray_length(Pointer array);
  Pointer varray_get(Pointer array, int index);
} 
