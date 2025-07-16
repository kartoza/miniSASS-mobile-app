package com.rk.amii.activities;

import android.content.Intent;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.rk.amii.R;
import com.rk.amii.shared.LanguageHelper;
import com.rk.amii.MainActivity;

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
        builder.setTitle("Select Language");

        builder.setItems(languageNames, (dialog, which) -> {
            String selectedLanguage = languageCodes[which];
            saveLanguagePreference(selectedLanguage);
            LanguageHelper.setLocale(this, selectedLanguage);

            // Restart the main activity with clear task
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
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
