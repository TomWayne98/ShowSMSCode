package cz.johrusk.showsmscode.sched

import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest

/**
 * Helper class for running jobs which can be called from whole app.

 * @author Josef Hruska (pepa.hruska@gmail.com)
 */

object JobRunner {
    // Scheduled Job which updates DB every day(default) eventually as soon as is connection available
    fun scheduleJob(period: Long) {
        if (JobManager.instance().getAllJobRequestsForTag(UpdateJob.TAG).isEmpty()) {
            val jobId = JobRequest.Builder(UpdateJob.TAG).setPeriodic(60000L * period)
                    .setRequirementsEnforced(true) // All requirements have to be satisfied
                    .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                    .build()
                    .schedule()
        }
    }

    fun scheduleOnStartJob() {
        if (JobManager.instance().getAllJobRequestsForTag(UpdateJob.TAG_ONSTART).isEmpty()) {
            val jobId = JobRequest
                    .Builder(UpdateJob.TAG_ONSTART)
                    .setExecutionWindow(10000L, 30000L).setRequirementsEnforced(true) // All requirements have to be satisfied
                    .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                    .build()
                    .schedule()
        }
    }
}
