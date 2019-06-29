package com.example.myhello.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.example.myhello.R;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection deprecation
        addPreferencesFromResource(R.xml.preference);

    }
}
