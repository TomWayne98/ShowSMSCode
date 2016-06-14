package cz.johrusk.showsmscode.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import cz.johrusk.showsmscode.service.MsgHandlerService;
import timber.log.Timber;

/**
 * Class which checks received SMS.
 *
 * @author Josef Hruska (pepa.hruska@gmail.com)
 */
public class SmsReceiver extends BroadcastReceiver {

    public Context c;

    @Override
    public void onReceive(Context c, Intent intent) {
        this.c = c;
        Timber.d("SMS RECEIVED");
        SmsMessage[] messages = getMessages(intent);
        for (SmsMessage message : messages) {
            try {
                Timber.d("receiving sms from " + message.getDisplayOriginatingAddress());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (message != null) {

                Intent msgHandler = new Intent(c, MsgHandlerService.class);
                String msgContent = message.getMessageBody();
                String msgSender = message.getDisplayOriginatingAddress();
                Bundle msg = new Bundle();

                Timber.d("Content of SMS " + msgContent + " / " + msgSender);
                msg.putStringArray("msg", new String[]{msgSender, msgContent});
                msgHandler.putExtra("msg", msg);
                c.startService(msgHandler);
            }
        }
    }

    public synchronized SmsMessage[] getMessages(Intent intent) {
        Bundle bundle = intent.getExtras();

        Object messages[] = (Object[]) bundle.get("pdus");
        if (messages != null) {
            try {
                SmsMessage[] smsMessages = new SmsMessage[messages.length];
                for (int i = 0; i < messages.length; i++) {
                    smsMessages[i] = SmsMessage.createFromPdu((byte[]) messages[i]);
                }
                return smsMessages;
            } catch (SecurityException e) {
                return new SmsMessage[0];
            }
        } else {
            return new SmsMessage[0];
        }
    }
}