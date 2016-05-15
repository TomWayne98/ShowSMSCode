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
 * Helper class for SMS pattern testing
 */
@RunWith(MockitoJUnitRunner.class)
public class SmsParser {
    @Mock
    public Context c;

    public static String TestSMSPattern(String msg_content, String unique, String paternString) {
        Pattern patern = Pattern.compile(paternString);
        String code = null;
        Matcher matcher = patern.matcher(msg_content);
        Pattern pUnique = Pattern.compile(unique);
        Matcher mUnique = pUnique.matcher(msg_content);
        while (matcher.find() && mUnique.find()) {
            code = matcher.group(1);
        }
        return code;
    }
}


