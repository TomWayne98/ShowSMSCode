package cz.johrusk.showsmscode.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import pl.tajchert.buswear.EventBus;

/**
 * Service which sends code and sender info to wear device
 *
 * @author Josef Hruska (pepa.hruska@gmail.com)
 */

public class WearService extends IntentService {
    final static String LOG_TAG = WearService.class.getSimpleName();

    public WearService() {
        super("WearService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context c = getApplicationContext();
        Bundle bundle = intent.getExtras();
        String[] Arr = new String[4];
        Arr = bundle.getStringArray("key");
        String codePlusSender = Arr[0] + "/" + Arr[2];
        Log.d(LOG_TAG,"Post remote sent ( " + codePlusSender + " )");
        EventBus.getDefault().postRemote(codePlusSender, c);
    }
}
