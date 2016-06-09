package cz.johrusk.showsmscode.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.johrusk.showsmscode.activity.MainActivity;
import es.dmoral.prefs.Prefs;
import timber.log.Timber;

/**
 * Service which update definitions database.
 *
 * @author Josef Hruska (pepa.hruska@gmail.com)
 */

public class UpdateService extends Service {

    UpdateTask updateTask;
    public Context context = this;
    public final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public UpdateService getService() {
            return UpdateService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Check whether is connection available otherwise it will postpone update about 60 seconds.
        if (isConnected()) {

            updateTask = new UpdateTask(context);
            updateTask.execute("0");
            Timber.d("CONNECTED = true");
        } else if (!intent.hasExtra("firstTry")) {
            Timber.d("CONNECTED = false");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent updtintent = new Intent(context, UpdateService.class);
                    updtintent.putExtra("firstTry", false);
                    startService(updtintent);
                }
            }, 60000);
        }
        return START_NOT_STICKY;
    }

    /**
     * This method return true if device has internet connection, otherwise false
     */
    public Boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private class UpdateTask extends AsyncTask<String, Void, String[]> {
        private Context c;
        private String LOG_TAG = MainActivity.class.getName();

        public UpdateTask(Context context) {
            this.c = context;
        }

        private void writeToFile(String data, String name) throws IOException {
            String file = null;
            if (name.equals("SMS")) {
                Timber.d("Saving new SMS.json....");
                file = "sms.txt";
            } else if (name.equals("VER")) {
                Timber.d("Saving new version.json...." + data);
                file = "version.txt";
                try {
                    JSONObject object = new JSONObject(data);
                    Timber.d(object.toString());
                    String updateContent = object.getString("news");
                    Timber.d("NEWS is : " + updateContent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(file, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            if (name.equals("VER")) {
                stopSelf();
            }
        }

        public String loadJSONFromAsset() {
            String json = null;
            try {
                InputStream is = context.getAssets().open("version.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
            } catch (IOException ex) {
                Crashlytics.logException(ex);
                return null;
            }
            Timber.d("loadJSONFromAssets returns:" + json);
            Crashlytics.log("loadJSONFromAssets returns:" + json);
            return json;
        }

        public int localCheckVersion() throws JSONException {

            String str = readFromFile("version.txt");
            Timber.d("version string :" + str);
            JSONObject jarray = new JSONObject(str);

            int offlineVer = jarray.getInt("version");
            Timber.d("offline version is :" + offlineVer);
            Timber.d("versionDB was updated to: " + offlineVer);

            return offlineVer;
        }

        public String readFromFile(String file) throws JSONException {
            String ret = "";

            try {
                InputStream inputStream = context.openFileInput(file);

                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((receiveString = bufferedReader.readLine()) != null) {
                        stringBuilder.append(receiveString);
                    }

                    inputStream.close();
                    ret = stringBuilder.toString();
                }
            } catch (FileNotFoundException e) {
                Crashlytics.log(1, "READFROMFILE", "File not found: " + e.toString());
            } catch (IOException e) {
                Crashlytics.log(1, "READFROMFILE", "Can not read file: " + e.toString());
            }
            Timber.d("readFormFile return: " + ret);
            return ret;
        }

        public Boolean firstTimeCheckVersion(String onlineVerStr) throws JSONException {
            final String SMS_FILE = "/sms.txt";
            final String VERSION_FILE = "/version.txt";
            final String INTERNAL_PATH_SMS = context.getFilesDir().getPath() + SMS_FILE;
            final String INTERNAL_PATH_VERSION = context.getFilesDir().getPath() + VERSION_FILE;

            File version_file = new File(INTERNAL_PATH_VERSION);
            File sms_file = new File(INTERNAL_PATH_SMS);
            Timber.d("Path of smsJSON: " + sms_file.getAbsolutePath());
            Timber.d("Path of versionJSON: " + version_file.getAbsolutePath());

            if (sms_file.exists() && version_file.exists()) {
                Crashlytics.log("sms.txt and version.txt exists in internal storage");
                Timber.d("sms.txt and version.txt exists in internal storage");
                int localVer = localCheckVersion();

                JSONObject jarray = new JSONObject(onlineVerStr);
                int onlineVer = jarray.getInt("version");
                Timber.d("Online Ver: " + onlineVer);

                if (onlineVer == localVer) {
                    Timber.d("Online version in internal storage is same as online version");
                    stopSelf();
                    return true;
                } else {
                    Timber.d("Online version in internal storage is older then online version");
                    UpdateTask replaceSms = new UpdateTask(context);
                    Timber.d( "Local version in internal storage will be updated");
                    replaceSms.execute("1");
                }
            } else {
                Crashlytics.log("version.txt doesn't exist in internal storage");
                int localVer;
                int onlineVer;
                JSONObject locObj;

                JSONObject Json = new JSONObject(onlineVerStr);

                onlineVer = Json.getInt("version");
                Timber.d( "Online version is: " + String.valueOf(onlineVer));

                locObj = new JSONObject(loadJSONFromAsset());
                localVer = locObj.getInt("version");
                Timber.d( "Local version is: " + String.valueOf(localVer));
                Prefs.with(c).writeInt("DBVersion", localVer);

                if (localVer == onlineVer) {
                    Timber.d( "Version of JSON in Assets is same as online version");
                    stopSelf();

                    return true;
                } else {
                    Timber.d( "Version of JSON in Assets is older than online version");
                    UpdateTask updateSms = new UpdateTask(context);
                    updateSms.execute("1");
                    Timber.d("Local version in Assets wont be used anymore. Online version will be stored in internal storage and will be used instead ");
                }
            }
            return true;
        }

        @Override
        protected String[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            int par = Integer.valueOf(params[0]);
            String[] results = new String[2];
            String JsonStr = null;
            String dUrl = null;

            final String VERSION_URL = "https://rawgit.com/JosefHruska/ShowSMSCode/master/app/src/main/assets/version.json";
            final String SMS_URL = "https://rawgit.com/JosefHruska/ShowSMSCode/master/app/src/main/assets/sms.json";

            if (par == 0 || par == 2) {
                dUrl = VERSION_URL;
            } else if (par == 1) {
                dUrl = SMS_URL;
            }
            try {

                Uri buildUri = Uri.parse(dUrl).buildUpon().build();
                URL url = new URL(buildUri.toString());

                // Create the request to GITHub, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    Timber.d("BUFFER == 0");
                    stopSelf();
                    return null;
                }
                JsonStr = buffer.toString();
            } catch (IOException e) {
                Crashlytics.logException(e);

                return null;
            } finally {
                Timber.d("output" + JsonStr);
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Timber.d("Error closing stream");
                        Crashlytics.log(1, "UPDATE-SERVICE", "Error closing stream");
                    }
                }
            }
            if (JsonStr != null) {
                results[0] = JsonStr;
                results[1] = String.valueOf(par);
            } else {
                Timber.d("Probably connection problem");
                stopSelf();
                return null;
            }
            return results;
        }

        @Override
        protected void onPostExecute(String[] result) {

            if (result[1] != null) {
                switch (result[1]) {
                    case "0":
                        try {
                            firstTimeCheckVersion(result[0]);
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                        }
                        break;
                    case "1":
                        try {
                            Timber.d("New version.json was downloaded");
                            Crashlytics.log("New version.json was downloaded");
                            writeToFile(result[0], "SMS");
                            UpdateTask updateVersion = new UpdateTask(context);
                            updateVersion.execute("2");
                        } catch (IOException e) {
                            Crashlytics.logException(e);
                        }
                        break;
                    case "2":
                        try {
                            writeToFile(result[0], "VER");
                        } catch (IOException e) {
                            Crashlytics.logException(e);
                        }
                        break;
                }
            } else {
                Timber.d("Download Failded");
                Crashlytics.log(1, "UPDATE_SERVICE", "NullPointerException");
            }
        }
    }
}
