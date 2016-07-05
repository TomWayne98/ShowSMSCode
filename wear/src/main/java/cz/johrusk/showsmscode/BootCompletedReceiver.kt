package cz.johrusk.showsmscode

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by Pepa on 19.06.2016.
 */

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if ("android.intent.action.BOOT_COMPLETED" == intent.action) {
            val pushIntent = Intent(context, DataLayerListenerService::class.java)
            context.startService(pushIntent)
        }
    }
}
