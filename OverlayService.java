package cz.johrusk.showsmscode;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import cz.johrusk.showsmscode.receiver.SettingsFragment;

/**
 * Created by Pepa on 27.03.2016.
 */

// TODO Přidat nastavení pro zmizení okna po časovém intervalu a po tapnutí

public class OverlayService extends Service {
    private WindowManager windowManager;
    private TextView codeView;





    @Override public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String i = sharedPref.getString(SettingsFragment.KEY_PREF_OVERLAY_DELAY, "");
        int overlayDelay = Integer.valueOf(i);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSelf();
            }
        }, overlayDelay);

        Log.d("OverLayService", "StartOk");
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        codeView = new TextView(this);

        codeView.setText("123456789");
        codeView.setTextSize(80);
        codeView.setTextColor(Color.BLACK);
        codeView.setBackgroundColor(Color.WHITE);
        codeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                stopSelf();
            }
         });


        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        windowManager.addView(codeView, params);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (codeView != null) windowManager.removeView(codeView);
    }
}

