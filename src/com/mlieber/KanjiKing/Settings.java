package com.mlieber.KanjiKing;

import android.preference.PreferenceActivity;
import android.app.Activity;
import android.os.Bundle;


public class Settings extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.settings);
    }
}

