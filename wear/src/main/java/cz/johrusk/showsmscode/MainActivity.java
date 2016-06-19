package cz.johrusk.showsmscode;

import android.app.Activity;
import android.content.Intent;

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

    }
}
