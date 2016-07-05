package cz.johrusk.showsmscode

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.warn

/**
 * Created by Pepa on 19.06.2016.
 */

class MainActivity : Activity(),AnkoLogger {
    override fun onStart() {
        super.onStart()
        startService(intentFor<DataLayerListenerService>().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        warn("OnStart")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        warn("OnCreate")
    }
}
