package cz.johrusk.showsmscode.helpers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import cz.johrusk.showsmscode.service.MsgHandlerService



/**
 * Object which return fake content of SMS to simulate that sms with code has been received

 * @author Josef Hruska (pepa.hruska@gmail.com)
 */
object SimulateSms {

        fun show(contx:Context){
            val msgContent = "TEST code: 997456192"
            val msgSender = "123456"
            val msg = Bundle()
//            debug("Content of SMS $msgContent / $msgSender")
            msg.putStringArray("msg", arrayOf(msgSender, msgContent))
            var intent:Intent = Intent(contx,MsgHandlerService::class.java)
            intent.putExtra("msg",msg)
            contx.startService(intent)
        }
}
