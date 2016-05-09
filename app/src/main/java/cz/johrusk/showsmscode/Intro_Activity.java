package cz.johrusk.showsmscode;

import android.Manifest;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import java.util.ArrayList;

/**
 * Created by Pepa on 03.05.2016.
 */
public class Intro_Activity extends AppIntro {

    // Please DO NOT override onCreate. Use init.
    @Override
    public void init(Bundle savedInstanceState) {



//        Drawable image = getResources().getDrawable(R.drawable.ic_security);
//        // Add your slide's fragments here.
//        // AppIntro will automatically generate the dots indicator and buttons.
//        //addSlide(first_fragment);
        ArrayList<String> titles = new ArrayList<String>();
        titles.add(getResources().getString(R.string.intro_title_1));
        titles.add(getResources().getString(R.string.intro_title_2));
        titles.add(getResources().getString(R.string.intro_title_3));
        titles.add(getResources().getString(R.string.intro_title_4));
        titles.add(getResources().getString(R.string.intro_title_5));
        titles.add(getResources().getString(R.string.intro_title_6));
        titles.add(getResources().getString(R.string.intro_title_7));
        titles.add(getResources().getString(R.string.intro_title_8));
        titles.add(getResources().getString(R.string.intro_title_9));

        ArrayList<String> texts = new ArrayList<String>();
        texts.add(getResources().getString(R.string.intro_text_1));
        texts.add(getResources().getString(R.string.intro_text_2));
        texts.add(getResources().getString(R.string.intro_text_3));
        texts.add(getResources().getString(R.string.intro_text_4));
        texts.add(getResources().getString(R.string.intro_text_5));
        texts.add(getResources().getString(R.string.intro_text_6));
        texts.add(getResources().getString(R.string.intro_text_7));
        texts.add(getResources().getString(R.string.intro_text_8));
        texts.add(getResources().getString(R.string.intro_text_9));

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.

        addSlide(AppIntroFragment.newInstance(titles.get(0), texts.get(0), R.drawable.thumbup, ContextCompat.getColor(this,R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance(titles.get(1), texts.get(1), R.drawable.eyw, ContextCompat.getColor(this,R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance(titles.get(2), texts.get(2), R.drawable.code, ContextCompat.getColor(this,R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance(titles.get(3), texts.get(3), R.drawable.imediately, ContextCompat.getColor(this,R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance(titles.get(4), texts.get(4), R.drawable.copy, ContextCompat.getColor(this,R.color.colorPrimary)));
        if (Build.VERSION.SDK_INT >= 23) {
            addSlide(AppIntroFragment.newInstance(titles.get(5), texts.get(5), R.drawable.lock, ContextCompat.getColor(this, R.color.colorPrimary)));
            addSlide(AppIntroFragment.newInstance(titles.get(6), texts.get(6), R.drawable.lock, ContextCompat.getColor(this, R.color.colorPrimary)));
        }
        addSlide(AppIntroFragment.newInstance(titles.get(7), texts.get(7), R.drawable.adddb, ContextCompat.getColor(this,R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance(titles.get(8), texts.get(8), R.drawable.complete, ContextCompat.getColor(this,R.color.colorPrimary)));

        askForPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS,Manifest.permission.RECEIVE_BOOT_COMPLETED}, 6);
    setZoomAnimation();
        // OPTIONAL METHODS
        // Override bar/separator color.
      //setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));
        showStatusBar(false);

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed() {
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed() {
       finish();
    }

    @Override
    public void onSlideChanged() {
        // Do something when the slide changes.
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }

}
