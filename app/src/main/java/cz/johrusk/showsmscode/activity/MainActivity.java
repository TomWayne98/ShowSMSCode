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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.johrusk.showsmscode.R;
import cz.johrusk.showsmscode.core.App;
import cz.johrusk.showsmscode.sched.UpdateJob;
import cz.johrusk.showsmscode.service.SimulateSmsService;
import timber.log.Timber;

import static java.lang.String.valueOf;


/**
 * Main application activity.
 *
 * @author Josef Hruska (pepa.hruska@gmail.com)
 **/

public class MainActivity extends AppCompatActivity {

    static Context context;
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

//    @BindView(R.id.ll_state) LinearLayout ll_state;
    @BindView(R.id.iv_state) ImageView iv_state;
    @BindView(R.id.tv_state) TextView tv_state;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.MA_tv_notRecognized) TextView notRecognized ;
    @BindView(R.id.MA_tv_addToGit) TextView addToGit;
    @BindView(R.id.MA_tv_author) TextView author;
    @BindView(R.id.MA_tv_sourceCode) TextView sourceCode;
    @BindView(R.id.MA_tv_reportIssue) TextView reportIssue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = App.get();
        String crashlytics_id = valueOf(android.os.Build.MANUFACTURER + android.os.Build.MODEL);
        Crashlytics.setUserName(crashlytics_id);
        Crashlytics.log(crashlytics_id);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
//        Toolbar settings
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColorPrimary));
        setSupportActionBar(toolbar);
//        Underline text
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
        Timber.d("ONSTART");
//        Intent updtintent = new Intent(context, UpdateService.class);
//        startService(updtintent);
        scheduleOnStartJob();
        checkPermissionState();
    }

    @Override
    protected void onDestroy() {
        Timber.d("MainActivity state: onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Timber.d("MainActivity state: onStop");
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
                Intent settingsIntent = new Intent(context, SettingsActivity.class);
                startActivity(settingsIntent);// User chose the "Settings" item, show the app settings UI...
                return true;
            case R.id.action_simulateSMS:
                Intent simulateIntent = new Intent(context, SimulateSmsService.class);
                startService(simulateIntent);// User chose the "Settings" item, show the app settings UI...
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Scheduled Job which updates DB every day(default) eventually as soon as is connection available
    public static void scheduleJob(long period) {
        if (JobManager.instance().getAllJobRequestsForTag(UpdateJob.TAG).isEmpty()) {
            int jobId = new JobRequest.Builder(UpdateJob.TAG)
                    .setPeriodic(60_000L * period)
                    .setPersisted(true)
                    .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                    .build()
                    .schedule();
        }
    }
    public static void scheduleOnStartJob() {
            int jobId = new JobRequest.Builder(UpdateJob.TAG_ONSTART)
                    .setExecutionWindow(10_000L, 20_000L)
                    .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                    .setRequirementsEnforced(true)
                    .setPersisted(true)
                    .setUpdateCurrent(true)
                    .build()
                    .schedule();
    }
    /**
     * This method checks if are all permissions granted
     * eventually it change state indicator to yellow (green circle)
     */
    public void checkPermissionState() {
        Boolean isOK = true;

        if (ContextCompat.checkSelfPermission(MainActivity.context,
                Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
                    PERM_SHOW_WINDOWS);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.context,
                Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    PERM_SMS_READ);
            isOK = false;
        }
        if (ContextCompat.checkSelfPermission(MainActivity.context,
                Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_SMS},
                    PERM_SMS_RECIEVE);
            isOK = false;
        }
        if (ContextCompat.checkSelfPermission(MainActivity.context,
                Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED},
                    PERM_RECEIVE_BOOT);
            isOK = false;
        }

        if (isOK == false) {
            iv_state.setColorFilter(Color.YELLOW);
            tv_state.setText(R.string.MA_text_state_needperm);
            iv_state.setOnClickListener(new View.OnClickListener() {
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
            iv_state.setOnClickListener(new View.OnClickListener() {
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
            iv_state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        } else if (isOK == true && (Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(context))) {
            iv_state.setColorFilter(getResources().getColor(R.color.color_state));
            tv_state.setText(R.string.MA_text_state);
           iv_state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }


    public void openBrowser(View v) {
        String url = null;

        switch (v.getId()) {
            case R.id.MA_tv_addToGit:
                url = "https://github.com/JosefHruska/ShowSMSCode/wiki/How-to-add-new-SMS-pattern";
                break;
            case R.id.MA_tv_reportIssue:
                url = "https://github.com/JosefHruska/ShowSMSCode/issues";
                break;
            case R.id.MA_tv_sourceCode:
                url = "https://github.com/JosefHruska/ShowSMSCode";
                break;
            case R.id.MA_tv_author:
                url = "https://www.linkedin.com/in/josefhruska";
                break;
        }
        Intent openBrowser = new Intent(Intent.ACTION_VIEW);
        openBrowser.setData(Uri.parse(url));
        startActivity(openBrowser);
    }
}


