package cz.johrusk.showsmscode.sched;

/**
 * Class which creates scheduled job
 *
 * @author Josef Hruska (pepa.hruska@gmail.com)
 */
public class JobCreator implements com.evernote.android.job.JobCreator {


    @Override
    public com.evernote.android.job.Job create(String tag) {
        switch (tag) {

            case UpdateJob.TAG:
                return  new UpdateJob();
            case UpdateJob.TAG_WEEKLY:
                return new UpdateJob();
            default:
                return null;
        }
    }
}