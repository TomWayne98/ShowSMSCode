package cz.johrusk.showsmscode.service

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import org.jetbrains.anko.ctx
import timber.log.Timber


/**
 * Service which simulates what happen when user receive SMS which is contained in SMS.

 * @author Josef Hruska (pepa.hruska@gmail.com)
 */
class SimulateSmsService : IntentService("SimulateSmsService") {

    override fun onHandleIntent(intent: Intent) {

        val msgHandler = Intent(ctx, MsgHandlerService::class.java)
        val msgContent = "TEST code: 997456192"
        val msgSender = "123456"
        var msg = Bundle()

        Timber.d("Content of SMS $msgContent / $msgSender")
        msg.putStringArray("msg", arrayOf(msgSender, msgContent))
        msgHandler.putExtra("msg", msg)
        ctx.startService(msgHandler)
    }
}
