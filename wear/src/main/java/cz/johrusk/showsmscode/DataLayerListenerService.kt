package cz.johrusk.showsmscode

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.android.gms.wearable.MessageApi
import com.patloew.rxwear.RxWear
import com.patloew.rxwear.transformers.MessageEventGetDataMap
import org.jetbrains.anko.*

/**
 * Service which receives String containing code and sender from WearActivity.

 * @author Josef Hruska (pepa.hruska@gmail.com)
 */
class DataLayerListenerService : Service(), AnkoLogger {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        RxWear.init(this)
        RxWear.Message.listen("/dataMap", MessageApi.FILTER_LITERAL).compose(MessageEventGetDataMap.noFilter()).subscribe { dataMap ->
            val message = dataMap.getString("message", getString(R.string.no_message_info))
            warn("Message received: " + message)
            if (message != "no_msg") {
                startActivity(intentFor<ShowActivity>("code" to message).newTask())
                stopSelf()
            }
        }
        debug("DataLayer started")
        return super.onStartCommand(intent, flags, startId)
    }
}


