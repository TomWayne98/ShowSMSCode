package cz.johrusk.showsmscode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import timber.log.Timber;

/**
 * Created by Pepa on 19.06.2016.
 */

public class MainActivity extends Activity {
    @Override
    protected void onStart() {
        super.onStart();
        Intent startIntent = new Intent(this, DataLayerListenerService.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(startIntent);
        Timber.d("OnStart");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("OnCreate");
    }
}
