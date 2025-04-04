package com.rk.amii;

import android.app.ActionBar;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.rk.amii.activities.AuthenticationActivity;
import com.rk.amii.activities.CreateNewSampleActivity;
import com.rk.amii.databinding.ActivityMainBinding;
import com.rk.amii.services.ApiService;
import com.rk.amii.shared.Utils;

import java.util.zip.Inflater;

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
                R.id.navigation_landing, R.id.navigation_home, R.id.navigation_sites, R.id.navigation_about)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public void onResume() {
        super.onResume();
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