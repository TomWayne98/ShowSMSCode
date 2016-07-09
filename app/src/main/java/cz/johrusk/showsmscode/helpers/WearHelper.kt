package cz.johrusk.showsmscode.helpers

import android.content.Context
import android.os.Bundle
import com.patloew.rxwear.GoogleAPIConnectionException
import com.patloew.rxwear.RxWear
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug


/**
 * Object which sends code and sender info to wear device

 * @author Josef Hruska (pepa.hruska@gmail.com)
 */

object WearHelper : AnkoLogger {
        fun sentToWatch(bundle: Bundle, contx: Context){
        val Arr:Array<String> = bundle.getStringArray("key")
        val codePlusSender = Arr[0] + "/" + Arr[2]
        debug("Post remote sent ( $codePlusSender )")
        RxWear.init(contx)
        RxWear.Message.SendDataMap.toAllRemoteNodes("/dataMap").putString("message", codePlusSender).toObservable().subscribe({ requestId -> }) { throwable ->
            if (throwable is GoogleAPIConnectionException) {
                debug("Android wear is not installed")
            } else {
                debug("Message was not send to wearable")
            }
        }
    }
    }
