package cz.johrusk.showsmscode.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;

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

import cz.johrusk.showsmscode.core.App;
import timber.log.Timber;

/**
 * Class which do most of the work after receiving the code.
 *
 * @author Josef Hruska (pepa.hruska@gmail.com
 */

public class MsgHandlerService extends IntentService {

    public Context c = App.Companion.get();
    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
    Boolean sendNotification = sharedPref.getBoolean("pref_notification", true);
    private JSONArray m_jArry;

    public MsgHandlerService() {
        super("MsgHandlerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle msgBundle = intent.getBundleExtra("msg");
        String[] msgArr = msgBundle.getStringArray("msg");

        String[] smsOnList;
        try {
            smsOnList = recognizeSms(msgArr[0], msgArr[1]);
            Timber.d(Arrays.toString(smsOnList));

            if (smsOnList != null) {

                String code = "";
                Timber.d("Unique text was recognized");

                Pattern p = Pattern.compile(smsOnList[2]);
                Matcher matcher = p.matcher(msgArr[1]);
                while (matcher.find()) { // Find each match in turn; String can't do this.
                    code = matcher.group(1); // Access a submatch group; String can't do this.
                    Timber.d("code is: " + code);
                }
                if (code != "" && code != null) {
                    String type = "notifCode";
                    Bundle bundle = new Bundle();
                    bundle.putStringArray("key", new String[]{code, msgArr[0], smsOnList[1], type});

                    // It will start service for sending notification if its allowed in settings
                    if (sendNotification) {
                        Intent notifIntent = new Intent(c, NotificationService.class);
                        notifIntent.putExtras(bundle);
                        c.startService(notifIntent);
                    }
                    // It send a content which can be displayed on wear device.
                    Intent wearIntent = new Intent(c, WearService.class);
                    wearIntent.putExtras(bundle);
                    c.startService(wearIntent);


                    if (Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(c)) {
                        // Starts service for showing a code on the screen
                        Intent overlayIntent = new Intent(c, OverlayService.class);
                        overlayIntent.putExtra("bundle", bundle);
                        c.startService(overlayIntent);
                        Timber.d("Overlay intent started");
                    } else if (Build.VERSION.SDK_INT < 23) {
                        Intent overlayIntent = new Intent(c, OverlayService.class);
                        overlayIntent.putExtra("bundle", bundle);
                        c.startService(overlayIntent);
                        Timber.d("Overlay intent started (SDK is lower than 23)");
                    } else {
                        Timber.d("Permission for overlay is not granted");
                    }
                    //Starts IntentService which sets sms code to clipboard;
                    Intent clipIntent = new Intent(c, ClipService.class);
                    clipIntent.putExtra("code", code);
                    c.startService(clipIntent);
                } else {
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
        Timber.d("Path of smsJSON: " + sms_file.getAbsolutePath());
        Timber.d("Path of versionJSON: " + version_file.getAbsolutePath());

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
     * @throws org.json.JSONException
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
        } catch (IOException e) {
        }
        Timber.d("readFrommFile return: " + ret);

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
        Timber.d(msg_content);

        if (mUnique.find()) {
            Timber.d("Unique text is contained in sms");
            return true;
        } else {
            Timber.d("Unique text is not contained in sms");
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
            Timber.d("edited number: " + number);
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
        Timber.d(p.toString());
        Matcher m = p.matcher(msg_sender_number);

        if (m.find()) {
            Timber.d("Sender adress is a name");
            return false;
        } else {
            Timber.d("Sender adress is a number");
            return true;
        }
    }

    /**
     * This method loads data from JSON DB and compares them with each sms.
     *
     * @param msg_sender_number
     * @param msg_content
     * @return
     * @throws org.json.JSONException
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
            Timber.d("Internal source will be used. Length of JSONArray: " + m_jArry.length());
        } else {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            m_jArry = obj.getJSONArray("sms");
            Timber.d("Assets source will be used. Length of JSONArray: " + m_jArry.length());
        }

        for (int i = 0; i < m_jArry.length(); i++) {
            Timber.d("i = " + i);
            JSONObject jo_inside = m_jArry.getJSONObject(i);
            Timber.d("id = " + jo_inside.getString("id"));
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
                    Timber.d("number was recognized");
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
                    Timber.d("number was recognized");
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
        Timber.d("number is not in DB");
        return null;
    }

}
