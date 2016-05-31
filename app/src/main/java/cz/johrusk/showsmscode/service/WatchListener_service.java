    package cz.johrusk.showsmscode.service;


    import android.util.Log;

    import com.google.android.gms.common.api.GoogleApiClient;
    import com.google.android.gms.wearable.DataEvent;
    import com.google.android.gms.wearable.DataEventBuffer;
    import com.google.android.gms.wearable.DataMap;
    import com.google.android.gms.wearable.DataMapItem;
    import com.google.android.gms.wearable.Wearable;
    import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Pepa on 21.05.2016.
 */

public class WatchListener_service extends WearableListenerService {
    @Override
    public void onCreate() {
        super.onCreate();
       GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }
    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.d("prijato","number is: ");

        for (DataEvent dataEvent : dataEventBuffer) {
        if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
            DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
            String path = dataEvent.getDataItem().getUri().getPath();
            if (path.equals("/number")){
                int number = dataMap.getInt("numa");
                long time = dataMap.getInt("timestamp");
                Log.d("prijato","number is: " + number);

            }
        }
    }
    }
}
