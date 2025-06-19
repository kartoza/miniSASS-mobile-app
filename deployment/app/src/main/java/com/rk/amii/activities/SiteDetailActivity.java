package com.rk.amii.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rk.amii.adapters.SiteImageAdapter;
import com.rk.amii.models.AssessmentModel;
import com.rk.amii.adapters.AssessmentsAdapter;
import com.rk.amii.models.SitesModel;
import com.rk.amii.database.DBHandler;
import com.rk.amii.R;
import com.rk.amii.services.ApiService;
import com.rk.amii.shared.Utils;
import com.rk.amii.database.DBHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SiteDetailActivity extends AppCompatActivity {

    private RecyclerView assessmentsView;
    private TextView noData;
    private RecyclerView siteImagesView;
    private ArrayList<String> siteImages = new ArrayList<>();
    private SitesModel site;
    private TextView siteNameView;
    private TextView riverNameView;
    private TextView descriptionView;
    private TextView country;
    private TextView location;
    private TextView dateView;
    private boolean isOnline;
    private DBHandler dbHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHandler = new DBHandler(getApplicationContext());
        setContentView(R.layout.activity_site_detail);
        setTitle("Site Detail");

        isOnline = Utils.isNetworkAvailable(this);

        // Get site id and type from previous activity
        Intent intent = getIntent();
        long siteId = Integer.parseInt(intent.getStringExtra("siteId"));
        String type = intent.getStringExtra("type");

        noData = (TextView) findViewById(R.id.idNoData);

        if(ContextCompat.checkSelfPermission(
                SiteDetailActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SiteDetailActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE},300);
        }

        this.onPrepareActivity(siteId, type);
    }

    /**
     * Get the site's id and type and prepare the activity view again when the activity is resumed
     */
    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        long siteId = Integer.parseInt(intent.getStringExtra("siteId"));
        String type = intent.getStringExtra("type");
        this.onPrepareActivity(siteId, type);
        isOnline = Utils.isNetworkAvailable(getApplicationContext());
    }

    /**
     * Add Assessment button
     */
    private void setAddAssessment(String siteId) {
        Button addSampleButton = (Button) findViewById(R.id.idAddSample);

        addSampleButton.setOnClickListener(view -> {
            Intent intent = new Intent(SiteDetailActivity.this, CreateNewSampleActivity.class);
            intent.putExtra("siteId", siteId);
            startActivity(intent);
        });

        loadSiteImagesInView();
    }

    /**
     * Prepare the activity, initialise the view elements and set their values, set the onclick
     * listener of the add assessment button, load the site's images into the view
     * @param siteId The site's id
     * @param type The site type (online|offline)
     */
    private void onPrepareActivity(long siteId, String type) {
        //get view elements
        siteNameView = findViewById(R.id.idSiteName);
        riverNameView = findViewById(R.id.idRiverName);
        descriptionView = findViewById(R.id.idDescription);
        country = findViewById(R.id.idCountry);
        location = findViewById(R.id.idLocation);
        dateView = findViewById(R.id.idDate);
        siteImagesView = findViewById(R.id.rvSiteImagesDetails);
        assessmentsView = findViewById(R.id.rvAssessments);

        if (type.equals("offline")) {
            this.getOfflineSiteData((int) siteId);
        } else {
            if (isOnline) {
                this.getOnlineSiteData(String.valueOf(siteId));
            }
        }
    }

    /**
     * Get the offline site data
     * @param siteId The offline site's id
     */
    public void getOfflineSiteData(int siteId) {
        DBHandler dbHandler = new DBHandler(this);

        site = dbHandler.getSiteById(siteId);
        ArrayList<Integer> assessmentIds = dbHandler.getAssessmentIdsBySiteId(site.getSiteId());
        ArrayList<AssessmentModel> assessments = new ArrayList<>();

        siteImages = dbHandler.getSiteImagePathsBySiteId(site.getSiteId());

        for (int i = 0; i < assessmentIds.size(); i++) {
            AssessmentModel assessment = dbHandler.getAssessmentById(assessmentIds.get(i));
            assessments.add(
                    new AssessmentModel(
                            assessmentIds.get(i),
                            assessment.getOnlineAssessmentId(),
                            assessment.getMiniSassScore(),
                            assessment.getMiniSassMLScore(),
                            assessment.getCollectorsName(),
                            assessment.getOrganisation(),
                            assessment.getObservationDate(),
                            assessment.getNotes(),
                            assessment.getPh(),
                            assessment.getWaterTemp(),
                            assessment.getDissolvedOxygen(),
                            assessment.getDissolvedOxygenUnit(),
                            assessment.getElectricalConductivity(),
                            assessment.getElectricalConductivityUnit(),
                            assessment.getWaterClarity()
                    )
            );
        }
        siteNameView.setText(site.getSiteName());
        String riverName = site.getRiverName() + " (" + site.getRiverType() + ")";
        riverNameView.setText(riverName);
        descriptionView.setText(site.getDescription());
        country.setText(site.getCountry());
        String locationText = "(" + site.getSiteLocation().replace(",", ", ") + ")";
        location.setText(locationText);
        dateView.setText(site.getDate());

        setAddAssessment(Integer.toString(siteId));

        prepareAssessments(assessments);
    }

    /**
     * Get the online site data from the miniSASS website
     * @param siteId The online site's id
     */
    public void getOnlineSiteData(String siteId) {

        ApiService service = new ApiService(this);
        try {
            service.getSiteById(result -> {
                try {
                    JSONObject onlineSite = new JSONObject(result);

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

                    long savedSiteId = dbHandler.addNewSite(siteNameValue, locationValue, riverNameValue,
                            descriptionValue, dateValue, riverTypeValue, countryValue, true);

                    site = dbHandler.getSiteById((int) savedSiteId);
                    dbHandler.updateSiteUploaded(
                            Long.toString(savedSiteId), Integer.parseInt(siteId)
                    );
                    siteNameView.setText(site.getSiteName());
                    String riverName = site.getRiverName() + " (" + site.getRiverType() + ")";
                    riverNameView.setText(riverName);
                    descriptionView.setText(site.getDescription());
                    dateView.setText(site.getDate());
                    String locationText = "(" + site.getSiteLocation().replace(",", ", ") + ")";
                    location.setText(locationText);
                    country.setText(site.getCountry());

                    setAddAssessment(Long.toString(savedSiteId));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, siteId);
        } catch (Exception e) {
            System.out.println("ERR: " + e);
        }

        try {

            service.getAssessmentsBySiteById(result -> {
                try {
                    JSONObject data = new JSONObject(result);
                    JSONArray onlineAssessments = new JSONArray(data.getString("observations"));

                    System.out.println(data);
                    System.out.println(onlineAssessments);

                    if(onlineAssessments.length() > 0) {
                        ArrayList<AssessmentModel> assessments = new ArrayList<>();
                        // Add reversed assessment
                        for (int i = onlineAssessments.length() - 1; i >= 0; i--) {

                            JSONObject assessment = new JSONObject(onlineAssessments.get(i).toString());
                            assessments.add(
                                    new AssessmentModel(
                                            Integer.parseInt(assessment.getString("gid")),
                                            Integer.parseInt(assessment.getString("gid")),
                                            Float.parseFloat(assessment.getString("score")),
                                            Float.parseFloat("0.00"),
                                            assessment.getString("collector_name"),
                                            assessment.getString("organisationname"),
                                            assessment.getString("obs_date"),
                                            assessment.getString("comment"),
                                            assessment.getString("ph"),
                                            assessment.getString("water_temp"),
                                            assessment.getString("diss_oxygen"),
                                            assessment.getString("diss_oxygen_unit"),
                                            assessment.getString("elec_cond"),
                                            assessment.getString("elec_cond_unit"),
                                            assessment.getString("water_clarity")
                                    )
                            );

                            System.out.println("ASSESSMENTS: " + assessments + " : " + assessments.size());
                        }
                        prepareAssessments(assessments);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, siteId);
        } catch (Exception e) {
            System.out.println("ERR: " + e);
        }
    }

    /**
     * Load the assessments of the site into the view
     * @param assessments The assessments to load
     */
    private void prepareAssessments(ArrayList<AssessmentModel> assessments) {
        if (assessments.size() > 0) {
            AssessmentsAdapter assessmentsAdapter = new AssessmentsAdapter(this, assessments);
            assessmentsView.setLayoutManager(new LinearLayoutManager(this));
            assessmentsView.setAdapter(assessmentsAdapter);
            noData.setVisibility(View.GONE);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                findViewById(R.id.idLoadingSiteView).setVisibility(View.GONE);
            }
        }, 2000);

    }

    /**
     * Load site images into the view
     */
    private void loadSiteImagesInView() {
        if (siteImages.size() > 0) {
            SiteImageAdapter siteImageAdapter = new SiteImageAdapter(this, siteImages);
            siteImagesView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            siteImagesView.setAdapter(siteImageAdapter);
        } else {
            CardView imageContainer = findViewById(R.id.idSitePhotoContainerDetails);
            imageContainer.setVisibility(View.GONE);
        }
    }
}