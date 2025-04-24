package com.rk.amii.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.rk.amii.workers.SyncOfflineSitesWorker;


//public class NetworkChangeReceiver extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if (Utils.isNetworkAvailable(context)) {
//            DBHandler dbHandler = new DBHandler(context);
//            ArrayList<SitesModel> sites = dbHandler.getSites(
//                "onlineSiteId = ?",
//                new String[]{"0"}
//            );
//            // Sync offline sites
////            Toast.makeText(context, "Site uploaded: " + site.getSiteName(), Toast.LENGTH_SHORT).show();
//            Toast.makeText(context, "Site uploaded: ", Toast.LENGTH_SHORT).show();
//        }
//    }
//}

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Enqueue the sync worker when network is back

        Log.d("Network change", "WorkManager started");
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest uploadWork =
                new OneTimeWorkRequest.Builder(SyncOfflineSitesWorker.class)
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance(context).enqueue(uploadWork);
    }
}
