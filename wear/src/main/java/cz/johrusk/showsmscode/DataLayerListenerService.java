/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.johrusk.showsmscode;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.google.android.gms.wearable.MessageApi;
import com.patloew.rxwear.RxWear;
import com.patloew.rxwear.transformers.MessageEventGetDataMap;
import timber.log.Timber;

/**
 * Service which receives String containing code and sender from WearActivity.
 *
 * @author Josef Hruska (pepa.hruska@gmail.com)
 */
public class DataLayerListenerService extends Service {
    final static String LOG_TAG = DataLayerListenerService.class.getSimpleName();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RxWear.init(this);
        RxWear.Message.listen("/dataMap", MessageApi.FILTER_LITERAL)
                .compose(MessageEventGetDataMap.noFilter())
                .subscribe(dataMap -> {
                    String message = dataMap.getString("message", getString(R.string.no_message_info));
                    Timber.d("Message received: " + message);
                    if (!message.equals("no_msg")) {
                        Intent startIntent = new Intent(this, ShowActivity.class);
                        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startIntent.putExtra("code", message);
                        startActivity(startIntent);
                        stopSelf();
                    }
                });
        Timber.d("DataLayer started");
        return super.onStartCommand(intent, flags, startId);
    }
}


