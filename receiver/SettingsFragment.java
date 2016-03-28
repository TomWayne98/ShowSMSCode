package cz.johrusk.showsmscode.receiver;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import cz.johrusk.showsmscode.R;

/**
 * Created by Pepa on 28.03.2016.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_general);
    }
}
