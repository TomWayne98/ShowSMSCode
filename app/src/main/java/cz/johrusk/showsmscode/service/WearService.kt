package cz.johrusk.showsmscode.service

import android.app.IntentService
import android.content.Intent
import com.patloew.rxwear.GoogleAPIConnectionException
import com.patloew.rxwear.RxWear
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.ctx
import org.jetbrains.anko.debug

/**
 * Service which sends code and sender info to wear device

 * @author Josef Hruska (pepa.hruska@gmail.com)
 */

class WearService : IntentService("WearService"), AnkoLogger {

    override fun onHandleIntent(intent: Intent) {
        val bundle = intent.extras
        val Arr:Array<String> = bundle.getStringArray("key")
        val codePlusSender = Arr[0] + "/" + Arr[2]
        debug("Post remote sent ( $codePlusSender )")
        RxWear.init(ctx)
        RxWear.Message.SendDataMap.toAllRemoteNodes("/dataMap").putString("message", codePlusSender).toObservable().subscribe({ requestId -> }) { throwable ->
            if (throwable is GoogleAPIConnectionException) {
                debug("Android wear is not installed")
            } else {
                debug("Message was not send to wearable")
            }
        }
    }
}
