package cz.johrusk.showsmscode.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import cz.johrusk.showsmscode.core.App;
import cz.johrusk.showsmscode.service.Notification_service;
import cz.johrusk.showsmscode.service.Update_service;

/**
 * Created by Pepa on 15.05.2016.
 */
public class JobClass extends com.evernote.android.job.Job {




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
            Intent notifWeeklyIntent2 = new Intent(context, Notification_service.class);
            notifWeeklyIntent2.putExtras(bundle);
           // context.startService(notifWeeklyIntent2);
        } else if (params.getTag().equals(TAG)) {

            Intent updtintent = new Intent(context, Update_service.class);
            context.startService(updtintent);
        }
        return Result.SUCCESS;
    }
}