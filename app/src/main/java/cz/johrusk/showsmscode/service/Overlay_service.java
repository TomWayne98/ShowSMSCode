package cz.johrusk.showsmscode.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import cz.johrusk.showsmscode.R;
import cz.johrusk.showsmscode.activity.Main_activity;
import cz.johrusk.showsmscode.fragment.Settings_fragment;
import me.grantland.widget.AutofitTextView;

/**
 * This class show code in windows which overlay entire screen
 */


public class Overlay_service extends Service {
    public static final String LOG_TAG = Main_activity.class.getName();
    private WindowManager windowManager;
    private TextView codeView;
    private TextView senderView;
    private AutofitTextView foo;
    private LayoutInflater layoutInf;
    private View layout;
    public Bundle bundle;
    public int overlayDelay;
    private View.OnClickListener clicklistener;


    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        this.bundle = intent.getBundleExtra("bundle");


        layoutInf = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        layout = layoutInf.inflate(R.layout.overlay_service,null);
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

        clicklistener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Window tappped");
                stopSelf();
            }
        };



        Log.d("OverLayService", "StartOk");
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);



        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                900,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER | Gravity.TOP;
        params.x = 0;
        params.y = 0;


        codeView = (TextView) layout.findViewById(R.id.textView2);
        senderView = (TextView) layout.findViewById(R.id.textView);

        codeView.setText(sender);
        senderView.setText(code);
        windowManager.addView(layout, params);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String i = sharedPref.getString(Settings_fragment.KEY_PREF_OVERLAY_DELAY, "");
        String tap = "until_tap";
        if (!tap.equals(String.valueOf(i))) {
            overlayDelay = Integer.valueOf(i) * 1000;
            Log.d(LOG_TAG, "Handler started");
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
//        if (codeView != null) windowManager.removeView(codeView);
//        if (senderView != null) windowManager.removeView(senderView);
        if (layout != null) windowManager.removeView(layout);

    }
}



