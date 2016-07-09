package cz.johrusk.showsmscode.sched

import com.evernote.android.job.JobCreator

/**
 * Class which creates scheduled job
 * @author Josef Hruska (pepa.hruska@gmail.com)
 */
class JobHandler : JobCreator {


    override fun create(tag: String): com.evernote.android.job.Job? {
        when (tag) {
            UpdateJob.TAG -> return UpdateJob()
            UpdateJob.TAG_ONSTART -> return UpdateJob()
            else -> return null
        }
    }
}