package com.mlieber.KanjiKing.Activity;

import android.preference.PreferenceActivity;
import android.app.Activity;
import android.os.Bundle;
import com.mlieber.KanjiKing.R;


public class Settings extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.settings);
    }
}
