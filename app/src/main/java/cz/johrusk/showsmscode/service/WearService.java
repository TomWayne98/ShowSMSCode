package cz.johrusk.showsmscode.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.patloew.rxwear.GoogleAPIConnectionException;
import com.patloew.rxwear.RxWear;

import cz.johrusk.showsmscode.core.App;
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
        Context c = App.get();
        Bundle bundle = intent.getExtras();
        String[] Arr = new String[4];
        Arr = bundle.getStringArray("key");
        String codePlusSender = Arr[0] + "/" + Arr[2];
        Timber.d("Post remote sent ( " + codePlusSender + " )");
        RxWear.init(c);
        RxWear.Message.SendDataMap.toAllRemoteNodes("/dataMap")
                .putString("message", codePlusSender)
                .toObservable()
                .subscribe(requestId -> {
                        }, throwable -> {
                            if (throwable instanceof GoogleAPIConnectionException) {
                                Timber.d("Android wear is not installed");
                            } else {
                                Timber.d("Message was not send to wearable");
                            }
                        }
                );
    }
}
