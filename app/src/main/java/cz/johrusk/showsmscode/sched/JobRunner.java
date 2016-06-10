package cz.johrusk.showsmscode.sched;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

/**
 * Helper class for running jobs which can be call from whole app.
 *
 * @author Josef Hruska (pepa.hruska@gmail.com)
 */

public class JobRunner {


    // Scheduled Job which updates DB every day(default) eventually as soon as is connection available
    public static void scheduleJob(long period) {
        if (JobManager.instance().getAllJobRequestsForTag(UpdateJob.TAG).isEmpty()) {
            int jobId = new JobRequest.Builder(UpdateJob.TAG)
                    .setPeriodic(60_000L * period)
                    .setPersisted(true)
                    .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                    .build()
                    .schedule();
        }
    }
    public static void scheduleOnStartJob() {
        int jobId = new JobRequest.Builder(UpdateJob.TAG_ONSTART)
                .setExecutionWindow(10_000L, 20_000L)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setRequirementsEnforced(true)
                .setPersisted(true)
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }
}
