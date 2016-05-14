package cz.johrusk.showsmscode;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import cz.johrusk.showsmscode.fragment.FiveFragment;
import cz.johrusk.showsmscode.fragment.FourFragment;
import io.fabric.sdk.android.Fabric;

import java.util.ArrayList;
import java.util.List;

import cz.johrusk.showsmscode.fragment.OneFragment;
import cz.johrusk.showsmscode.fragment.ThreeFragment;
import cz.johrusk.showsmscode.fragment.TwoFragment;

/**
 * The ShowSMSCode app find codes in incoming messages. Codes are showed via notification, overlaywindow or Android wear device.
 * Code is also sent to clipboard.
 *
 * @author Josef Hruska
 * @version 0.1
 * @since 2016-04-09
 */

public class MainActivity extends AppCompatActivity {

    static Context context;
    private UpdateService updateService;
    private boolean bound = false;

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
    private TabLayout tabLayout;
    private ViewPager viewPager;
    // Tab icons in main menu
    private int[] tabIcons = {
            R.drawable.ic_screen_lock_portrait,
            R.drawable.ic_library_add,
            R.drawable.ic_find_in_page
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        JobManager.create(this).addJobCreator(new DemoJobCreator());
        super.onCreate(savedInstanceState);


        Fabric.with(this, new Crashlytics());
        MainActivity.context = getApplicationContext();

        String crashlytics_id = String.valueOf(android.os.Build.MANUFACTURER + android.os.Build.MODEL);

        Crashlytics.setUserName(crashlytics_id);
        Crashlytics.log(crashlytics_id);

        setContentView(R.layout.new_main_activity);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColorPrimary));
        setSupportActionBar(toolbar);

//        viewPager = (ViewPager) findViewById(R.id.viewpager);
//        setupViewPager(viewPager);
//
//        tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(viewPager);
//        setupTabIcons();

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        //TODO- set back correct methods

        // Schedule all jobs
        scheduleJob(UPDATE_DEBUG); // Basic DB update
        scheduleWeeklyNotifJob(UPDATE_DEBUG); // This job sends notification which hints about features which app provides

        //  This is thread which runs first run intro
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                if (isFirstStart) {

                    //  Launch app intro
                    Intent i = new Intent(MainActivity.this, Intro_Activity.class);
                    startActivity(i);
                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();
                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);
                    e.apply();
                }
            }
        });
        t.start();

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("TEST","ONSTART -----");
        // TODO - check whether it solved the bug (JOBMANAGER);
       // JobManager.create(this);
        Intent updtintent = new Intent(context, UpdateService.class);
        startService(updtintent);



        if (ContextCompat.checkSelfPermission(MainActivity.context,
                Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.READ_SMS},
//                    PERM_SMS_READ);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.context,
                Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.RECEIVE_SMS},
//                    PERM_SMS_RECIEVE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.context,
                Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED},
                    PERM_RECEIVE_BOOT);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.context,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.READ_PHONE_STATE},
