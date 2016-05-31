package cz.johrusk.showsmscode.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import cz.johrusk.showsmscode.R;
import cz.johrusk.showsmscode.core.JobClass;
import cz.johrusk.showsmscode.service.SimulateSMS_service;
import cz.johrusk.showsmscode.service.Update_service;
import cz.johrusk.showsmscode.service.WatchListener_service;
import pl.tajchert.buswear.EventBus;


/**
 * The ShowSMSCode app find codes in incoming messages. Codes are showed via notification, overlaywindow or android wear device.
 * Code is also sent to clipboard.
 *
 * @author Josef Hruska
 * @version 0.1
 * @since 2016-04-09
 */

public class Main_activity extends AppCompatActivity {

    static Context context;
    public static final String LOG_TAG = Main_activity.class.getSimpleName();
    // Permission requests
    public static final int PERM_SMS_RECIEVE = 0;
    public static final int PERM_SMS_READ = 1;
    public static final int PERM_RECEIVE_BOOT = 2;
    public static final int PERM_READ_P_STATE = 3;
    public static final int PERM_SHOW_WINDOWS = 4;

    //Jobs - Time periods
    public static final long UPDATE_24H = (60 * 24);
    public static final long UPDATE_12H = UPDATE_24H / 2;
    public static final long UPDATE_6H = UPDATE_24H / 4;
    public static final long UPDATE_1H = UPDATE_24H / 24;
    public static final long UPDATE_WEEK = UPDATE_24H * 7;
    public static final long UPDATE_DEBUG = UPDATE_1H / 60;


    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Main_activity.context = getApplicationContext();
        String crashlytics_id = String.valueOf(android.os.Build.MANUFACTURER + android.os.Build.MODEL);
        Crashlytics.setUserName(crashlytics_id);
        Crashlytics.log(crashlytics_id);

        setContentView(R.layout.new_main_activity);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

//        Toolbar settings
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColorPrimary));
        setSupportActionBar(toolbar);
//        Underline text
        // TODO - add LinkedIn profile
        TextView notRecognized = (TextView) findViewById(R.id.MA_tv_notRecognized);
        TextView addToGit = (TextView) findViewById(R.id.MA_tv_addToGit);
        TextView author = (TextView) findViewById(R.id.MA_tv_author);
        TextView sourceCode = (TextView) findViewById(R.id.MA_tv_sourceCode);
        TextView reportIssue = (TextView) findViewById(R.id.MA_tv_reportIssue);

        addToGit.setPaintFlags(reportIssue.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        author.setPaintFlags(author.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        sourceCode.setPaintFlags(sourceCode.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        reportIssue.setPaintFlags(reportIssue.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


//        Schedule Job
        scheduleJob(UPDATE_24H); // Basic DB update
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().post("text", this);
        Log.d(LOG_TAG, "ONSTART -----");
        Intent updtintent = new Intent(context, Update_service.class);
        startService(updtintent);
        checkPermissionState();
        Intent ListenIntent = new Intent(context, WatchListener_service.class);
        startService(ListenIntent);


        if (ContextCompat.checkSelfPermission(Main_activity.context,
                Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    PERM_SMS_READ);
        }
        if (ContextCompat.checkSelfPermission(Main_activity.context,
                Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_SMS},
                    PERM_SMS_RECIEVE);
        }
        if (ContextCompat.checkSelfPermission(Main_activity.context,
                Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED},
                    PERM_RECEIVE_BOOT);
        }
        if (ContextCompat.checkSelfPermission(Main_activity.context,
                Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
                    PERM_SHOW_WINDOWS);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "MainActivity state: onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Log.d(LOG_TAG, "MainActivity state: onStop");
        super.onStop();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1234 && Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(context, Settings_activity.class);
                startActivity(settingsIntent);// User chose the "Settings" item, show the app settings UI...
                return true;
            case R.id.action_simulateSMS:
                Intent simulateIntent = new Intent(context, SimulateSMS_service.class);
                startService(simulateIntent);// User chose the "Settings" item, show the app settings UI...
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    // Scheduled Job which updates DB every day(default) eventually as soon as is connection available
    private void scheduleJob(long period) {

        if (JobManager.instance().getAllJobRequestsForTag(JobClass.TAG).isEmpty()) {
            int jobId = new JobRequest.Builder(JobClass.TAG)
                    .setPeriodic(60_000L * period)
                    .setPersisted(true)
                    .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                    .build()
                    .schedule();
        }
    }

    /**
     * This method checks if are all permissions granted
     * eventually it change state indicator to yellow (green circle)
     */
    public void checkPermissionState() {
        Boolean isOK = true;
        LinearLayout ll_state = (LinearLayout) findViewById(R.id.ll_state);
        ImageView iv_state = (ImageView) findViewById(R.id.iv_state);
        TextView tv_state = (TextView) findViewById(R.id.tv_state);
        if (ContextCompat.checkSelfPermission(Main_activity.context,
                Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            isOK = false;
        }
        if (ContextCompat.checkSelfPermission(Main_activity.context,
                Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            isOK = false;
        }
        if (ContextCompat.checkSelfPermission(Main_activity.context,
                Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
            isOK = false;
        }

        if (isOK == false) {
            iv_state.setColorFilter(Color.YELLOW);
            tv_state.setText(R.string.MA_text_state_needperm);
            ll_state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                    myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                    myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myAppSettings);
                }
            });
        }
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(context)) {
            iv_state.setColorFilter(Color.YELLOW);
            tv_state.setText(R.string.MA_text_state_needperm);
            ll_state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myAppSettings = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                    myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(myAppSettings, 1234);
                }
            });

        }
        if (isOK == true && Build.VERSION.SDK_INT < 23) {
            iv_state.setColorFilter(getResources().getColor(R.color.color_state));
            tv_state.setText(R.string.MA_text_state);
            ll_state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        } else if (isOK == true && (Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(context))) {
            iv_state.setColorFilter(getResources().getColor(R.color.color_state));
            tv_state.setText(R.string.MA_text_state);
            ll_state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    public void AddToGit(View view) {
        String url = "https://github.com/JosefHruska/ShowSMSCode/wiki/How-to-add-new-SMS-pattern";
        Intent showGithub = new Intent(Intent.ACTION_VIEW);
        showGithub.setData(Uri.parse(url));
        startActivity(showGithub);
    }

    public void ReportIssue(View view) {
        String url = "https://github.com/JosefHruska/ShowSMSCode/issues";
        Intent ReportIssue = new Intent(Intent.ACTION_VIEW);
        ReportIssue.setData(Uri.parse(url));
        startActivity(ReportIssue);
    }

    public void OpenSourceCode(View view) {
        String url = "https://github.com/JosefHruska/ShowSMSCode";
        Intent OpenSourceCode = new Intent(Intent.ACTION_VIEW);
        OpenSourceCode.setData(Uri.parse(url));
        startActivity(OpenSourceCode);
    }

    public void AboutAuthor(View view) {
        String url = "https://www.linkedin.com/in/josefhruska";
        Intent AboutAuthor = new Intent(Intent.ACTION_VIEW);
        AboutAuthor.setData(Uri.parse(url));
        startActivity(AboutAuthor);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], int[] grantResults) {


        /**
         * This method sends notifications if some of permissions isn't granted
         */
        switch (requestCode) {
            case PERM_READ_P_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {

                }
                return;
            }
            case PERM_SMS_READ: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
            case PERM_SMS_RECIEVE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
            case PERM_RECEIVE_BOOT: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
            case PERM_SHOW_WINDOWS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
                }
                return;
            }

        }
    }

}


