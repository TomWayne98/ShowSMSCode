package cz.johrusk.showsmscode

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Broadcast receiver which start Datalayerlistener when is Android Wear started
 *
 * @author Josef Hruska (pepa.hruska@gmail.com)
 */

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if ("android.intent.action.BOOT_COMPLETED" == intent.action) {
            val pushIntent = Intent(context, DataLayerListenerService::class.java)
            context.startService(pushIntent)
        }
    }
}
