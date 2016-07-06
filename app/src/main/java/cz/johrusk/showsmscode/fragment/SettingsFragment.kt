package cz.johrusk.showsmscode.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import cz.johrusk.showsmscode.R
import cz.johrusk.showsmscode.sched.JobRunner
import es.dmoral.prefs.Prefs
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.ctx
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.toast


/**
 * Fragment for SettingsActivity
 *
 * @author Josef Hruska (pepa.hruska@gmail.com)
 **/

class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener, AnkoLogger {


    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        val DBVersion = Prefs.with(ctx).readInt("DBVersion", -1).toString()

        val prefVer = findPreference(KEY_PREF_VERSION)
        val prefDelay = findPreference(KEY_PREF_OVERLAY_DELAY)
        val delay = defaultSharedPreferences.getString(KEY_PREF_OVERLAY_DELAY, "5")
        prefDelay.summary = (delay + getString(R.string.SF_delay_summary_seconds))
        prefVer.summary = resources.getString(R.string.SF_summary_text)
        prefVer.title = resources.getString(R.string.versionDB_title) + DBVersion
        prefVer.setOnPreferenceClickListener {
            JobRunner.scheduleOnStartJob()
            toast(getString(R.string.SF_manual_update))
            true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_general)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == KEY_PREF_NOTIFICATION) {
            val notificationPref = findPreference(key)
            val b = sharedPreferences.getBoolean(key, false)
            // Set summary to be the user-description for the selected value
            if (b === true) {
                notificationPref.setSummary(R.string.SF_notification_summary_allowed)
            } else {
                notificationPref.setSummary(R.string.SF_notification_summary_denied)
            }
        }
        if (key == KEY_PREF_OVERLAY_DELAY) {
            val overlayDelayPref = findPreference(key)
            // Set summary to be the user-description for the selected value
            overlayDelayPref.summary = sharedPreferences.getString(key, "") + ctx.getString(R.string.SF_delay_summary_seconds)
        }
    }

    companion object {

        val KEY_PREF_OVERLAY_DELAY = "pref_overlayDelay"
        val KEY_PREF_NOTIFICATION = "pref_notification"
        val KEY_PREF_VERSION = "pref_versionUpdate"
    }

}
