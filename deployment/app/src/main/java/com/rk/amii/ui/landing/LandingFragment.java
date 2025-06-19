package com.rk.amii.ui.landing;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Data;

import com.rk.amii.R;
import com.rk.amii.shared.Utils;
import com.rk.amii.workers.TaskRunner;

import java.util.ArrayList;


public class LandingFragment extends Fragment {

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private boolean isOnline;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        isOnline = Utils.isNetworkAvailable(this.getContext());

        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA
        };

        requestPermissionsIfNecessary(permissions);

        // Then in your method:
        Data inputData = new Data.Builder()
                .putBoolean("uploadOnly", true)
                .build();

        OneTimeWorkRequest uploadRequest = new OneTimeWorkRequest.Builder(TaskRunner.class)
                .setInputData(inputData)
                .build();

        // Use unique work to ensure it only runs once
        WorkManager.getInstance(requireContext())
                .enqueueUniqueWork(
                        "task_runner_unique_work",  // Unique name for this work
                        ExistingWorkPolicy.KEEP,    // KEEP means if it's already running, don't start a new one
                        uploadRequest
                );

        return inflater.inflate(R.layout.fragment_landing, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        isOnline = Utils.isNetworkAvailable(getContext());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this.getActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this.getContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this.getActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnWatchVideo = view.findViewById(R.id.btn_watch_video);
        btnWatchVideo.setOnClickListener(v -> {
            String url = "https://youtu.be/eUAr6n5gSqE?si=qd4I3HzsQmAPHxHp";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });
    }

}