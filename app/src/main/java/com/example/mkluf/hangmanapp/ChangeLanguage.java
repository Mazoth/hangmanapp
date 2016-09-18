package com.example.mkluf.hangmanapp;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;

import java.util.Locale;

/**
 * This activity is intended to change the language on all activities
 */
public class ChangeLanguage extends Application {
    public static void changeLanguage(Activity activity, Locale locale) {
        final Resources res = activity.getResources();
        final Configuration cfg = res.getConfiguration();
        cfg.locale = locale; // Norwegian
        res.updateConfiguration(cfg, null);
        activity.recreate();
    }
}
