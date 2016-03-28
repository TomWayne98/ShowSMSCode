package cz.johrusk.showsmscode.receiver;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import cz.johrusk.showsmscode.R;

/**
 * Created by Pepa on 28.03.2016.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_OVERLAY_DELAY = "pref_overlayDelay";
    public static final String KEY_PREF_NOTIFICATION = "pref_notification";

    /*

    Caution: When you call registerOnSharedPreferenceChangeListener(),
    the preference manager does not currently store a strong reference to the listener.
    You must store a strong reference to the listener, or it will be susceptible to garbage collection.
    We recommend you keep a reference to the listener in the instance data of an object that will exist as long as you need the listener.
     */


//    SharedPreferences.OnSharedPreferenceChangeListener listener =
//            new SharedPreferences.OnSharedPreferenceChangeListener() {
//                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
//                    // listener implementation
//                }
//            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_general);

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
        }
        if (key.equals(KEY_PREF_OVERLAY_DELAY)) {
            Preference overlayDelayPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            overlayDelayPref.setSummary(sharedPreferences.getString(key, ""));
        }
    }
}
