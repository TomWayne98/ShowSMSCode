package cz.johrusk.showsmscode.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import cz.johrusk.showsmscode.R;

/**
 * Settings class. All configurable stuff is here
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_OVERLAY_DELAY = "pref_overlayDelay";
    public static final String KEY_PREF_NOTIFICATION = "pref_notification";
    public static final String KEY_PREF_VERSION = "versionDB";
    SharedPreferences sharedpreferences;

    Context c = getActivity();

    public SettingsFragment(){}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_general);

        sharedpreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Preference pref = findPreference("pref_version_key");
        pref.setSummary(String.valueOf(sharedpreferences.getInt(getString(R.string.versionDB), 7)));
    }


    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_PREF_NOTIFICATION)) {
            Preference notificationPref = findPreference(key);
            Boolean b = sharedPreferences.getBoolean(key, false);
            String s = Boolean.toString(b);
            // Set summary to be the user-description for the selected value
            notificationPref.setSummary(s);

            Answers.getInstance().logCustom(new CustomEvent("Notification ON/OFF")
                    .putCustomAttribute("Set to:", s));

        }
        if (key.equals(KEY_PREF_OVERLAY_DELAY)) {
            Preference overlayDelayPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            overlayDelayPref.setSummary(sharedPreferences.getString(key, ""));

        }
    }

    Preference myPref = (Preference) findPreference("pref_version_key");


}
