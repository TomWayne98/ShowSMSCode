package cz.johrusk.showsmscode.core;

import android.app.Application;
import android.content.Intent;

import cz.johrusk.showsmscode.DataLayerListenerService;
import timber.log.Timber;

/**
 * Main application class.
 *
 * @author Josef Hruska (pepa.hruska@gmail.com)
 */
public class AppCore extends Application {
    private static AppCore app;
    final static String LOG_TAG = AppCore.class.getSimpleName();



    public static AppCore get() {
        return app;
    }

    @Override
    public void onCreate() {
        Timber.plant(new Timber.DebugTree());
        Timber.d("AppCore on Create started");

        app = this;
        super.onCreate();
        Intent startIntent = new Intent(this, DataLayerListenerService.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(startIntent);
        Timber.d("AppCore-onCREATED");

    }



}
