package cz.johrusk.showsmscode.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;


import cz.johrusk.showsmscode.R;
import cz.johrusk.showsmscode.fragment.Settings_fragment;


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
                .replace(R.id.frame_content, new Settings_fragment())
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

