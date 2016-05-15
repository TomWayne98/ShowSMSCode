package cz.johrusk.showsmscode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;


import cz.johrusk.showsmscode.fragment.SettingsFragment;


public class Settings_activity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.settings_activity);
            Toolbar toolb = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolb);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

            FrameLayout frame = (FrameLayout) findViewById(R.id.frame_content);
            // Display the fragment as the main content.
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_content, new SettingsFragment())
                    .commit();
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


    }

