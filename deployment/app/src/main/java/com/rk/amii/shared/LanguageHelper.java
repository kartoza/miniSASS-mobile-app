package com.rk.amii.shared;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import java.util.Locale;

public class LanguageHelper {

    /**
     * Set the app locale to the specified language
     * @param context Application context
     * @param languageCode Language code (e.g., "en", "es", "pt")
     */
    public static void setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config);
        }

        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    /**
     * Get the current language code
     * @param context Application context
     * @return Current language code
     */
    public static String getCurrentLanguage(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0).getLanguage();
        } else {
            return context.getResources().getConfiguration().locale.getLanguage();
        }
    }

    /**
     * Get display name for language code
     * @param languageCode Language code
     * @return Display name
     */
    public static String getLanguageDisplayName(String languageCode) {
        switch (languageCode) {
            case "en":
                return "English";
            case "pt":
                return "Português";
            case "zu":
                return "IsiZulu";
//            case "es":
//                return "Español";
//            case "fr":
//                return "Français";
//            case "af":
//                return "Afrikaans";
            default:
                return languageCode;
        }
    }
}
