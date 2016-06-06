package cz.johrusk.showsmscode.core;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import cz.johrusk.showsmscode.DataLayerListenerService;

/**
 * Created by Pepa on 27.05.2016.
 */
public class AppCore extends Application {
    private static AppCore app;
    final static String LOG_TAG = AppCore.class.getSimpleName();


    public static AppCore get() {
        return app;
    }

    @Override
    public void onCreate() {
        Log.d(LOG_TAG,"AppCore on Create started");

        app = this;

        super.onCreate();
        Intent startIntent = new Intent(this, DataLayerListenerService.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(startIntent);
        Log.d(LOG_TAG,"AppCore-onCREATED");

    }

    public  void onEvent(String se){
        Log.d(LOG_TAG,"AppCore-onevent" + se);
    }

}
