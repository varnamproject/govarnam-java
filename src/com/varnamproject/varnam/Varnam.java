/*
 * Varnam Java Interface
 * Copyright 2013, Navaneeth K.N
 * Copyright 2020, Subin Siby
 * https://github.com/navaneeth/libvarnam-java
 * https://gitlab.com/indicproject/varnam/libvarnam-java
 */

package com.varnamproject.varnam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;

public final class Varnam {
  
  private final String vstFile;
  private final Pointer handle;

  public Varnam(String vstFile, String learningsFile) throws VarnamException {
    this.vstFile = vstFile;
    PointerByReference varnamHandle = new PointerByReference();
    PointerByReference errorMessage = new PointerByReference();
    int status = VarnamLibrary.INSTANCE.varnam_init(vstFile, varnamHandle, errorMessage);
    if (status != 0) {
      throw new VarnamException("Error initializing varnam." + errorMessage.getValue().getString(0));
    }

    this.handle = varnamHandle.getValue();
    VarnamLibrary.INSTANCE.varnam_config(handle, 102, learningsFile);
  }
  
  public void enableSuggestions(String suggestionsFile) {
    VarnamLibrary.INSTANCE.varnam_config(handle, 102, suggestionsFile);
  }
  
  public List<Word> transliterate(String textToTransliterate) throws VarnamException {
    PointerByReference output = new PointerByReference();
    VarnamLibrary library = VarnamLibrary.INSTANCE;
    int status = library.varnam_transliterate(handle, textToTransliterate, output);
    if (status != 0) {
      throw new VarnamException(library.varnam_get_last_error(handle));
    }
    
    ArrayList<Word> words = new ArrayList<Word>();
    Pointer result = output.getValue();
    int totalWords = library.varray_length(result);
    for (int i = 0; i < totalWords; i++) {
      Pointer item = library.varray_get(result, i);
      words.add(new Word(item));
    }
    
    return words;
  }

  public void learn(String word) throws VarnamException {
    int status = VarnamLibrary.INSTANCE.varnam_learn(handle, word);
    if (status != 0) {
      throw new VarnamException(VarnamLibrary.INSTANCE.varnam_get_last_error(handle));
    }
  }

  public static class LearnStatus extends Structure {
    public static class ByReference extends LearnStatus implements Structure.ByReference {}

    public int total_words;
    public int failed;

    protected List<String> getFieldOrder() {
      return Arrays.asList("total_words", "failed");
    }
  }

  public LearnStatus.ByReference learnFromFile(String path, VarnamLibrary.LearnCallback callback) throws VarnamException {
    VarnamLibrary library = VarnamLibrary.INSTANCE;
    LearnStatus.ByReference learnStatus = new LearnStatus.ByReference();

    int status = library.varnam_learn_from_file(handle, path, learnStatus, callback, null);
    if (status != 0) {
      throw new VarnamException(library.varnam_get_last_error(handle));
    }

    return learnStatus;
  }

  /**
   * Import a varnam exported/trained file
   * @param path
   * @return
   * @throws VarnamException
   */
  public void importFromFile(String path) throws VarnamException {
    VarnamLibrary library = VarnamLibrary.INSTANCE;

    int status = library.varnam_import_learnings_from_file(handle, path);
    if (status != 0) {
      throw new VarnamException(library.varnam_get_last_error(handle));
    }
  }

  /**
   * Export words to a folder
   * @param dirPath
   * @param callback
   * @throws VarnamException
   */
  public void exportFull(String dirPath, VarnamLibrary.ExportCallback callback) throws VarnamException {
    VarnamLibrary library = VarnamLibrary.INSTANCE;

    int status = library.varnam_export_words(handle, 30000, dirPath, VarnamLibrary.VARNAM_EXPORT_FULL, callback);
    if (status != 0) {
      throw new VarnamException(status + ": " + library.varnam_get_last_error(handle));
    }
  }

  public String getVstFile() {
    return vstFile;
  }
}
