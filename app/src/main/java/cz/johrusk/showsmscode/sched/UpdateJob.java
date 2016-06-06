package cz.johrusk.showsmscode.sched;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import cz.johrusk.showsmscode.core.App;
import cz.johrusk.showsmscode.service.NotificationService;
import cz.johrusk.showsmscode.service.UpdateService;

/**
 * Created by Pepa on 15.05.2016.
 */
public class UpdateJob extends com.evernote.android.job.Job {

    public static final String TAG = "job_demo_tag";
    public static final String TAG_WEEKLY = "job_weekly_tag";

    @Override
    @NonNull
    protected Result onRunJob(Params params) {
        // run your job
       Log.d("TAG","TAF---------");
        Context context = App.get();
        if (params.getTag().equals(TAG_WEEKLY)) {
            Bundle bundle = new Bundle();
            String type = "notifWeekly";
            bundle.putStringArray("key", new String[]{null, null, type, type});
            Intent notifWeeklyIntent2 = new Intent(context, NotificationService.class);
            notifWeeklyIntent2.putExtras(bundle);
           // context.startService(notifWeeklyIntent2);
        } else if (params.getTag().equals(TAG)) {

            Intent updtIntent = new Intent(context, UpdateService.class);
            context.startService(updtIntent);
        }
        return Result.SUCCESS;
    }
}