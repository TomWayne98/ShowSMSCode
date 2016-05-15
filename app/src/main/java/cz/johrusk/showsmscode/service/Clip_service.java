package cz.johrusk.showsmscode.service;

import android.app.IntentService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

/**
 * This class add the code to clipboard.
 */
public class Clip_service extends IntentService {

    public Clip_service() {
        super("Clip_service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String str = intent.getStringExtra("code");
        CharSequence code = str;

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("code",code);
        clipboard.setPrimaryClip(clip);
    }




}
