package com.rk.amii.workers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker.Result;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.work.Data;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.content.Intent;


import com.rk.amii.database.DBHandler;
import com.rk.amii.models.AssessmentModel;
import com.rk.amii.models.SitesModel;
import com.rk.amii.models.PhotoModel;
import com.rk.amii.models.UserModel;
import com.rk.amii.services.ApiService;
import com.rk.amii.shared.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TaskRunner extends Worker {
    public TaskRunner(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    private Integer submitAssessment(SitesModel site, AssessmentModel assessment, DBHandler dbHandler) {
        boolean isOnline = Utils.isNetworkAvailable(getApplicationContext());
        HashMap<String, String> onlineInvertMapping = Utils.getOnlineInvertMapping();
        if (isOnline) {
            JSONObject assessmentData = new JSONObject();
            JSONObject assessmentDataObject = new JSONObject();
            Map<String, File> imageFiles = new HashMap<>();
            try {
                ApiService service = new ApiService(getApplicationContext());

                ArrayList<PhotoModel> photos = dbHandler.getPhotoInfo(assessment.getAssessmentId());
                for (int i = 0; i < photos.size(); i++) {
                    PhotoModel currentPhoto = photos.get(i);

                    String imageKey = "pest_" + i + ":" + onlineInvertMapping.get(currentPhoto.getUserChoice());
                    File image = new File(currentPhoto.getPhotoLocation());

                    imageFiles.put(imageKey, image);
                    assessmentDataObject.put(
                            onlineInvertMapping.get(
                                    currentPhoto.getUserChoice()
                            ), true
                    );
                }

                JSONObject assessmentInputObject = new JSONObject();
                String[] locationLngLat = site.getSiteLocation().split(",");

                String createSiteOrObservation = "false";
                if (site.getOnlineSiteId().equals("0")) {
                    createSiteOrObservation = "true";
                }

                assessmentInputObject.put("riverName", site.getRiverName());
                assessmentInputObject.put("siteName", site.getSiteName());
                assessmentInputObject.put("siteDescription", site.getDescription());
                assessmentInputObject.put("rivercategory", site.getRiverType());
                assessmentInputObject.put("date", LocalDate.now().toString());
                assessmentInputObject.put("collectorsname", "");
                assessmentInputObject.put("notes", assessment.getNotes());
                assessmentInputObject.put("waterclaritycm", assessment.getWaterClarity());
                assessmentInputObject.put("watertemperatureOne", assessment.getWaterTemp());
                assessmentInputObject.put("ph", assessment.getPh());
                assessmentInputObject.put("dissolvedoxygenOne", assessment.getDissolvedOxygen());
                assessmentInputObject.put("dissolvedoxygenOneUnit", assessment.getDissolvedOxygenUnit());
                assessmentInputObject.put("electricalconduOne", assessment.getElectricalConductivity());
                assessmentInputObject.put("electricalconduOneUnit", assessment.getElectricalConductivityUnit());
                assessmentInputObject.put("latitude", locationLngLat[0]);
                assessmentInputObject.put("longitude", locationLngLat[1]);
                assessmentInputObject.put("selectedSite", site.getOnlineSiteId());
                assessmentInputObject.put("flag", "dirty");
                assessmentInputObject.put("ml_score", assessment.getMiniSassMLScore());


                assessmentDataObject.put("score", assessment.getMiniSassScore());
                assessmentDataObject.put("datainput", assessmentInputObject);
//
                assessmentData.put("data", assessmentDataObject.toString());
                assessmentData.put("siteId", site.getOnlineSiteId());
                assessmentData.put("create_site_or_observation", createSiteOrObservation);

                Integer onlineAssessmentId = service.createAssessment(imageFiles, assessmentData);

                if (onlineAssessmentId != 0) {
                    dbHandler.updateAssessmentUploaded(
                            String.valueOf(assessment.getAssessmentId()), onlineAssessmentId
                    );
                }
                return onlineAssessmentId;
            } catch (JSONException |IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private Integer submitSite(SitesModel site, DBHandler dbHandler) {
        // Add the site to the miniSASS website
        JSONObject siteObject = new JSONObject();
        JSONObject siteDetails = new JSONObject();
        Map<String, File> imageFiles = new HashMap<>();
        try {
            ApiService service = new ApiService(getApplicationContext());

            String[] locationLngLat = site.getSiteLocation().split(",");

            siteDetails.put("longitude", locationLngLat[1]);
            siteDetails.put("latitude", locationLngLat[0]);
            siteDetails.put("site_name", site.getSiteName());
            siteDetails.put("river_name", site.getRiverName());
            siteDetails.put("description", site.getDescription());
            siteDetails.put("river_cat", site.getRiverType().toLowerCase());

            Integer counter = 0;
            for(String imagePath : dbHandler.getSiteImagePathsBySiteId(site.getSiteId())) {
                String imageKey = "images_" + counter;
                File image = new File(imagePath);

                imageFiles.put(imageKey, image);
                counter +=1;
            }

            siteObject.put("site_data", siteDetails);

            JSONObject result = service.createSite(imageFiles, siteObject);
            Integer _onlineSiteId = result.has("gid") ? result.getInt("gid") : 0;
            dbHandler.updateSite(
                    Integer.toString(site.getSiteId()),
                    site.getSiteName(),
                    site.getSiteLocation(),
                    site.getRiverName(),
                    site.getDescription(),
                    site.getDate(),
                    site.getRiverType(),
                    result.has("country") ? result.getString("country") : ""
            );

            return _onlineSiteId;
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void uploadData(DBHandler dbHandler) {
        // Check if there's any data that needs syncing
        boolean hasUnuploadedData = false;
        try {
            // Check for sites that need uploading
            ArrayList<SitesModel> sites = dbHandler.getSites();
            for (int i = 0; i < sites.size(); i++) {
                SitesModel site = sites.get(i);
                String onlineSiteId = site.getOnlineSiteId();
                if (onlineSiteId.equals("0")) {
                    hasUnuploadedData = true;
                    break; // Found at least one, no need to check more
                }
            }

            // Check for assessments that need uploading (only if no sites found yet)
            if (!hasUnuploadedData) {
                ArrayList<AssessmentModel> assessments = dbHandler.getAssessments();
                for (int j = 0; j < assessments.size(); j++) {
                    AssessmentModel assessment = assessments.get(j);
                    if (assessment.getOnlineAssessmentId().equals(0)) {
                        hasUnuploadedData = true;
                        break; // Found at least one, no need to check more
                    }
                }
            }

            // Show toast only if there's data to sync
            if (hasUnuploadedData) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(
                                getApplicationContext(),
                                "Syncing Sites and Observations!",
                                Toast.LENGTH_SHORT
                        ).show());
            }

            // Now do the actual syncing
            for (int i = 0; i < sites.size(); i++) {
                Integer _onlineSiteId = 0;
                SitesModel site = sites.get(i);
                String onlineSiteId = site.getOnlineSiteId();
                if (onlineSiteId.equals("0")) {
                    _onlineSiteId = submitSite(site, dbHandler);
                }

                if (_onlineSiteId != 0) {
                    dbHandler.updateSiteUploaded(
                            String.valueOf(site.getSiteId()), _onlineSiteId
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
        }

        ArrayList<AssessmentModel> assessments = dbHandler.getAssessments();
        for (int j = 0; j < assessments.size(); j++) {
            AssessmentModel assessment = assessments.get(j);
            Integer siteId = dbHandler.getSiteIdByAssessmentId(assessment.getAssessmentId());
            SitesModel site = dbHandler.getSiteById(siteId);
            if (assessment.getOnlineAssessmentId().equals(0)) {
                submitAssessment(site, assessment, dbHandler);
            }
        }

        // Show success notification
        if (hasUnuploadedData) {
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(
                            getApplicationContext(),
                            "Data synced successfully!",
                            Toast.LENGTH_SHORT
                    ).show());
        }
    }

    private void downloadData(DBHandler dbHandler) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(
                        getApplicationContext(),
                        "Syncing Sites and Observations!",
                        Toast.LENGTH_SHORT
                ).show());
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        dbHandler.dropAssessmentRelatedTables(db);
        dbHandler.createAssessmentRelatedTables(db);

        ApiService service = new ApiService(getApplicationContext());
        service.getPaginatedSites(result -> {
            try {
                JSONObject sites = new JSONObject(result);
                JSONArray sitesArray = sites.getJSONArray("results");
                for (int i = 0; i < sitesArray.length(); i++) {
                    JSONObject onlineSite = sitesArray.getJSONObject(i);

                    String siteNameValue = onlineSite.getString("site_name");
                    String locationValue = onlineSite.getString("the_geom").
                            replace("SRID=4326;POINT (", "").
                            replace(")", "").
                            replace(" ", ",");
                    String riverNameValue = onlineSite.getString("river_name");
                    String descriptionValue = onlineSite.getString("description");
                    String dateValue = onlineSite.getString("time_stamp").split("T")[0];
                    String riverTypeValue = onlineSite.getString("river_cat");
                    String countryValue = onlineSite.getString("country");
                    Integer userValue = onlineSite.getInt("user");
                    Integer onlineSiteId = onlineSite.getInt("gid");

                    long savedSiteId = dbHandler.addNewSite(siteNameValue, locationValue, riverNameValue,
                            descriptionValue, dateValue, riverTypeValue, countryValue, userValue);
                    dbHandler.updateSiteUploaded(String.valueOf(savedSiteId), onlineSiteId);

                    try {

                        service.getAssessmentsBySiteById(assesments -> {
                            try {
                                JSONObject data = new JSONObject(assesments);
                                JSONArray onlineAssessments = new JSONArray(data.getString("observations"));

                                System.out.println(data);
                                System.out.println(onlineAssessments);

                                if(onlineAssessments.length() > 0) {
                                    ArrayList<AssessmentModel> assessments = new ArrayList<>();
                                    // Add reversed assessment
                                    for (int j = onlineAssessments.length() - 1; j >= 0; j--) {

                                        JSONObject assessmentJSON = new JSONObject(onlineAssessments.get(j).toString());
                                        AssessmentModel assessment = new AssessmentModel(
                                                Integer.parseInt(assessmentJSON.getString("gid")),
                                                Integer.parseInt(assessmentJSON.getString("gid")),
                                                Float.parseFloat(assessmentJSON.getString("score")),
                                                Float.parseFloat("0.00"),
                                                assessmentJSON.getString("collector_name"),
                                                assessmentJSON.getString("organisationname"),
                                                assessmentJSON.getString("obs_date"),
                                                assessmentJSON.getString("comment"),
                                                assessmentJSON.getString("ph"),
                                                assessmentJSON.getString("water_temp"),
                                                assessmentJSON.getString("diss_oxygen"),
                                                assessmentJSON.getString("diss_oxygen_unit"),
                                                assessmentJSON.getString("elec_cond"),
                                                assessmentJSON.getString("elec_cond_unit"),
                                                assessmentJSON.getString("water_clarity")
                                        );
                                        dbHandler.addNewAssessment(
                                                assessment.getMiniSassScore().toString(),
                                                assessment.getMiniSassMLScore().toString(),
                                                assessment.getNotes(),
                                                assessment.getCollectorsName(),
                                                assessment.getOrganisation(),
                                                assessment.getObservationDate(),
                                                assessment.getPh(),
                                                assessment.getWaterTemp(),
                                                assessment.getDissolvedOxygen(),
                                                assessment.getDissolvedOxygenUnit(),
                                                assessment.getElectricalConductivity(),
                                                assessment.getElectricalConductivityUnit(),
                                                assessment.getWaterClarity(),
                                                assessmentJSON.getInt("gid")
                                        );
                                        dbHandler.addNewSiteAssessment((int) savedSiteId, assessment.getAssessmentId());
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, String.valueOf(onlineSiteId));
                    } catch (Exception e) {
                        System.out.println("ERR: " + e);
                    }
                }

                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(
                                getApplicationContext(),
                                "Synced sucessfully!",
                                Toast.LENGTH_SHORT
                        ).show());

                // Send broadcast to refresh dashboard
                Intent intent = new Intent("SYNC_COMPLETED");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, "?my_sites=true&paginated=true");
    }

    @NonNull
    @Override
    public Result doWork() {
        Data inputData = getInputData();
        boolean uploadOnly = inputData.getBoolean("uploadOnly", true);
        boolean isOnline = Utils.isNetworkAvailable(getApplicationContext());

        DBHandler dbHandler = new DBHandler(getApplicationContext());
        if (isOnline) {
            uploadData(dbHandler);
            if (!uploadOnly) {
                downloadData(dbHandler);
            }
        }

        return Result.success();
    }
}