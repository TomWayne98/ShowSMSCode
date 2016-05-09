package cz.johrusk.showsmscode;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Pepa on 29.04.2016.
 */
public class Credits extends AppCompatActivity{

    @Override
    public void setContentView(View view) {
        super.setContentView(view);

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedpreferences;

        sharedpreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String dbVer = sharedpreferences.getString("pref_version_key", getString(R.string.CA_db_placeholder));
        setContentView(R.layout.credits_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSetting_CA);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        TextView tx2 = (TextView) findViewById(R.id.creditsA_appver2);
        tx2.setText(getResources().getString(R.string.action_craditsA_appVerTitle) + " " + BuildConfig.VERSION_NAME);
        TextView tx1 = (TextView) findViewById(R.id.creditsA_DBver2);
        tx1.setText(getResources().getString(R.string.action_craditsA_appDBTitle)+ " " + dbVer);
        TextView tx3 = (TextView) findViewById(R.id.creditsA_AppName);
        tx3.setText(R.string.app_name);


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
