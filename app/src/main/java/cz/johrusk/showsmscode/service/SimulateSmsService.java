package cz.johrusk.showsmscode.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cz.johrusk.showsmscode.core.App;
import timber.log.Timber;


/**
 * Service which simulates what happen when user receive SMS which is contained in SMS.
 *
 * @author Josef Hruska (pepa.hruska@gmail.com)
 */
public class SimulateSmsService extends IntentService {

    public SimulateSmsService() {
        super("SimulateSmsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context c = App.get();

        Intent msgHandler = new Intent(c, MsgHandlerService.class);
        String msgContent = "TEST code: 997192";
        String msgSender = "123456";
        Bundle msg = new Bundle();

        Timber.d("Content of SMS " + msgContent + " / " + msgSender);
        msg.putStringArray("msg", new String[]{msgSender, msgContent});
        msgHandler.putExtra("msg", msg);
        c.startService(msgHandler);
    }
}
