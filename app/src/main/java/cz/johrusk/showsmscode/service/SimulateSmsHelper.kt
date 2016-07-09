package cz.johrusk.showsmscode.service

import android.os.Bundle


/**
 * Object which return fake content of SMS to simulate that sms with code has been received

 * @author Josef Hruska (pepa.hruska@gmail.com)
 */
object SimulateSmsHelper {

        fun getFakeContent() : Bundle {
            val msgContent = "TEST code: 997456192"
            val msgSender = "123456"
            val msg = Bundle()
//            debug("Content of SMS $msgContent / $msgSender")
            msg.putStringArray("msg", arrayOf(msgSender, msgContent))
            return msg
        }
}
