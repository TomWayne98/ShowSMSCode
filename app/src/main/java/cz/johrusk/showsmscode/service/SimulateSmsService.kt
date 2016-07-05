package cz.johrusk.showsmscode.service

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.intentFor


/**
 * Service which simulates what happen when user receive SMS which is contained in SMS.

 * @author Josef Hruska (pepa.hruska@gmail.com)
 */
class SimulateSmsService : IntentService("SimulateSmsService"),AnkoLogger {

    override fun onHandleIntent(intent: Intent) {

        val msgContent = "TEST code: 997456192"
        val msgSender = "123456"
        var msg = Bundle()

        debug("Content of SMS $msgContent / $msgSender")
        msg.putStringArray("msg", arrayOf(msgSender, msgContent))
        startService(intentFor<MsgHandlerService>("msg" to msg))
    }
}
