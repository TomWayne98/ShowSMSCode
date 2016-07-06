package cz.johrusk.showsmscode.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Handler
import android.os.IBinder
import android.preference.PreferenceManager
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import cz.johrusk.showsmscode.R
import cz.johrusk.showsmscode.fragment.SettingsFragment
import org.jetbrains.anko.*

/**
 * Service which shows code in windows which overlays all other apps.
 *
 * @author Josef Hruska (pepa.hruska@gmail.com)
 */

class OverlayService : Service(), AnkoLogger{
    override fun onBind(p0: Intent?): IBinder? {
        return null //Not used
    }

    companion object {
      var layout: View? = null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val bundle = intent.getBundleExtra("bundle")

        layout =  layoutInflater.inflate(R.layout.overlay_service,null)
        val dataArray: Array<String>
        dataArray = bundle.getStringArray("key")
        debug("Overlay Service - onStartCommand with" + dataArray[0] + " " + dataArray[2])
        val code = dataArray[0]
        val sender = dataArray[2]
        val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                PixelFormat.TRANSLUCENT)

        params.gravity = Gravity.CENTER or Gravity.TOP
        params.x = 0
        params.y = 0
        val tv_sender = layout!!.find<TextView>(R.id.tv_OS_sender)
        val tv_code = layout!!.find<TextView>(R.id.tv_OS_code)
        tv_sender.text = sender
        tv_code.text = code
        windowManager.addView(layout, params)

        return Service.START_STICKY

    }

    override fun onCreate() {
        super.onCreate()
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx)
        val i = sharedPref.getString(SettingsFragment.KEY_PREF_OVERLAY_DELAY, "")
        val overlayDelay = Integer.valueOf(i)!! * 1000
        debug("Handler started")
        Handler().postDelayed({ stopSelf() }, overlayDelay.toLong())
    }

    override fun onDestroy() {
        super.onDestroy()
        if (layout != null) windowManager.removeView(layout)
    }
}
