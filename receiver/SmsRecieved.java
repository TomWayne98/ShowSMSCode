package cz.johrusk.showsmscode.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.johrusk.showsmscode.ClipService;
import cz.johrusk.showsmscode.MainActivity;
import cz.johrusk.showsmscode.NotificationServis;
import cz.johrusk.showsmscode.OverlayService;

/**
 * Created by Pepa on 26.03.2016.
 */
public class SmsRecieved extends BroadcastReceiver {

    public static final String LOG_TAG = MainActivity.class.getName();

    public Context c;

    @Override
    public void onReceive(Context c, Intent intent) {

        this.c = c;

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

                    String[] smsOnList;

                    smsOnList = recognizeSms(message.getDisplayOriginatingAddress());
                    Log.d(LOG_TAG,String.valueOf(smsOnList));
                    Log.i(LOG_TAG, "Content of SMS " + message.getDisplayMessageBody());
                    if (smsOnList != null) {

                        String code = "";

                        Pattern pUnique = Pattern.compile(smsOnList[0]);
                        Matcher mUnique = pUnique.matcher(message.getDisplayMessageBody());

                        if (mUnique.find()) {


                            Pattern p = Pattern.compile(smsOnList[2]);
                            Matcher matcher = p.matcher(message.getDisplayMessageBody());
                            while (matcher.find()) { // Find each match in turn; String can't do this.
                                code = matcher.group(1); // Access a submatch group; String can't do this.
                                Log.d(LOG_TAG, "code is: " + code);
                            }
                        }

                        else
                        {
                        Log.d(LOG_TAG,"unique text is not cointained in sms");
                        }

                        Bundle bundle = new Bundle();
                        bundle.putStringArray("key", new String[]{code, message.getDisplayOriginatingAddress(),smsOnList[1]});
                        // TODO - nastavit ověřování zda je tato možnost v nastavení povolena
                        // It will start service for sending notification
                        Intent notifIntent = new Intent(c, NotificationServis.class);
                        notifIntent.putExtras(bundle);
                        c.startService(notifIntent);

                        // Starts service for showing a code on the screen
                        Intent overlayIntent = new Intent(c, OverlayService.class);
                        overlayIntent.putExtra("bundle",bundle);
                        c.startService(overlayIntent);

                        //Starts IntentService which sets sms code to clipboard;
                        Intent clipIntent = new Intent(c, ClipService.class);
                        clipIntent.putExtra("code",code);
                        c.startService(clipIntent);



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

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = c.getAssets().open("SMSJson.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
public String[] recognizeSms(String msg){

    String[] results = new String[3];
    long number = Long.valueOf(msg);
    try {
        JSONObject obj = new JSONObject(loadJSONFromAsset());
        JSONArray m_jArry = obj.getJSONArray("sms");


        for (int i = 0; i < m_jArry.length(); i++) {
            JSONObject jo_inside = m_jArry.getJSONObject(i);
            Log.d("Details-->", jo_inside.getString("id"));
            int id_value = jo_inside.getInt("id");
            long number_value = jo_inside.getLong("number");
            JSONArray alt_numbers = jo_inside.getJSONArray("alt_numbers");
            String[] altnumbers_value = new String[50];

            results[0] = jo_inside.getString("unique");
            results[1] = jo_inside.getString("sender");
            results[2] = jo_inside.getString("reg_ex");


            if (number == number_value)
            {
                Log.d(LOG_TAG, "number was recognized");
                return results;
            }

            for (int x = 0; i < alt_numbers.length(); i++){
                altnumbers_value[x] = alt_numbers.getString(x);
                if (number == Long.valueOf(altnumbers_value[x])){
                   return results;
                }
            }
            Log.d(LOG_TAG, Long.toString(number_value));







//
        }

    } catch (JSONException e) {
        e.printStackTrace();
    }
    Log.d("LOG_TAG", "number is not in DB");

    return null;
}
}