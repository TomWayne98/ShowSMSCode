package cz.johrusk.showsmscode.activity


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import cz.johrusk.showsmscode.R
import cz.johrusk.showsmscode.core.App
import cz.johrusk.showsmscode.service.MsgHandlerService
import cz.johrusk.showsmscode.service.SimulateSmsHelper
import kotlinx.android.synthetic.main.main_activity.*
import org.jetbrains.anko.*
import java.util.*

/**
 * MainActivity class
 *
 * @author Josef Hruska (pepa.hruska@gmail.com)
 */

class MainActivity : AppCompatActivity(), AnkoLogger {
    var permissionListener: PermissionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false) // It will set the default preference on first run
        // Toolbar
        toolbar.setTitle(R.string.app_name)
        toolbar.setTitleTextColor(ContextCompat.getColor(ctx, R.color.textColorPrimary))
        setSupportActionBar(toolbar)
        // Text underlinig
        MA_tv_addToGit.paintFlags +=  Paint.UNDERLINE_TEXT_FLAG
        MA_tv_author.paintFlags += Paint.UNDERLINE_TEXT_FLAG
        MA_tv_sourceCode.paintFlags +=  Paint.UNDERLINE_TEXT_FLAG
        MA_tv_reportIssue.paintFlags +=  + Paint.UNDERLINE_TEXT_FLAG
    }

    override fun onStart() {
        super.onStart()
        debug("MainActivity state: onStart")
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
                val msg = SimulateSmsHelper.getFakeContent()
                startService(intentFor<MsgHandlerService>("msg" to msg))
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
    fun checkPermissionState() {
        var isOK = true //State of permissions. - true means that all permissions (except overlaying) are granted.
        val PERM_SHOW_WINDOWS = 4


        if (ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.SYSTEM_ALERT_WINDOW),
                    PERM_SHOW_WINDOWS)
        }
        // Device which has not granted all permissions (we get overlay-permission checked separately)
        if (isOK == false) {
            iv_state.setColorFilter(Color.YELLOW)
            tv_state.setText(R.string.MA_text_state_needperm)
            iv_state.setOnClickListener {
                val myAppSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package: $packageName")) // TODO: I guess this kind of intent can't be simplified with Kotlin/Anko...
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
                myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(myAppSettings)
            }
        }
        // Marshmallow+ device with denied overlay-permission
        if (App.atleastMarshmallow() && !Settings.canDrawOverlays(ctx)) {
            iv_state.setColorFilter(Color.YELLOW)
            tv_state.setText(R.string.MA_text_state_needperm)
            iv_state.setOnClickListener {
                val myAppSettings = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package: $packageName"))
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
                myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivityForResult(myAppSettings, 1234)
            }
        }
        // Device with granted overlay permission
        if (isOK === true && ((!App.atleastMarshmallow()) || (App.atleastMarshmallow() && Settings.canDrawOverlays(ctx)))) {
            iv_state.setColorFilter(getColor(R.color.color_state))
            tv_state.setText(R.string.MA_text_state)
            iv_state.setOnClickListener { toast(R.string.MA_toast_permission_state_OK) }
        }

        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
            }
            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                toast("Permission Denied\n $deniedPermissions")
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
        browse(url as String) // All buttons have appropriate string url, it cant be null.
    }
}
