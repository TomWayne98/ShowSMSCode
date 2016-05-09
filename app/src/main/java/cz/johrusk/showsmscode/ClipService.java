package cz.johrusk.showsmscode;

import android.app.IntentService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

/**
 * This class add the code to clipboard.
 */
public class ClipService extends IntentService {

    public ClipService() {
        super("ClipService");
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
