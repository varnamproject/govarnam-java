package com.varnamproject.govarnam;

import java.util.Arrays;
import java.util.List;

public class TransliterationResult {
    public List<Suggestion> ExactMatches;
    public List<Suggestion> DictionarySuggestions;
    public List<Suggestion> PatternDictionarySuggestions;
    public List<Suggestion> TokenizerSuggestions;
    public List<Suggestion> GreedyTokenized;

    public TransliterationResult(Suggestion[] e, Suggestion[] d, Suggestion[] p, Suggestion[] t, Suggestion[] g) {
        this.ExactMatches = Arrays.asList(e);
        this.DictionarySuggestions = Arrays.asList(d);
        this.PatternDictionarySuggestions = Arrays.asList(p);
        this.TokenizerSuggestions = Arrays.asList(t);
        this.GreedyTokenized = Arrays.asList(g);
    }
}
