package com.stbam.rssnewsreader.logic;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by Esteban on 11/15/2014.
 */
public class SugerenciasRecientes extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.stbam.rssnewsreader.logic.SugerenciasRecientes";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SugerenciasRecientes() {
        setupSuggestions(AUTHORITY, MODE);
    }
}