package cz.johrusk.showsmscode;

import android.app.Activity;
import android.os.Bundle;


import cz.johrusk.showsmscode.fragment.SettingsFragment;


public class Settings_activity extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Display the fragment as the main content.
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .commit();
        }

    }

