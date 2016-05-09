package cz.johrusk.showsmscode.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import org.w3c.dom.Text;

import cz.johrusk.showsmscode.R;


public class OneFragment extends Fragment {
    SharedPreferences sharedpreferences;

    public OneFragment() {
        // Required empty public constructor
    }




    @Override
    public void onStart() {

        Log.d("...........","OnStart");
        super.onStart();

        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(getContext())) {
            TextView tvOverlay = (TextView) getView().findViewById(R.id.tab_1_tv_1);
            tvOverlay.setBackgroundResource(R.color.colorAccent);
            tvOverlay.setText(R.string.tab_1_tv_OverlayNotAllowed);
            tvOverlay.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                   Intent overlayEnable = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getContext().getPackageName()));
                    startActivity(overlayEnable);
                }
            });
        } else {
            TextView tvOverlay = (TextView) getView().findViewById(R.id.tab_1_tv_1);
            tvOverlay.setBackgroundResource(R.color.colorPrimary);
            tvOverlay.setText(R.string.tab_1_tv_1);
        }
    }

    @Override
    public void onPause() {

        Log.d("...........","onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d("...........","onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {

        Log.d("...........","onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d("...........","onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d("...........","onDetach");
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.d("...........","onCreate");super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("...........","onCreateView");

        return inflater.inflate(R.layout.fragment_one, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("...........","onActivityCreated");

//        if (Build.VERSION.SDK_INT >= 23) {
//            if (Settings.canDrawOverlays(getContext())) {
//                Button btn = (Button) getView().findViewById(R.id.btnPermission);
//                btn.setVisibility(View.GONE);
//            }
//        } else {
//            Button btn = (Button) getView().findViewById(R.id.btnPermission);
//            btn.setVisibility(View.GONE);
//
//        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Crashlytics.log("resume started");
        Log.d("...........","OnResume");
    }


}

