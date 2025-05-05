package com.rk.amii;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rk.amii.activities.AuthenticationActivity;
import com.rk.amii.databinding.ActivityMainBinding;
import com.rk.amii.services.ApiService;
import com.rk.amii.shared.Utils;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FragmentRefreshListener fragmentRefreshListener;
    private boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        isOnline = Utils.isNetworkAvailable(this);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_landing, R.id.navigation_home, R.id.navigation_sites, R.id.navigation_profile, R.id.navigation_about)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        Boolean isAgreedToPrivacyPolicy = (Boolean) getIntent().getExtras().get("is_agreed_to_privacy_policy");
        if (isAgreedToPrivacyPolicy == false) {
            showConsentDialog();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isOnline = Utils.isNetworkAvailable(this);
        if(getFragmentRefreshListener()!= null){
            getFragmentRefreshListener().onRefresh();
        }
    }

    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    public interface FragmentRefreshListener{
        void onRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_menu, menu);

        // Find the logout button inside the custom action layout
        MenuItem logoutItem = menu.findItem(R.id.logoutBtn);
        View actionView = logoutItem.getActionView();

        if (actionView != null) {
            Button logoutButton = actionView.findViewById(R.id.logoutBtn);
            if (logoutButton != null) {
                logoutButton.setOnClickListener(v -> handleLogout());
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logoutBtn) {
            handleLogout();
            return true;
        } else if (id == R.id.privacyPolicy) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://minisass.sta.do.kartoza.com/#/privacy-policy#privacy-policy-title"));
            startActivity(browserIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showConsentDialog() {
        TextView message = new TextView(this);
        message.setText(
                "We use cookies and analytics to improve your experience. By clicking continue, you agree to our Privacy Policy."
        );
        message.setPadding(50, 40, 50, 0);
        message.setTextSize(16f);
        message.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableString spannable = new SpannableString(message.getText());
        int start = message.getText().toString().indexOf("Privacy Policy");
        if (start >= 0) {
            spannable.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://minisass.sta.do.kartoza.com/#/privacy-policy#privacy-policy-title"));
                    startActivity(browserIntent);
                }
            }, start, start + "Privacy Policy".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            message.setText(spannable);
        }

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Privacy Notice")
                .setView(message)
                .setCancelable(false)
                .setPositiveButton("Continue", (dialog, which) -> {
                    ApiService service = new ApiService(MainActivity.this);
                    service.sendPrivacyConsent(true);
                    Toast.makeText(MainActivity.this, "You accepted the privacy policy.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }).show();
    }


    private void handleLogout() {
        if (isOnline) {
            ApiService service = new ApiService(this);
            service.logout();
            Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Could not logout")
                    .setMessage("Could not logout. Connect your device to the internet to logout.")
                    .setNeutralButton("Ok", (dialog, which) -> {
                    })
                    .setIcon(R.drawable.ic_baseline_wifi_off_24)
                    .show();
        }
    }
}
