package cz.johrusk.showsmscode.service

import android.app.IntentService
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent

/**
 * Service which copy code so it can be pasted.

 * @author Josef Hruska (pepa.hruska@gmail.com)
 */
class ClipService : IntentService("ClipService") {

    override fun onHandleIntent(intent: Intent) {
        val str = intent.getStringExtra("code")

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("code", str)
        clipboard.primaryClip = clip
    }
}
