package com.rk.amii.workers;

import android.content.Context;
import android.widget.Toast;

import com.rk.amii.database.DBHandler;
import com.rk.amii.models.SitesModel;
import com.rk.amii.shared.Utils;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;

public class SyncOfflineSitesWorker extends Worker {

    public SyncOfflineSitesWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("SyncOfflineSitesWorker", "WorkManager started");
        Context context = getApplicationContext();
        if (Utils.isNetworkAvailable(context)) {
            DBHandler dbHandler = new DBHandler(context);
            ArrayList<SitesModel> sites = dbHandler.getSites(
                "onlineSiteId = ?",
                new String[]{"0"}
            );
            // Sync offline sites
//            Toast.makeText(context, "Site uploaded: " + site.getSiteName(), Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "Site uploaded: ", Toast.LENGTH_SHORT).show();
        }

        return Result.success(); // or retry() if needed
    }
}
