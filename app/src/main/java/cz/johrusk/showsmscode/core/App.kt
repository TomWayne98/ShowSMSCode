package cz.johrusk.showsmscode.core

import android.app.Application
import com.evernote.android.job.JobManager
import cz.johrusk.showsmscode.sched.JobCreator

/**
 * Main application class

 * @author Josef Hruska (pepa.hruska@gmail.com)
 */
class App : Application() {

    override fun onCreate() {
        app = this
        super.onCreate()
        JobManager.create(this).addJobCreator(JobCreator()) // Job manager init
    }

    companion object {
        private var app: App? = null

        fun get(): App {
            return app as App
        }
    }
}