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
import android.util.Log;

import pl.tajchert.buswear.EventBus;

/**
 * Listens to DataItems and Messages from the local node.
 */
public class  DataLayerListenerService extends Service {
    final static String LOG_TAG = DataLayerListenerService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        EventBus.getDefault().register(this);
        Log.d(LOG_TAG, "DataLayer started");
        return super.onStartCommand(intent, flags, startId);
    }

    public void onEvent(String strg) {
        Log.d(LOG_TAG,"On-Event started" + strg);
        if (!strg.equals("text")) {
            Intent startIntent = new Intent(this, Show_activity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startIntent.putExtra("code", strg);
            startActivity(startIntent);
            stopSelf();
        }
    }
}