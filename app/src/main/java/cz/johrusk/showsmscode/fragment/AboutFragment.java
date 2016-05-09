//package cz.johrusk.showsmscode.fragment;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import org.json.JSONException;
//
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//
//import cz.johrusk.showsmscode.MainActivity;
//import cz.johrusk.showsmscode.R;
//
//
//public class AboutFragment extends Fragment {
//
//   // public AboutFragment(){}
//    private Toolbar toolbar;
//    final String LOG_TAG = MainActivity.class.getName();
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        TextView tv = (TextView) findViewById(R.id.verView);
//        try {
//            String version = readFromFile();
//            tv.setText(version);
//        } catch (JSONException e) {e.printStackTrace();}
//
//    return inflater.inflate(R.layout.about_activity, container, false);
//    }
//    public String readFromFile() throws JSONException {
//        String ret = "";
//
//        try {
//            InputStream inputStream = openFileInput("version.txt");
//
//            if (inputStream != null) {
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                String receiveString = "";
//                StringBuilder stringBuilder = new StringBuilder();
//
//                while ((receiveString = bufferedReader.readLine()) != null) {
//                    stringBuilder.append(receiveString);
//                }
//
//                inputStream.close();
//                ret = stringBuilder.toString();
//            }
//        } catch (FileNotFoundException e) {
//            Log.e("login activity", "File not found: " + e.toString());
//        } catch (IOException e) {
//            Log.e("login activity", "Can not read file: " + e.toString());
//        }
//        Log.d(LOG_TAG,"Version Text View was set to " + ret);
//        return ret;
//    }
//}
