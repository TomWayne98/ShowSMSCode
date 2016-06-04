package cz.johrusk.showsmscode.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.SmsMessage;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.johrusk.showsmscode.activity.Main_activity;
import cz.johrusk.showsmscode.service.Clip_service;
import cz.johrusk.showsmscode.service.Notification_service;
import cz.johrusk.showsmscode.service.Overlay_service;
import cz.johrusk.showsmscode.fragment.Settings_fragment;
import cz.johrusk.showsmscode.service.WearService;
import io.fabric.sdk.android.Fabric;

/**
 * This class check received SMS.
 */
public class Sms_reciever extends BroadcastReceiver {

    public static final String LOG_TAG = Main_activity.class.getName();

    public Context c;
    private JSONArray m_jArry;

    @Override
    public void onReceive(Context c, Intent intent) {
        Fabric.with(c, new Crashlytics());
        this.c = c;


        //Check whether is sending of notification allowed in settings (default value is true)
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        Boolean sendNotification = sharedPref.getBoolean(Settings_fragment.KEY_PREF_NOTIFICATION, true);
        Boolean showOnWearDevice = sharedPref.getBoolean(Settings_fragment.KEY_PREF_WEAR_DEVICE, true);

        Log.i(LOG_TAG, "OnRecieveStarted");
        SmsMessage[] messages = getMessages(intent);
        try {
            for (SmsMessage message : messages) {
                try {
                    Log.i("ShowSMSApp", "receiving sms from " + message.getDisplayOriginatingAddress());

                } catch (Exception e) {
                    Crashlytics.logException(e);
                }

                if (message != null) {

                    String[] smsOnList;
                    smsOnList = recognizeSms(message.getDisplayOriginatingAddress(), message.getMessageBody());
                    Log.d(LOG_TAG, Arrays.toString(smsOnList));
                    Log.i(LOG_TAG, "Content of SMS " + message.getMessageBody());
                    if (smsOnList != null) {

                        String code = "";
                        Log.d(LOG_TAG, "Unique text was recognized");

                        Pattern p = Pattern.compile(smsOnList[2]);
                        Matcher matcher = p.matcher(message.getMessageBody());
                        while (matcher.find()) { // Find each match in turn; String can't do this.
                            code = matcher.group(1); // Access a submatch group; String can't do this.
                            Log.d(LOG_TAG, "code is: " + code);
                        }
//                        Answers.getInstance().logCustom(new CustomEvent("SMS from DB received")
//                                .putCustomAttribute("Code / Sender", code + "  " + smsOnList[1]));
                        if (code != "" && code != null) {
                            String type = "notifCode";
                            Bundle bundle = new Bundle();
                            bundle.putStringArray("key", new String[]{code, message.getDisplayOriginatingAddress(), smsOnList[1], type});

                            // It will start service for sending notification if its allowed in settings
                            if (sendNotification) {
                                Intent notifIntent = new Intent(c, Notification_service.class);
                                notifIntent.putExtras(bundle);
                                c.startService(notifIntent);
                            }
                            // It send a content which can be displayed on wear device. (If it is allowed in prefs...)
                            if (showOnWearDevice) {
                                Intent wearIntent = new Intent(c, WearService.class);
                                wearIntent.putExtras(bundle);
                                c.startService(wearIntent);
                            }

                            if (Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(c)) {
                                // Starts service for showing a code on the screen
                                Intent overlayIntent = new Intent(c, Overlay_service.class);
                                overlayIntent.putExtra("bundle", bundle);
                                c.startService(overlayIntent);
                                Log.d(LOG_TAG, "Overlay intent started");
                            } else if (Build.VERSION.SDK_INT < 23) {
                                Intent overlayIntent = new Intent(c, Overlay_service.class);
                                overlayIntent.putExtra("bundle", bundle);
                                c.startService(overlayIntent);
                                Log.d(LOG_TAG, "Overlay intent started (SDK is lower than 23)");
                            } else {
                                Log.d(LOG_TAG, "Permission for overlay is not granted");
                            }
                            //Starts IntentService which sets sms code to clipboard;
                            Intent clipIntent = new Intent(c, Clip_service.class);
                            clipIntent.putExtra("code", code);
                            c.startService(clipIntent);
                        } else {
                            Answers.getInstance().logCustom(new CustomEvent("Code is null/\"\"")
                                    .putCustomAttribute("Sender / SMS Body", message.getDisplayMessageBody() + "  " + message.getDisplayOriginatingAddress()));
                        }
                    }
                }
            }
        } catch (ParseException | JSONException e) {
            Crashlytics.logException(e);
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
                Crashlytics.logException(e);
                return new SmsMessage[0];
            }
        } else {
            return new SmsMessage[0];
        }
    }

    /**
     * This method checks whether both sms and version file is located in internal storage
     *
     * @return true == both sms and version file is located in internal storage, yet. false == either sms or version isn't located in internal storage.
     */
    public Boolean checkStorage() {
        final String SMS_FILE = "/sms.txt";
        final String VERSION_FILE = "/version.txt";

        final String INTERNAL_PATH_SMS = c.getFilesDir().getPath() + SMS_FILE;
        final String INTERNAL_PATH_VERSION = c.getFilesDir().getPath() + VERSION_FILE;

        File version_file = new File(INTERNAL_PATH_VERSION);
        File sms_file = new File(INTERNAL_PATH_SMS);
        Log.d(LOG_TAG, "Path of smsJSON: " + sms_file.getAbsolutePath());
        Log.d(LOG_TAG, "Path of versionJSON: " + version_file.getAbsolutePath());

        if (sms_file.exists() && version_file.exists()) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * This method loads sms file from internal storage
     *
     * @return sms file in form of String
     * @throws JSONException
     */
    private String loadJSONFromInternal() throws JSONException {
        String ret = "";
        String file = "sms.txt";
        try {
            InputStream inputStream = c.openFileInput(file);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
            Crashlytics.log("File not found");
        } catch (IOException e) {
            Crashlytics.logException(e);
            Crashlytics.log("Cannot read file");
        }
        Log.d(LOG_TAG, "readFrommFile return: " + ret);

        return ret;
    }

    /**
     * This method loads sms file from internal storage.
     *
     * @return sms file in form of String
     */
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = c.getAssets().open("sms.json");
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

    /**
     * This method checks whether is unique text in sms
     *
     * @param msg_content text of sms
     * @param results     Array with information in sms
     * @return true == "Unique text is contained in sms" false == "Unique text is not contained in sms"
     */
    public Boolean containUnique(String msg_content, String[] results) {

        String code = "";

        Pattern pUnique = Pattern.compile(results[0]);
        Matcher mUnique = pUnique.matcher(msg_content);
        Log.d(LOG_TAG, msg_content);

        if (mUnique.find()) {
            Log.d(LOG_TAG, "Unique text is contained in sms");
            return true;
        } else {
            Log.d(LOG_TAG, "Unique text is not contained in sms");
            return false;
        }
    }

    /**
     * This method removes the "Precode" (I have no idea what's the exact word for that) in number. f.e.: +420123456789 will be changed to 123456789
     *
     * @param msg_sender_number original number
     * @return changed number
     */
    public long removePreCode(String msg_sender_number) {
        String pattern = "\\+(\\d..)";
        Pattern patt = Pattern.compile(pattern);
        Matcher mat = patt.matcher(msg_sender_number);
        if (mat.find()) {
            long number = Long.valueOf(mat.replaceFirst(""));
            Log.d(LOG_TAG, "edited number: " + number);
            return number;
        }
        return Long.valueOf(msg_sender_number);

    }

    /**
     * This method return false if the getDisplayOriginatingAddress is name of contact (f.e. "Notify";"Google Authenticator")
     * otherwise returns true which means it is a standard number (756453112)
     *
     * @param msg_sender_number
     * @return
     */
    public Boolean numberOrName(String msg_sender_number) {

        Pattern p = Pattern.compile("[^0123456789+]");
        Log.d(LOG_TAG, p.toString());
        Matcher m = p.matcher(msg_sender_number);

        if (m.find()) {
            Log.d(LOG_TAG, "Sender adress is a name");
            return false;
        } else {
            Log.d(LOG_TAG, "Sender adress is a number");
            return true;
        }
    }

    /**
     * This method loads data from JSON DB and compares them with each sms.
     *
     * @param msg_sender_number
     * @param msg_content
     * @return
     * @throws JSONException
     */
    public String[] recognizeSms(String msg_sender_number, String msg_content) throws JSONException {

        String[] results = new String[3];
        String name = null;
        long number = 0;
        String name_value = null;
        long number_value = 0;
        JSONArray alt_numbers = null;

        Boolean isNumber = numberOrName(msg_sender_number);
        if (!isNumber) {
            name = msg_sender_number;
        } else {
            number = removePreCode(msg_sender_number);
        }

        if (checkStorage()) {
            String str = loadJSONFromInternal();
            JSONObject obj = new JSONObject(str);
            m_jArry = obj.getJSONArray("sms");
            Log.d(LOG_TAG, "Internal source will be used. Length of JSONArray: " + m_jArry.length());
        } else {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            m_jArry = obj.getJSONArray("sms");
            Log.d(LOG_TAG, "Assets source will be used. Length of JSONArray: " + m_jArry.length());
        }

        for (int i = 0; i < m_jArry.length(); i++) {
            Log.d(LOG_TAG, "i = " + i);
            JSONObject jo_inside = m_jArry.getJSONObject(i);
            Log.d("Details-->", jo_inside.getString("id"));
            int id_value = jo_inside.getInt("id");
            if (id_value < 1000) {
                number_value = jo_inside.getLong("number");
            } else {
                name_value = jo_inside.getString("number");
            }
            String[] altnumbers_value = new String[50];

            results[0] = jo_inside.getString("unique");
            results[1] = jo_inside.getString("sender");
            results[2] = jo_inside.getString("reg_ex");
            if (id_value < 1000 && number != 0) {
                if (number == number_value && containUnique(msg_content, results)) {
                    Log.d(LOG_TAG, "number was recognized");
                    return results;
                }
                if (jo_inside.has("alt_numbers")) {
                    alt_numbers = jo_inside.getJSONArray("alt_numbers");
                    for (int x = 0; x < alt_numbers.length(); x++) {
                        altnumbers_value[x] = alt_numbers.getString(x);
                        if (number == Long.valueOf(altnumbers_value[x]) && containUnique(msg_content, results)) {
                            return results;
                        }
                    }
                }
            } else if (name != null) {
                if (name.equals(name_value) && containUnique(msg_content, results)) {
                    Log.d(LOG_TAG, "number was recognized");
                    return results;
                }
                if (jo_inside.has("alt_numbers")) {
                    alt_numbers = jo_inside.getJSONArray("alt_numbers");
                    for (int x = 0; x < alt_numbers.length(); x++) {
                        altnumbers_value[x] = alt_numbers.getString(x);
                        if (name.equals(altnumbers_value[x]) && containUnique(msg_content, results)) {
                            return results;
                        }
                    }
                }
            }
        }

        Log.d(LOG_TAG, "number is not in DB");
        return null;
    }
}