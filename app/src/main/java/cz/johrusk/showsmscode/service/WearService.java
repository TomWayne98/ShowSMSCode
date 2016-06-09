package cz.johrusk.showsmscode.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import pl.tajchert.buswear.EventBus;
import timber.log.Timber;

/**
 * Service which sends code and sender info to wear device
 *
 * @author Josef Hruska (pepa.hruska@gmail.com)
 */

public class WearService extends IntentService {

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
       Timber.d("Post remote sent ( " + codePlusSender + " )");
        EventBus.getDefault().postRemote(codePlusSender, c);
    }
}
