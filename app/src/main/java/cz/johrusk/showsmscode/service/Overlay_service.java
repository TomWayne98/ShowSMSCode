package cz.johrusk.showsmscode.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import cz.johrusk.showsmscode.activity.Main_activity;
import cz.johrusk.showsmscode.fragment.Settings_fragment;

/**
 * This class show code in windows which overlay entire screen
 */



public class Overlay_service extends Service {
    public static final String LOG_TAG = Main_activity.class.getName();
    private WindowManager windowManager;
    private TextView codeView;
    private TextView senderView;
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
        String sender = dataArray[2];
        String s = "until_tap";

        //má tam být if (!s.equals(overlayDelay)) ale until_tap se zatím nepoužívá


        Answers.getInstance().logCustom(new CustomEvent("Overlay showed")
                .putCustomAttribute("Code overlaying screen:", code));
        Answers.getInstance().logCustom(new CustomEvent("Overlay delay")
                .putCustomAttribute("Length of overlaying", overlayDelay));



        Log.d("OverLayService", "StartOk");
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        //add TextView which contain show in SMS
        senderView = new TextView(this);
        senderView.setText(sender);
        senderView.setTextSize(40);
        senderView.setTextColor(Color.BLACK);
        senderView.setGravity(Gravity.CENTER | Gravity.TOP);


        codeView = new TextView(this);
        codeView.setGravity( Gravity.CENTER | Gravity.BOTTOM);
        codeView.setPadding(0,100,0,0);
        codeView.setText(code);
        codeView.setTextSize(80);
        codeView.setTextColor(Color.BLACK);
        codeView.setBackgroundColor(Color.WHITE);
        codeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG,"Window tappped");
                stopSelf();
            }
        });


        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER | Gravity.TOP;
        params.x = 0;
        params.y = 0;

        windowManager.addView(codeView, params);
        windowManager.addView(senderView, params);


        return START_STICKY;
    }

    @Override public void onCreate() {
        super.onCreate();
      Boolean isTap = true;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String i = sharedPref.getString(Settings_fragment.KEY_PREF_OVERLAY_DELAY, "");
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
        if (senderView != null) windowManager.removeView(senderView);
    }
}



