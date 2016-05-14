package cz.johrusk.showsmscode.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

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
import java.util.Objects;

import cz.johrusk.showsmscode.MainActivity;
import cz.johrusk.showsmscode.R;


public class FiveFragment extends Fragment {

    public FiveFragment() {
        // Required empty public constructor
    }

    private static final String LOG_TAG = MainActivity.class.getName();

    String[][] senders = new String[50][2];
    int skiped = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);







    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String notAvailable = getString(R.string.AA_not_available);
        String[] data = {
                notAvailable, notAvailable, notAvailable, notAvailable, notAvailable, notAvailable,
                notAvailable, notAvailable, notAvailable, notAvailable, notAvailable};

        final ArrayList<String> placeholderData = new ArrayList<String>(Arrays.asList(data));
        final StableArrayAdapter adapter = new StableArrayAdapter(getContext(),
                android.R.layout.simple_list_item_1, placeholderData);

        final ListView listview = (ListView) getView().findViewById(R.id.listviewFrag);
        listview.setAdapter(adapter);
        // Inflate the layout for this fragment
        try {
            getSMSArray();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        Log.d(LOG_TAG,"ICH BIN PAUSEN");
        skiped = 0;
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_five, container, false);
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
                senders[i - skiped][0] = jo_inside.getString("sender");
                senders[i - skiped][1] = jo_inside.getString("example");
            } else {
                skiped += 1;
            }
        }
        Log.d(LOG_TAG, String.valueOf(senders.length));
        for (int x = 0; senders[x][0] != null; x++) {
            newData.add(senders[x][0]);
            Log.d(LOG_TAG, senders[x][0] + senders[x][1]);

        }
        final ListView listviewnew = (ListView) getView().findViewById(R.id.listviewFrag);
        final StableArrayAdapter adapter2 = new StableArrayAdapter(getContext(), android.R.layout.simple_list_item_1, newData);
        final ArrayList<String> xdata = newData;
        listviewnew.setAdapter(adapter2);

        listviewnew.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                CharSequence charSeq =((TextView)view).getText();
                if (view.getTag(R.id.active) != null) {
                    Object ob = view.getTag(R.id.active);

                    if (ob.toString().equals("activated")) {
                        view.setTag(R.id.active,"deactivated");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            view.setElevation(0);
                        }
                        Object originText = view.getTag(R.id.textAddit);
                        ((TextView)view).setText(originText.toString());
                    }
                    else if (ob.toString().equals("deactivated")) {
                        view.setTag(R.id.active,"activated");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            view.setElevation(8);
                        }
                        ((TextView) view).setHighlightColor(getResources().getColor(R.color.colorPrimary));
                        ((TextView)view).setText(charSeq + " : \n"+  senders[position][1]);
                    }
                } else {
                    view.setTag(R.id.textAddit,charSeq);
                    view.setTag(R.id.active,"activated");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        view.setElevation(8);
                    }
                    ((TextView) view).setHighlightColor(getResources().getColor(R.color.colorPrimary));
                    ((TextView)view).setText(charSeq + " : " + senders[position][1]);
                }
        Log.d(LOG_TAG,"Item was clicked( " + charSeq + " )");
//        ((TextView)view).setText(charSeq + " Heh");
            }
        });
    }

    public String loadSMS() throws JSONException {

            String json = null;
            try {
                InputStream is = getContext().getAssets().open("sms.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
            } catch (IOException ex) {
                Crashlytics.logException(ex);
                return null;
            }
            Log.d(LOG_TAG,"loadJSONFromAssets returns:" + json);
            Crashlytics.log("loadJSONFromAssets returns:" + json);
            return json;
        }
    }








