package cz.johrusk.showsmscode.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import cz.johrusk.showsmscode.R;
import cz.johrusk.showsmscode.sched.JobRunner;
import es.dmoral.prefs.Prefs;


/**
 * Fragment for SettingsActivity
 *
 * @author Josef Hruska (pepa.hruska@gmail.com)
 **/

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_OVERLAY_DELAY = "pref_overlayDelay";
    public static final String KEY_PREF_NOTIFICATION = "pref_notification";
    public static final String KEY_PREF_VERSION = "pref_versionUpdate";

    SharedPreferences sharedpreferences;
    Context c;

    public SettingsFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        c = getActivity();
        super.onActivityCreated(savedInstanceState);
        // Load the preferences from an XML resource

        int DBVersion = Prefs.with(c).readInt("DBVersion", -1);
        String str = String.valueOf(DBVersion);
        sharedpreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Preference pref = findPreference(KEY_PREF_VERSION);
        pref.setSummary(getResources().getString(R.string.SF_summary_text));
        pref.setTitle(getResources().getString(R.string.versionDB_title) + str);
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                JobRunner.scheduleOnStartJob();
                return false;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            if (b == true) {
                notificationPref.setSummary(R.string.SF_notification_summary_allowed);
            } else {
                notificationPref.setSummary(R.string.SF_notification_summary_denied);
            }
            Answers.getInstance().logCustom(new CustomEvent("Notification ON/OFF")
                    .putCustomAttribute("Set to:", s));
        }
        if (key.equals(KEY_PREF_OVERLAY_DELAY)) {
            Preference overlayDelayPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            overlayDelayPref.setSummary(sharedPreferences.getString(key, "") + c.getString(R.string.SF_delay_summary_seconds));
        }
    }

}
