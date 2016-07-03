package cz.johrusk.showsmscode.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import cz.johrusk.showsmscode.R
import cz.johrusk.showsmscode.fragment.SettingsFragment
import kotlinx.android.synthetic.main.settings_activity.*


/**
 * Settings activity

 * @author Josef Hruska (pepa.hruska@gmail.com)
 */

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
//        ActionBar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        // Display the fragment as the main content.
        fragmentManager.beginTransaction().replace(R.id.frame_content, SettingsFragment()).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item)
    }


}

