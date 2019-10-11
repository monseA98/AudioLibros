package com.example.audiolibros.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.audiolibros.R;

public class PreferenciasFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }
}
