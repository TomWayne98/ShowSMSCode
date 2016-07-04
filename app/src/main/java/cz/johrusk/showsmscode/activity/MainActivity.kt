package cz.johrusk.showsmscode.activity


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import cz.johrusk.showsmscode.R
import cz.johrusk.showsmscode.sched.JobRunner
import cz.johrusk.showsmscode.service.SimulateSmsService
import kotlinx.android.synthetic.main.main_activity.*
import org.jetbrains.anko.*
import java.util.*

/**
 * Helper class for running jobs which can be called from whole app.
 *
 * @author Josef Hruska (pepa.hruska@gmail.com)
 */

class MainActivity :  AppCompatActivity(), AnkoLogger {

    companion object{
        var isOK = true
        var permissionListener: PermissionListener? = null //PermissionListener

        //Jobs - time periods
        val UPDATE_24H:Long = (60 * 24)
        val UPDATE_12H = (UPDATE_24H) / 2
        val UPDATE_6H = (UPDATE_24H) / 4
        val UPDATE_1H = (UPDATE_24H) / 24
        val UPDATE_WEEK = (UPDATE_24H) * 7
        val UPDATE_DEBUG = (UPDATE_1H) / 60

//        val isMarshmallow = if (Build.VERSION.SDK_INT >= 23) {true}
//        else false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false) // It will set the default preference on first run
//        It schedule daily job if there is no scheduled daily job yet.
        JobRunner.scheduleJob(UPDATE_24H)
//        App name
        toolbar.setTitle(R.string.app_name)
        toolbar.setTitleTextColor(ContextCompat.getColor(ctx,R.color.textColorPrimary))
        setSupportActionBar(toolbar)
//        Text underlinig
        MA_tv_addToGit.paintFlags = MA_tv_reportIssue.paintFlags and  Paint.UNDERLINE_TEXT_FLAG
        MA_tv_author.paintFlags = MA_tv_author.paintFlags and  Paint.UNDERLINE_TEXT_FLAG
        MA_tv_sourceCode.paintFlags = MA_tv_sourceCode.paintFlags and  Paint.UNDERLINE_TEXT_FLAG
        MA_tv_reportIssue.paintFlags = MA_tv_reportIssue.paintFlags and  Paint.UNDERLINE_TEXT_FLAG
    }


    override fun onStart() {
        super.onStart()
        debug("MainActivity state: onStart")
        JobRunner.scheduleOnStartJob()
        checkPermissionState()

        TedPermission(this) // Use after checkPermissionState
                .setPermissionListener(permissionListener)
                .setDeniedMessage(R.string.MA_permission_denied_dialog)
                .setPermissions(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.RECEIVE_BOOT_COMPLETED)
                .check()
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
        // Inflate the menu; this adds items to the action bar if it is present.
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
                startService(intentFor<SimulateSmsService>())// Simulate receiving a SMS
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    /**
     * This method checks if  all permissions are granted
     * eventually it change state indicator to yellow (green circle)
     */
    // TODO: Improve permission handling UX + new logic
    fun checkPermissionState() {
        val PERM_SHOW_WINDOWS = 4

        if (ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.SYSTEM_ALERT_WINDOW),
                    PERM_SHOW_WINDOWS)
        }

        if (isOK == false) {
            iv_state.setColorFilter(Color.YELLOW)
            tv_state.setText(R.string.MA_text_state_needperm)
            iv_state.setOnClickListener {
                val myAppSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + packageName))
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
                myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(myAppSettings)
            }
        }
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(ctx)) {
            iv_state.setColorFilter(Color.YELLOW)
            tv_state.setText(R.string.MA_text_state_needperm)
            iv_state.setOnClickListener {
                val myAppSettings = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName))
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
                myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivityForResult(myAppSettings, 1234)
            }

        }
        if (isOK === true && Build.VERSION.SDK_INT < 23) {
            iv_state.setColorFilter(getColor(R.color.color_state))
            tv_state.setText(R.string.MA_text_state)
            iv_state.setOnClickListener { toast(R.string.MA_toast_permission_state_OK) }
        } else if (isOK === true && Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(ctx)) {
            iv_state.setColorFilter(getColor(R.color.color_state))
            tv_state.setText(R.string.MA_text_state)
            iv_state.setOnClickListener {toast(R.string.MA_toast_permission_state_OK) }
        }

        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                Toast.makeText(ctx, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
                isOK = false
            }

        }
        permissionListener = permissionlistener
    }

    fun openBrowser(v: View) { // This method is called from .xml...
        var url: String? = null

        when (v.id) {
            R.id.MA_tv_addToGit -> url = getString(R.string.URL_AddSMS)
            R.id.MA_tv_reportIssue -> url = getString(R.string.URL_Issues)
            R.id.MA_tv_sourceCode -> url = getString(R.string.URL_GitHub)
            R.id.MA_tv_author -> url = getString(R.string.URL_LinkedIn)
        }
        browse(url as String)
    }


}
