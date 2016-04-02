package cz.johrusk.showsmscode.receiver;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;

import java.net.URI;

/**
 * Created by Pepa on 02.04.2016.
 */
public class UpdateService extends JobService {

    JobParameters params;
    UpdateTask updateTask;

    @Override
    public boolean onStartJob(JobParameters params) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private class UpdateTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            public URI buildUri(int todo){

            }
        }
    }
}
