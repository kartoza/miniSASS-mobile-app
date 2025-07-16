package com.rk.amii;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.rk.amii.shared.LanguageHelper;

public class MiniSASSApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Apply saved language globally
        applySavedLanguage();
    }

    @Override
    protected void attachBaseContext(Context base) {
        SharedPreferences prefs = base.getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        String savedLanguage = prefs.getString("language", "en");
        LanguageHelper.setLocale(base, savedLanguage);
        super.attachBaseContext(base);
    }

    private void applySavedLanguage() {
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        String savedLanguage = prefs.getString("language", "en");
        LanguageHelper.setLocale(this, savedLanguage);
    }
}
