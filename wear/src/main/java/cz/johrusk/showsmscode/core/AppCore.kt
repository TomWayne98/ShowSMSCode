package cz.johrusk.showsmscode.core

import android.app.Application
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn

/**
 * Main application class.

 * @author Josef Hruska (pepa.hruska@gmail.com)
 */
class AppCore : Application(), AnkoLogger {

    override fun onCreate() {
        warn("AppCore on Create started")
        super.onCreate()
    }
}


