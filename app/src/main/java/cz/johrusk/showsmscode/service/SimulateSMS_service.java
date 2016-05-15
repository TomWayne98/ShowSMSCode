package cz.johrusk.showsmscode.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import cz.johrusk.showsmscode.activity.Main_activity;
import cz.johrusk.showsmscode.fragment.Settings_fragment;

/**
 * Created by Pepa on 14.05.2016.
 */
public class SimulateSMS_service extends IntentService {
    final static String LOG_TAG = Main_activity.class.getSimpleName();



    public SimulateSMS_service(){
        super("SimulateSMS_service");
}

    @Override
    protected void onHandleIntent(Intent intent) {
        Context c = getApplicationContext();
        String type = "notifCode";
        Bundle bundle = new Bundle();
        String codePHLD = "R2D2";
        String senderPHLD = "Skynet";
        bundle.putStringArray("key", new String[]{codePHLD,"true", senderPHLD, type});



        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        Boolean sendNotification = sharedPref.getBoolean(Settings_fragment.KEY_PREF_NOTIFICATION, true);
        // It will start service for sending notification if its allowed in settings
        if (sendNotification) {
            Intent notifIntent = new Intent(c, Notification_service.class);
            notifIntent.putExtras(bundle);
            c.startService(notifIntent);
        }
        if (Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(c)) {
            // Starts service for showing a code on the screen
            Intent overlayIntent = new Intent(c, Overlay_service.class);
            overlayIntent.putExtra("bundle", bundle);
            c.startService(overlayIntent);
            Log.d(LOG_TAG,"Overlay intent started");
        } else if (Build.VERSION.SDK_INT < 23)
        {
            Intent overlayIntent = new Intent(c, Overlay_service.class);
            overlayIntent.putExtra("bundle", bundle);
            c.startService(overlayIntent);
            Log.d(LOG_TAG,"Overlay intent started (SDK is lower than 23)");
        }
        else{
            Log.d(LOG_TAG, "Permission for overlay is not granted");
        }
        //Starts IntentService which sets sms code to clipboard;
        Intent clipIntent = new Intent(c, Clip_service.class);
        clipIntent.putExtra("code", codePHLD);
        c.startService(clipIntent);
    }
}
