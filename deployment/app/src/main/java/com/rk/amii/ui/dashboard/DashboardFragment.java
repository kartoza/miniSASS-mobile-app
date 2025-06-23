package com.rk.amii.ui.dashboard;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rk.amii.R;
import com.rk.amii.activities.CreateNewSiteActivity;
import com.rk.amii.adapters.SitesAdapter;
import com.rk.amii.database.DBHandler;
import com.rk.amii.databinding.FragmentDashboardBinding;
import com.rk.amii.models.SitesModel;
import com.rk.amii.models.UserModel;
import com.rk.amii.workers.TaskRunner;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private ArrayList<SitesModel> sites;
    private RecyclerView siteView;
    private SitesAdapter sitesAdapter;
    private DBHandler dbHandler;
    private String currentLocation;
    private View view;
    private TextView noSites;
    int LOCATION_REFRESH_TIME = 5000;
    int LOCATION_REFRESH_DISTANCE = 500;
    LocationManager location_manager;
    private boolean getLocationAndContinue;
    private androidx.appcompat.app.AlertDialog locationDialog;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            currentLocation = lat + "," + lng;

            if (getLocationAndContinue) {
                getLocationAndContinue = false;
                Intent i = new Intent(DashboardFragment.this.getActivity(), CreateNewSiteActivity.class);
                i.putExtra("location", currentLocation);
                startActivity(i);
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        getActivity().setTitle("Site Detail");

        if (ActivityCompat.checkSelfPermission(DashboardFragment.this.getActivity(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            requestPermission();
        }

        location_manager = (LocationManager) DashboardFragment.this.getContext().getSystemService(Context.LOCATION_SERVICE);
        location_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);

        currentLocation = null;

        getSites();

        siteView = view.findViewById(R.id.rvSites);
        FloatingActionButton addNewSiteFAB = view.findViewById(R.id.idFABadd);
        FloatingActionButton syncFAB = view.findViewById(R.id.idFABsync);

        prepareSites();

        syncFAB.setOnClickListener( v -> {
            // Then in your method:
            Data inputData = new Data.Builder()
                    .putBoolean("uploadOnly", false)
                    .build();

            OneTimeWorkRequest uploadRequest = new OneTimeWorkRequest.Builder(TaskRunner.class)
                    .setInputData(inputData)
                    .build();

            // Use unique work to ensure it only runs once
            WorkManager.getInstance(requireContext())
                    .enqueueUniqueWork(
                            "sync_data",  // Unique name for this work
                            ExistingWorkPolicy.KEEP,    // KEEP means if it's already running, don't start a new one
                            uploadRequest
                    );
        });

        addNewSiteFAB.setOnClickListener(v -> {

            boolean location_enabled = false;

            try {

                location_enabled = location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!location_enabled) {
                    new AlertDialog.Builder(DashboardFragment.this.getContext())
                            .setTitle("Location")
                            .setMessage("Your location services seems to be turned off. Please turn on your location services to continue.")
                            .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_map)
                            .show();
                    return;
                }
            } catch(Exception e) {
                System.out.println("Could not determine if location services are enabled.");
            }

            if (currentLocation != null) {
                Intent i = new Intent(DashboardFragment.this.getActivity(), CreateNewSiteActivity.class);
                i.putExtra("location", currentLocation);
                startActivity(i);
            } else {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(DashboardFragment.this.getActivity());
                builder.setTitle("Getting Location");
                builder.setMessage("Trying to get your location please wait.");
                locationDialog = builder.show();
                getLocationAndContinue = true;
            }
        });

        return view;
    }

    private void getSites() {
        dbHandler = new DBHandler(DashboardFragment.this.getActivity());

        UserModel user = dbHandler.getUserProfile();
        sites = dbHandler.getSites(
                "user_id = ?",
                new String[]{String.valueOf(user.getUserId())}
        );

        noSites = view.findViewById(R.id.idNoSites);

        if (sites.size() > 0) {
            noSites.setVisibility(View.INVISIBLE);
        }
    }

    private void prepareSites() {
        sitesAdapter = new SitesAdapter (this.getContext(), sites);
        siteView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        siteView.setAdapter(sitesAdapter);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(DashboardFragment.this.getActivity(), new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    public void onResume() {
        if (locationDialog != null) {
            locationDialog.dismiss();
        }
        getSites();
        prepareSites();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}