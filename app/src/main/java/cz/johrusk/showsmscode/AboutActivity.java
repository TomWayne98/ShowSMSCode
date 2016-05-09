package cz.johrusk.showsmscode;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AboutActivity extends AppCompatActivity{
    String[] senders = new String[50];
    Toolbar toolbar;
    int skiped = 0;
    Context c = this;


    private static final String LOG_TAG = MainActivity.class.getName();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.about_activity);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSetting);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null){
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }




    final ListView listview = (ListView) findViewById(R.id.listview);
            String notAvailable = getString(R.string.AA_not_available);
            String[] data = {
                    notAvailable, notAvailable, notAvailable, notAvailable, notAvailable, notAvailable,
                    notAvailable, notAvailable, notAvailable, notAvailable, notAvailable};

            final ArrayList<String> placeholderData = new ArrayList<String>(Arrays.asList(data));
            final StableArrayAdapter adapter = new StableArrayAdapter(this,
                    android.R.layout.simple_list_item_1, placeholderData);
// TODO případně smazat
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                CharSequence charSeq =((TextView)view).getText();
                Log.d(LOG_TAG,"Item was clicked( " + charSeq + " )");
                ((TextView)view).setText(charSeq + " Heh");


            }
        });
            try {
                 getSMSArray();
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
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


        Toast.makeText(c, "SMS pattern sended", Toast.LENGTH_LONG).show();

        senderText.setText("");
        smsBodyText.setText("");

    }


    public class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }



    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        toolbar = (Toolbar) findViewById(R.id.toolbarSetting);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
    }
    public void getSMSArray() throws JSONException {
        String str = loadSMS();
        ArrayList<String> newData = new ArrayList<String>();
        Log.d(LOG_TAG, "version string :" + str);
        JSONObject jarray = new JSONObject(str);

        JSONArray jsonArray = jarray.getJSONArray("sms");
        for (int i = 0; jsonArray.length() > i; i++) {
            JSONObject jo_inside = jsonArray.getJSONObject(i);
            Log.d("Details-->", jo_inside.getString("id"));
            int id_value = jo_inside.getInt("id");
            Boolean isPublic = jo_inside.getBoolean("ispublic");
            if (isPublic) {
                senders[i - skiped] = jo_inside.getString("sender");
            } else {
                skiped += 1;
            }
        }
        Log.d(LOG_TAG,String.valueOf(senders.length));
        for (int x = 1; senders[x]  != null; x++)
        {
            newData.add(senders[x]);
            Log.d(LOG_TAG,senders[x]);
        }
        final ListView listviewnew = (ListView) findViewById(R.id.listview);
        final StableArrayAdapter adapter2 = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, newData);
        final  ArrayList<String> xdata = newData;
        listviewnew.setAdapter(adapter2);

        listviewnew.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                CharSequence charSeq =((TextView)view).getText();
                Log.d(LOG_TAG,"Item was clicked( " + charSeq + " )");
        ((TextView)view).setText(charSeq + " Heh");
                            }



        });
    }

    public String loadSMS() throws JSONException {
        String ret = "";

        try {
            InputStream inputStream = c.openFileInput("sms.txt");

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
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        Log.d(LOG_TAG,"readFormFile return: " + ret);
        return ret;
    }

}

