package cz.johrusk.showsmscode.activity


import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import cz.johrusk.showsmscode.R
import cz.johrusk.showsmscode.fragment.MainFragment
import cz.johrusk.showsmscode.helpers.SimulateSms
import kotlinx.android.synthetic.main.main_activity.*
import org.jetbrains.anko.*

/**
 * MainActivity class
 *
 * @author Josef Hruska (pepa.hruska@gmail.com)
 */

class MainActivity : AppCompatActivity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false) // It will set the default preference on first run
        // Toolbar
        toolbar.setTitle(R.string.app_name)
        toolbar.setTitleTextColor(ContextCompat.getColor(ctx, R.color.textColorPrimary))
        setSupportActionBar(toolbar)

        supportFragmentManager.beginTransaction().replace(R.id.fragment, MainFragment()).commit()
    }

    override fun onStart() {
        super.onStart()
        debug("MainActivity state: onStart")
    }

    override fun onDestroy() {
        debug("MainActivity state: onDestroy")
        super.onDestroy()
    }

    override fun onStop() {
        debug("MainActivity state: onStop")
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                startActivity(intentFor<SettingsActivity>())// starts Settings activity
                return true
            }
            R.id.action_simulateSMS -> {
                SimulateSms.show(ctx)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /**
     * This method checks if  all permissions are granted
     * eventually it change state indicator to yellow (green circle)
     * If the circle is yellow, it can be clicked and user can allow system permission to app.
     */


    fun openBrowser(v: View) { // This method is called from .xml...
        var url: String? = null

        when (v.id) {
            R.id.MA_tv_addToGit -> url = getString(R.string.URL_AddSMS)
            R.id.MA_tv_reportIssue -> url = getString(R.string.URL_Issues)
            R.id.MA_tv_sourceCode -> url = getString(R.string.URL_GitHub)
            R.id.MA_tv_author -> url = getString(R.string.URL_LinkedIn)
        }
        browse(url as String) // All buttons have appropriate string url, it cant be null.
    }
}
