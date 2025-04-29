package com.rk.amii.ui.landing;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rk.amii.R;
import com.rk.amii.activities.CreateNewSampleActivity;
import com.rk.amii.database.DBHandler;
import com.rk.amii.models.AssessmentModel;
import com.rk.amii.models.LocationPinModel;
import com.rk.amii.models.SitesModel;
import com.rk.amii.services.ApiService;
import com.rk.amii.shared.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
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

        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();

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

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String response = "Could not upload information";
        AlertDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Uploading sites...");
            if (isOnline) {
                try {
                    DBHandler dbHandler = new DBHandler(getContext());
                    ArrayList<SitesModel>sites = dbHandler.getSites();
                    for (int i = 0; i < sites.size(); i++) {
                        SitesModel site = sites.get(i);
                        String onlineSiteId = site.getOnlineSiteId();

                        if (onlineSiteId.equals("0")) {
                            // Add the site to the miniSASS website
                            JSONObject siteObject = new JSONObject();
                            JSONObject siteDetails = new JSONObject();
                            JSONArray siteImageObjects = new JSONArray();
                            try {
                                ApiService service = new ApiService(getContext());

                                String[] locationLngLat = site.getSiteLocation().split(",");

                                siteDetails.put("the_geom", "SRID=4326;POINT ("+locationLngLat[0]+" "+locationLngLat[1]+")");
                                siteDetails.put("site_name", site.getSiteName());
                                siteDetails.put("river_name", site.getRiverName());
                                siteDetails.put("description", site.getDescription());
                                siteDetails.put("river_cat", site.getRiverType().toLowerCase());
                                //siteDetails.put("user", "19");

                                Integer counter = 0;
                                for(String imagePath : dbHandler.getSiteImagePathsBySiteId(site.getSiteId())) {
                                    File image = new File(imagePath);
                                    byte[] fileData = new byte[(int) image.length()];
                                    DataInputStream dis = new DataInputStream(new FileInputStream(image));
                                    JSONObject temp = new JSONObject();
                                    temp.put("image_"+counter, fileData);
                                    siteImageObjects.put(temp);
                                    dis.close();
                                    counter +=1;
                                }

                                siteObject.put("site_data", siteDetails);
                                siteObject.put("images", siteImageObjects);

                                Integer _onlineSiteId = service.createSite(siteObject);
                                dbHandler.updateSiteUploaded(String.valueOf(site.getSiteId()), _onlineSiteId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    response = "Uploaded";
                } catch (Exception e) {
                    e.printStackTrace();
                    response = e.getMessage();
                }
            }
            return response;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            //progressDialog.dismiss();

        }


        @Override
        protected void onPreExecute() {
            /*new AlertDialog.Builder(getContext())
                    .setTitle("Upload Images")
                    .setMessage("You are back online. Do you want to upload the assessment images? They are " + totalFileSize + " MB in size.")
                    .setNegativeButton("No", (dialog, which) -> {
                        saveAssessment((int)siteId, false);
                    })
                    .setPositiveButton("Yes", (dialog, which) -> {
                        saveAssessment((int)siteId, true);
                    })
                    .setIcon(R.drawable.ic_baseline_image_24)
                    .show();*/
        }


        @Override
        protected void onProgressUpdate(String... text) {
            //finalResult.setText(text[0]);

        }
    }

}