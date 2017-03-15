package com.example.szymek.shopping;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        setPreferences();
    }

    private void setPreferences() {
        // Set default values, so we don't get null
        ListPreference color = (ListPreference) findPreference ("text_color_preference");
        if(color.getValue() == null)
            color.setValueIndex(0);
        ListPreference size = (ListPreference) findPreference ("text_size_preference");
        if(size.getValue() == null)
            size.setValueIndex(0);

        color.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.color), newValue.toString());
                editor.commit();
                Toast.makeText(getActivity().getApplicationContext(),
                        newValue.toString(),
                        Toast.LENGTH_SHORT);
                return true;
            }
        });
        size.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.size), newValue.toString());
                editor.commit();
                Toast.makeText(getActivity().getApplicationContext(),
                        newValue.toString(),
                        Toast.LENGTH_SHORT);
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        container.removeAllViews();
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}