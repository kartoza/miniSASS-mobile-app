package com.rk.amii.workers;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker.Result;  // Add this import for Result
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.rk.amii.database.DBHandler;
import com.rk.amii.models.SitesModel;
import com.rk.amii.services.ApiService;
import com.rk.amii.shared.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


public class TaskRunner extends Worker {

    private String response = "Could not upload information";

    public TaskRunner(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Do your background syncing logic here

        boolean isOnline = Utils.isNetworkAvailable(getApplicationContext());

//        new Handler(Looper.getMainLooper()).post(() ->
//                Toast.makeText(
//                        getApplicationContext(),
//                        "Syncing Sites and Observations!",
//                        Toast.LENGTH_SHORT
//                ).show());

        if (isOnline) {
            try {
                DBHandler dbHandler = new DBHandler(getApplicationContext());
                ArrayList<SitesModel>sites = dbHandler.getSites();
                for (int i = 0; i < sites.size(); i++) {
                    SitesModel site = sites.get(i);
                    String onlineSiteId = site.getOnlineSiteId();

                    if (onlineSiteId.equals("0")) {
                        // Add the site to the miniSASS website
                        JSONObject siteObject = new JSONObject();
                        JSONObject siteDetails = new JSONObject();
                        JSONArray siteImageObjects = new JSONArray();
                        Map<String, File> imageFiles = new HashMap<>();
                        try {
                            ApiService service = new ApiService(getApplicationContext());

                            String[] locationLngLat = site.getSiteLocation().split(",");

                            siteDetails.put("the_geom", "SRID=4326;POINT ("+locationLngLat[0]+" "+locationLngLat[1]+")");
                            siteDetails.put("site_name", site.getSiteName());
                            siteDetails.put("river_name", site.getRiverName());
                            siteDetails.put("description", site.getDescription());
                            siteDetails.put("river_cat", site.getRiverType().toLowerCase());
                            String siteRiver = site.getSiteName() + " - " + site.getRiverName();

                            Integer counter = 0;
                            for(String imagePath : dbHandler.getSiteImagePathsBySiteId(site.getSiteId())) {
                                String imageKey = "images_" + counter;
                                File image = new File(imagePath);

                                imageFiles.put(imageKey, image);
                                counter +=1;
                            }

                            siteObject.put("site_data", siteDetails);

                            Integer _onlineSiteId = service.createSite(imageFiles, siteObject);
                            dbHandler.updateSiteUploaded(String.valueOf(site.getSiteId()), _onlineSiteId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                response = "Uploaded";
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }

//        new Handler(Looper.getMainLooper()).post(() ->
//                Toast.makeText(getApplicationContext(), "Sites synced!", Toast.LENGTH_SHORT).show());

        return Result.success();
    }
}
