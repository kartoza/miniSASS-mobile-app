package com.rk.amii.activities;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.rk.amii.R;
import com.rk.amii.shared.LanguageHelper;

public class LanguageSelectionActivity extends AppCompatActivity {

    private String[] languageCodes = {"en", "pt", "zu"};
    private String[] languageNames = {"English", "Português", "IsiZulu"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set title using existing ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.select_language));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Show language selection dialog immediately
        showLanguageSelectionDialog();
    }

    private void showLanguageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.select_language));

        builder.setItems(languageNames, (dialog, which) -> {
            String selectedLanguage = languageCodes[which];
            saveLanguagePreference(selectedLanguage);
            LanguageHelper.setLocale(this, selectedLanguage);

            // Finish and recreate calling activity
            setResult(RESULT_OK);
            finish();
        });

        builder.setOnCancelListener(dialog -> finish());
        builder.setOnDismissListener(dialog -> finish());

        builder.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void saveLanguagePreference(String languageCode) {
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        prefs.edit().putString("language", languageCode).apply();
    }
}
