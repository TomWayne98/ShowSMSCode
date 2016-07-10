package cz.johrusk.showsmscode.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import cz.johrusk.showsmscode.R
import cz.johrusk.showsmscode.core.App
import kotlinx.android.synthetic.main.main_fragment.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.toast
import java.util.*

/**
 * Created by Pepa on 10.07.2016.
 */

class MainFragment : Fragment(), AnkoLogger{
    var permissionListener: PermissionListener? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Text underlinig
        MA_tv_addToGit.paintFlags += Paint.UNDERLINE_TEXT_FLAG
        MA_tv_author.paintFlags += Paint.UNDERLINE_TEXT_FLAG
        MA_tv_sourceCode.paintFlags += Paint.UNDERLINE_TEXT_FLAG
        MA_tv_reportIssue.paintFlags += Paint.UNDERLINE_TEXT_FLAG
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


            debug("MainActivity state: onStart")
            checkPermissionState()

            TedPermission(ctx) // Use after checkPermissionState
                    .setPermissionListener(permissionListener)
                    .setDeniedMessage(R.string.MA_permission_denied_dialog)
                    .setPermissions(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.RECEIVE_BOOT_COMPLETED)
                    .check()
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
            ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.SYSTEM_ALERT_WINDOW),
                    PERM_SHOW_WINDOWS)
        }
        // Device which has not granted all permissions (we get overlay-permission checked separately)
        if (isOK == false) {
            iv_state.setColorFilter(Color.YELLOW)
            tv_state.setText(R.string.MA_text_state_needperm)
            iv_state.setOnClickListener {
                val myAppSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package: ${ctx.packageName}")) // TODO: I guess this kind of intent can't be simplified with Kotlin/Anko...
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
                val myAppSettings = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package: ${ctx.packageName}"))
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
                myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivityForResult(myAppSettings, 1234)
            }

        }
        // Device with granted overlay permission
        if (isOK === true && ((!App.atleastMarshmallow()) || (App.atleastMarshmallow() && Settings.canDrawOverlays(ctx)))) {
            iv_state.setColorFilter(ctx.getColor(R.color.color_state))
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
    }
