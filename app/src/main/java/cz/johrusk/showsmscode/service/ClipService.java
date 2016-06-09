package cz.johrusk.showsmscode.service;

import android.app.IntentService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

/**
 * Service which copy code so it can be pasted.
 *
 * @author Josef Hruska (pepa.hruska@gmail.com)
 */
public class ClipService extends IntentService {

    public ClipService() {
        super("ClipService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String str = intent.getStringExtra("code");

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("code", str);
        clipboard.setPrimaryClip(clip);
    }


}
