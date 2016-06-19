package cz.johrusk.showsmscode.core;

import android.app.Application;

import timber.log.Timber;

/**
 * Main application class.
 *
 * @author Josef Hruska (pepa.hruska@gmail.com)
 */
public class AppCore extends Application {
    private static AppCore app;


    public static AppCore get() {
        return app;
    }

    @Override
    public void onCreate() {
        Timber.plant(new Timber.DebugTree());
        Timber.d("AppCore on Create started");
        app = this;
        super.onCreate();

    }



}
