package com.rk.amii.workers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker.Result;  // Add this import for Result
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.rk.amii.database.DBHandler;
import com.rk.amii.models.AssessmentModel;
import com.rk.amii.models.SitesModel;
import com.rk.amii.models.PhotoModel;
import com.rk.amii.services.ApiService;
import com.rk.amii.shared.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TaskRunner extends Worker {

    private String response = "Could not upload information";

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

            Integer _onlineSiteId = service.createSite(imageFiles, siteObject);
            return _onlineSiteId;
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        // Do your background syncing logic here
        boolean isOnline = Utils.isNetworkAvailable(getApplicationContext());

        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(
                        getApplicationContext(),
                        "Syncing Sites and Observations!",
                        Toast.LENGTH_SHORT
                ).show());
        DBHandler dbHandler = new DBHandler(getApplicationContext());
        if (isOnline) {
            try {
                ArrayList<SitesModel>sites = dbHandler.getSites();
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
            response = "Uploaded";
        }

        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(getApplicationContext(), "Sites synced!", Toast.LENGTH_SHORT).show());

        return Result.success();
    }
}
