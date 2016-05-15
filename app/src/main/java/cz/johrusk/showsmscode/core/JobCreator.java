package cz.johrusk.showsmscode.core;

import cz.johrusk.showsmscode.core.JobClass;

/**
 * Created by Pepa on 15.05.2016.
 */
public class JobCreator implements com.evernote.android.job.JobCreator {


    @Override
    public com.evernote.android.job.Job create(String tag) {
        switch (tag) {

            case JobClass.TAG:
                return  new JobClass();
            case JobClass.TAG_WEEKLY:
                return new JobClass();
            default:
                return null;
        }
    }
}