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

import cz.johrusk.showsmscode.activity.MainActivity;
import cz.johrusk.showsmscode.fragment.SettingsFragment;


/**
 * This method simulates what happen when user receive SMS which is contained in SMS.
 */
public class SimulateSmsService extends IntentService {
    final static String LOG_TAG = MainActivity.class.getSimpleName();


    public SimulateSmsService() {
        super("SimulateSmsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context c = getApplicationContext();
        String type = "notifCode";
        Bundle bundle = new Bundle();
        String codePHLD = "C0DE";

        String senderPHLD = "Sender";
        bundle.putStringArray("key", new String[]{codePHLD, "true", senderPHLD, type});
        Log.d(LOG_TAG, "TEST");


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        Boolean sendNotification = sharedPref.getBoolean(SettingsFragment.KEY_PREF_NOTIFICATION, true);
        // It will start service for sending notification if its allowed in settings
        if (sendNotification) {
            Intent notifIntent = new Intent(c, NotificationService.class);
            notifIntent.putExtras(bundle);
            c.startService(notifIntent);
        }
        if (Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(c)) {
            // Starts service for showing a code on the screen
            Intent overlayIntent = new Intent(c, OverlayService.class);
            overlayIntent.putExtra("bundle", bundle);
            c.startService(overlayIntent);
            Log.d(LOG_TAG, "Overlay intent started");
        } else if (Build.VERSION.SDK_INT < 23) {
            Intent overlayIntent = new Intent(c, OverlayService.class);
            overlayIntent.putExtra("bundle", bundle);
            c.startService(overlayIntent);
            Log.d(LOG_TAG, "Overlay intent started (SDK is lower than 23)");
        } else {
            Log.d(LOG_TAG, "Permission for overlay is not granted");
        }
        //Starts IntentService which sets sms code to clipboard
        Intent clipIntent = new Intent(c, ClipService.class);
        clipIntent.putExtra("code", codePHLD);
        c.startService(clipIntent);

        Intent wearIntent = new Intent(c, WearService.class);
        wearIntent.putExtras(bundle);
        c.startService(wearIntent);
    }
}
