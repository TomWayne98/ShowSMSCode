package cz.johrusk.showsmscode.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import cz.johrusk.showsmscode.NotificationServis;
import cz.johrusk.showsmscode.OverlayService;

/**
 * Created by Pepa on 26.03.2016.
 */
public class SmsRecieved extends BroadcastReceiver {

    @Override
    public void onReceive(Context c, Intent intent) {
//        if (!Preferences.getBoolean(c, Preferences.EULA_CONFIRMED, false)) {
//            DebugLog.w("EULA not confirmed");
//            return;
//        }
        Log.i("ShowSMSApp","OnRecieveStarted");
        SmsMessage[] messages = getMessages(intent);
        try {
            for (SmsMessage message : messages) {
                try {
                    Log.i("ShowSMSApp","receiving sms from " + message.getDisplayOriginatingAddress());

                } catch (Exception e) {
                    // ignore
                }

                if (message != null) {
                    //TODO : Přidat metodu, která rozpozná zda je přijatá sms na listu.
                    boolean smsOnList = true;
                    // smsOnList = reckognizeSms(message)
                    Log.i("ShowSMSApp", "Content of SMS " + message.getDisplayMessageBody());
                    if (smsOnList)
                    {

                        Bundle bundle = new Bundle();
                        bundle.putStringArray("key", new String[]{message.getDisplayMessageBody(), message.getDisplayOriginatingAddress()});

                        Intent intent2 = new Intent(c, NotificationServis.class);
                        intent2.putExtras(bundle);
                        c.startService(intent2);

                        c.startService(new Intent(c, OverlayService.class));
                        //MainActivity.showNotif();
                    }
                }
            }
        } catch (ParseException e) {
            Log.e("ShowSMSApp","received sms cannot be parsed because of " + e.getMessage());
        }
    }

    /**
     * Creates array of <code>SmsMessage</code> from given intent.
     *
     * @param intent extracts messages from this intent
     * @return array of extracted messages. If no messages received, empty array is returned
     */
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
                Log.w("ShowSMSApp","SMS parsing forbidden");
                return new SmsMessage[0];
            }
        } else {
            return new SmsMessage[0];
        }
    }
}