package cz.johrusk.showsmscode;

import android.app.IntentService;
import android.content.Intent;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

public class SmsUploadService extends IntentService {
//TODO:Nefunguje share action
    public SmsUploadService(){
        super("SmsUploadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

         String smsContent = intent.getStringExtra(Intent.EXTRA_TEXT);

        Answers.getInstance().logCustom(new CustomEvent("SMS Sended")
                .putCustomAttribute("SMS BODY: ", smsContent));
    }
}
