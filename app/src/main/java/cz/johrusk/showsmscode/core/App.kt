package cz.johrusk.showsmscode.core

import android.app.Application
import android.os.Build
import com.evernote.android.job.JobManager
import cz.johrusk.showsmscode.sched.JobHandler
import cz.johrusk.showsmscode.sched.JobRunner

/**
 * Main application class

 * @author Josef Hruska (pepa.hruska@gmail.com)
 */
class App : Application() {
    val UPDATE_24H: Long = (60 * 24)


    override fun onCreate() {
        app = this
        super.onCreate()

        JobManager.create(this).addJobCreator(JobHandler()) // Job manager init
        JobRunner.scheduleJob(UPDATE_24H)
        JobRunner.scheduleOnStartJob()
    }


    companion object {
        private var app: App? = null
        fun  atleastMarshmallow() : Boolean {
            if (Build.VERSION.SDK_INT >= 23) return true
            else return false
        }

        fun get(): App {
            return app!! //App is created as very first. It can't be null when is it called
        }
    }
}