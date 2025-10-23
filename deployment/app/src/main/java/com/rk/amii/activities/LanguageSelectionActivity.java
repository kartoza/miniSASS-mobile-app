package com.rk.amii.activities;

import android.content.Intent;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rk.amii.R;
import com.rk.amii.databinding.ActivityLanguageSelectionBinding;
import com.rk.amii.shared.LanguageHelper;
import com.rk.amii.MainActivity;

public class LanguageSelectionActivity extends AppCompatActivity {

    private ActivityLanguageSelectionBinding binding;
    private String[] languageCodes = {"en", "pt", "zu"};
    private String[] languageNames = {"English", "Português", "IsiZulu"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLanguageSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets status = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            int top = status.top;
            v.setPadding(v.getPaddingLeft(), top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets status = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            int top = status.top;
            v.setPadding(v.getPaddingLeft(), top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

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
