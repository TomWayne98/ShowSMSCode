package cz.johrusk.showsmscode.sched;

/**
 * Created by Pepa on 15.05.2016.
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