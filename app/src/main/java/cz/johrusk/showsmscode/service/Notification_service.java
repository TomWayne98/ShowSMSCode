package cz.johrusk.showsmscode.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.util.Random;

import cz.johrusk.showsmscode.R;


public class Notification_service extends IntentService {

    public Notification_service() {
        super("Notification_service");
    }

    /**
     * This method send notification which contain the code and number of sender.
     *
     * @param intent contain sms info (text, number)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        String[] dataArray = new String[4];
        Bundle bundle = intent.getExtras();
        dataArray = bundle.getStringArray("key");
        int length = dataArray.length;
        Log.d("TEST", String.valueOf(length));
        String notifType = dataArray[3];


        String smsContent = null;
        String smsSender = null;
        String[] notifWeeklyArr = getResources().getStringArray(R.array.notification_didyouknow);

        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        int nID = 0;
        switch (notifType) {
            case "notifCode":
                smsContent = dataArray[0];
                smsSender = dataArray[2];
                nID = 1;
                break;
            case "notifUpdate":
                Log.d("NOTIFICATION", "NotifUpdate start");
                smsSender = getString(R.string.NS_database_update);
                smsContent = getString(R.string.notif_newsDB) + dataArray[0];
                nID = 2;
                break;
            default:
                Crashlytics.log(1, "NOTIFICATION_SERVICE", "Switch statement didn't catch the case:" + notifType);
                Log.d("NOTIFICATION_SERVICE", "Switch statement didn't catch the case:" + notifType);

        }

        PendingIntent permissionIntent =
                PendingIntent.getActivity(this, 0, myAppSettings, 0);

        // Create a big text style for the second page
        android.support.v4.app.NotificationCompat.BigTextStyle secondPageStyle = new NotificationCompat.BigTextStyle();
        secondPageStyle.setBigContentTitle("Page 2")
                .bigText("A lot of text...");

        // Create second page notification
        Notification secondPageNotification =
                new NotificationCompat.Builder(this)
                        .setStyle(secondPageStyle)
                        .build();


        Log.d("TEST", "onHandleIntent");

        NotificationCompat.Builder notifBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_sms)
                        .setContentTitle(smsSender)
                        .setContentText(smsContent)
                        .setAutoCancel(true);

                        //.setSubText("Heya..........................")

                if (notifType.equals("notifPermission")) {
//                    notifBuilder.addAction(R.drawable.ic_security, getString(R.string.NS_notif_permission_actionIntent), permissionIntent);
                      notifBuilder.setContentIntent(permissionIntent);
                }


        Notification notif = notifBuilder
               // .extend(new NotificationCompat.WearableExtender()
                //        .addPage(secondPageNotification))
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        notificationManager.notify(nID, notif);


    }
}