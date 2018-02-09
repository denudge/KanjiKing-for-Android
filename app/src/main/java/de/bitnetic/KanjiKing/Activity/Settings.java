package de.bitnetic.KanjiKing.Activity;

import android.preference.PreferenceActivity;
import android.app.Activity;
import android.os.Bundle;
import de.bitnetic.KanjiKing.R;


public class Settings extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.settings);
    }
}