//                    PERM_READ_P_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.context,
                Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
                    PERM_SHOW_WINDOWS);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d("DEBUG","ONDESTROY");
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        Log.d("DEBUG","OnSTOP");

        JobManager.instance().cancelAll();
        super.onStop();
        // Unbind from the service


        }



    /**
     * This method checks whether is Overlay permission granted (Android 6.0+)
     * @param view
     */
    public void checkOverlayPermission(View view) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1234);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1234 && Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Toast toast = Toast.makeText(context, R.string.toast_overlay_allowed, Toast.LENGTH_LONG);
                toast.show();

            } else {
                Toast toast = Toast.makeText(context, R.string.toast_overlay_denied, Toast.LENGTH_LONG);
                toast.show();
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
//            case R.id.action_about:
//                Intent aboutIntent = new Intent(context, AboutActivity.class);
//                startActivity(aboutIntent);// User chose the "Settings" item, show the app settings UI...
//                return true;
//            case R.id.action_credits:
//                Intent creditsIntent = new Intent(context, Credits.class);
//                startActivity(creditsIntent);// User chose the "Settings" item, show the app settings UI...
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    public class DemoJobCreator implements JobCreator {

        @Override
        public Job create(String tag) {
            switch (tag) {

                case DemoJob.TAG:
                    return new DemoJob();
                case DemoJob.TAG_WEEKLY:
                    return new DemoJob();
                default:
                    return null;
            }
        }
    }


    public class DemoJob extends Job {


        public static final String TAG = "job_demo_tag";
        public static final String TAG_WEEKLY = "job_weekly_tag";

        @Override
        @NonNull
        protected Result onRunJob(Params params) {
            // run your job
            if (params.getTag().equals(TAG_WEEKLY))
            {
                Bundle bundle = new Bundle();
                String type = "notifWeekly";
                bundle.putStringArray("key", new String[]{null, null, type,type});
                Intent notifWeeklyIntent2 = new Intent(context,NotificationService.class);
                notifWeeklyIntent2.putExtras(bundle);
                startService(notifWeeklyIntent2);
            }
            else if (params.getTag().equals(TAG)){
                Intent updtintent = new Intent(context, UpdateService.class);
                startService(updtintent);
            }
            return Result.SUCCESS;
        }
    }

    private void scheduleWeeklyNotifJob(long period) {
        if (JobManager.instance().getAllJobRequestsForTag(DemoJob.TAG_WEEKLY).isEmpty()) {
            int job = new JobRequest.Builder(DemoJob.TAG_WEEKLY)
                    .setPeriodic(60_000L * period)
                    .setPersisted(true)
                    .build()
                    .schedule();
        }
    }

    private void scheduleJob(long period) {

        if (JobManager.instance().getAllJobRequestsForTag(DemoJob.TAG).isEmpty()) {
            int jobId = new JobRequest.Builder(DemoJob.TAG)
                    .setPeriodic(60_000L * period)
                    .setPersisted(true)
                    .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                    .build()
                    .schedule();
        }
    }

    /**
     * This method send a SMS pattern written by user to Crashlitics. This pattern is supposed to be added to SMS DB
     * @param view
     */
    public void sendSMS(View view)
    {
        EditText senderText = (EditText) findViewById(R.id.edit_text_sender);
        EditText smsBodyText = (EditText) findViewById(R.id.edit_text_body);
        String smsBodyTextStr = smsBodyText.getText().toString();
        String senderTextStr = senderText.getText().toString();

        Answers.getInstance().logCustom(new CustomEvent("SMS Sended")
                .putCustomAttribute("Sender " + "/ SMS Body", smsBodyTextStr + " / "+ senderTextStr));
        Crashlytics.log(1,"TEST","Sender text: "+ senderTextStr + "SMS Body: " + smsBodyTextStr);
        Crashlytics.log("SMS pattern sended " + "(Sender text: "+ senderTextStr + "SMS Body: " + smsBodyTextStr + ")");
        Toast.makeText(context, "SMS pattern sended", Toast.LENGTH_LONG).show();

        senderText.setText("");
        smsBodyText.setText("");

    }

    /**
     * This method set icons for tabs in mainActivity
     */
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new OneFragment(), getString(R.string.tab_functions));
        adapter.addFrag(new FourFragment(),  getString(R.string.tab_addSMS));
        adapter.addFrag(new FiveFragment(),  getString(R.string.tab_listSMS));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {


        /**
         * This method sends notifications if some of permissions isn't granted
         */
        switch (requestCode) {
            case PERM_READ_P_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, R.string.MA_Toast_permallowed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.MA_Toast_permdenied, Toast.LENGTH_SHORT).show();
                    sentPermissionNotif(getString(R.string.NS_notif_SMS_receive_permission),false);
                }
                return;
            }
            case PERM_SMS_READ: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, R.string.MA_Toast_permallowed, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, R.string.MA_Toast_permdenied, Toast.LENGTH_SHORT).show();
                    sentPermissionNotif(getString(R.string.NS_notif_SMS_receive_permission),false);
                }
                return;
            }
            case PERM_SMS_RECIEVE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, R.string.MA_Toast_permallowed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.MA_Toast_permdenied, Toast.LENGTH_SHORT).show();
                    sentPermissionNotif(getString(R.string.NS_notif_SMS_read_permission),false);
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
                } else if(Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
                    sentPermissionNotif(getString(R.string.NS_notif_Draw_over_permission),true);
                }
                return;
            }

        }
    }

    private void sentPermissionNotif(String perm, Boolean overlay){
        Bundle bundle = new Bundle();
        String type = "notifPermission";
        bundle.putStringArray("key", new String[]{perm, String.valueOf(overlay), null,type});
        Intent notifPermission = new Intent(context,NotificationService.class);
        notifPermission.putExtras(bundle);
        startService(notifPermission);
    }


}


