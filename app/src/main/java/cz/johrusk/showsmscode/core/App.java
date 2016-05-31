package cz.johrusk.showsmscode.core;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.JobManager;

import io.fabric.sdk.android.Fabric;

/**
 * This class is used to initialize Fabric and JobManager
 */
public class App extends Application {
    private static App app;

    public static App get() {
        return app;
    }

    @Override
    public void onCreate() {
        app = this;

        super.onCreate();
        JobManager.create(this).addJobCreator(new JobCreator());
        Fabric.with(this, new Crashlytics());
    }


}