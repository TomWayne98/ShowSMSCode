package cz.johrusk.showsmscode;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import java.util.Objects;

import cz.johrusk.showsmscode.fragment.SettingsFragment;

/**
 * This class show code in windows which overlay entire screen
 */



public class OverlayService extends Service {
    public static final String LOG_TAG = MainActivity.class.getName();
    private WindowManager windowManager;
    private TextView codeView;
    public Bundle bundle;
    public int overlayDelay;






    @Override public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        this.bundle = intent.getBundleExtra("bundle");


        String[] dataArray;
        dataArray = bundle.getStringArray("key");
        String code = dataArray[0];
        String s = "until_tap";

        //má tam být if (!s.equals(overlayDelay)) ale until_tap se zatím nepoužívá


        Answers.getInstance().logCustom(new CustomEvent("Overlay showed")
                .putCustomAttribute("Code overlaying screen:", code));
        Answers.getInstance().logCustom(new CustomEvent("Overlay delay")
                .putCustomAttribute("Length of overlaying", overlayDelay));



        Log.d("OverLayService", "StartOk");
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        //add TextView which contain show in SMS
        codeView = new TextView(this);
        codeView.setGravity(Gravity.CENTER);
        codeView.setText(code);
        codeView.setTextSize(80);
        codeView.setTextColor(Color.BLACK);
        codeView.setBackgroundColor(Color.WHITE);
        codeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG,"Windows tappped");
                stopSelf();
            }
        });


        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER | Gravity.LEFT;
        params.x = 0;
        params.y = 200;

        windowManager.addView(codeView, params);


        return START_STICKY;
    }

    @Override public void onCreate() {
        super.onCreate();
      Boolean isTap = true;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String i = sharedPref.getString(SettingsFragment.KEY_PREF_OVERLAY_DELAY, "");
String tap = "until_tap";

        if  (!tap.equals(String.valueOf(i))){
            overlayDelay = Integer.valueOf(i) * 1000;
            Log.d(LOG_TAG,"Handler started");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopSelf();
                }
            }, overlayDelay);

        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (codeView != null) windowManager.removeView(codeView);
    }
}



