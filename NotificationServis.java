package cz.johrusk.showsmscode;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Pepa on 27.03.2016.
 */
public class NotificationServis extends IntentService {

    public NotificationServis() {
        super("NotificationServis");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // Normally we would do some work here, like download a file.

        // For our sample, we just sleep for 5 seconds.
        String[] dataArray;

        Bundle bundle = intent.getExtras();
        dataArray = bundle.getStringArray("key");

        String smsContent = dataArray[0];
        String smsSender = dataArray[1];
//sd


        Log.d("TEST", "onHandleIntent");
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_account_balance_wallet)
                        .setContentTitle(smsSender)
                        .setContentText(smsContent);
        mBuilder.build() ;

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        int mId = 0;
        mNotificationManager.notify(mId, mBuilder.build());
    }
}