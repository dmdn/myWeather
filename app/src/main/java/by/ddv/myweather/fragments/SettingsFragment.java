package by.ddv.myweather.fragments;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import by.ddv.myweather.R;



public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences preferences;

    private SwitchPreference geolocationEnabledPreference;
    private EditTextPreference manualLocationPreference;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        geolocationEnabledPreference = (SwitchPreference) findPreference(getString(R.string.pref_geolocation_enabled));
        manualLocationPreference = (EditTextPreference) findPreference(getString(R.string.pref_manual_location));

        bindPreferenceSummaryToValue(manualLocationPreference);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_unit_format)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_number_days)));

        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
        onSharedPreferenceChanged(null, null);

        if(!preferences.getBoolean(getString(R.string.pref_needs_setup), false)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(getString(R.string.pref_needs_setup), false);
            editor.apply();
        }

        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(Color.parseColor("#d7d7d7"));
        return view;
    }


    public void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference, preferences.getString(preference.getKey(), null));
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

        } else if (preference instanceof EditTextPreference) {
            preference.setSummary(stringValue);
        }

        return true;
    }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (geolocationEnabledPreference.isChecked()) {
            manualLocationPreference.setEnabled(false);
        } else {
            manualLocationPreference.setEnabled(true);
        }
    }




}
