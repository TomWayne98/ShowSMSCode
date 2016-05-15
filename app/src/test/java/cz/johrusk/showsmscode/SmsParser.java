package cz.johrusk.showsmscode;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.johrusk.showsmscode.activity.Main_activity;

/**
 * Created by Pepa on 02.04.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class SmsParser{
    @Mock
    public Context c;
    public static final String LOG_TAG = Main_activity.class.getName();


    public Sms parse(long number, String smsText){
        String[] smsContent = getSmsContent(number, smsText);
        Sms sms = new Sms();
        sms.unique = smsContent[0];
        sms.sender = smsContent[1];
        sms.number = smsContent[2];
        sms.code = smsContent[3];

        return sms;

    }


//    public String loadJSONFromAsset() {

//        String json = null;
//        try {
//            try {
//                InputStream is = c.getAssets().open("SMSJson.json");
//
//
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read(buffer);
//            is.close();
//            json = new String(buffer, "UTF-8");
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            return null;
//        }
//        return json;
//        }
//        catch (NullPointerException n){
//            throw new RuntimeException("Null poitttnter exception");}
//    }

    private String readResult(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is, "utf-8"), 1000);
        String l = null;
        while ((l = r.readLine()) != null) {
            sb.append(l);
        }

        return sb.toString();
    }



    public String[] getSmsContent ( long number, String smsText) {
            String[] results = new String[5];
            InputStream is = null;
            try {
                is = ClassLoader.getSystemResourceAsStream("assets/SMSJson.json");
                String strg = readResult(is);
                JSONObject obj = new JSONObject(strg);
                JSONArray m_jArry = obj.getJSONArray("sms");


                for (int i = 0; i < m_jArry.length(); i++) {
                    JSONObject jo_inside = m_jArry.getJSONObject(i);


                    long number_value = jo_inside.getLong("number");
                    if (number_value == number) {
                        JSONArray alt_numbers = jo_inside.getJSONArray("alt_numbers");
                        String[] altnumbers_value = new String[50];
                        String code = "";

                        Pattern p = Pattern.compile(jo_inside.getString("reg_ex"));
                        Matcher m = p.matcher(smsText);
                        while (m.find()) { // Find each match in turn; String can't do this.
                            code = m.group(1); // Access a submatch group; String can't do this.
                            Log.d(LOG_TAG, "code is: " + code);
                        }


                        results[0] = jo_inside.getString("unique");
                        results[1] = jo_inside.getString("sender");
                        results[2] = jo_inside.getString("number");
                        results[3] = code;
                        return results;
                    }
                }

            } catch (IOException | JSONException e) {
                Log.e("ShowSMSApp", "received sms cannot be parsed because of " + e.getMessage());
            }

        return results;
        }
}




