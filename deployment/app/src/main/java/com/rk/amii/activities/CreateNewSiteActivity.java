package com.rk.amii.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.rk.amii.adapters.SiteImageAdapter;
import com.rk.amii.camera.Camera;
import com.rk.amii.database.DBHandler;
import com.rk.amii.R;
import com.rk.amii.services.ApiService;
import com.rk.amii.shared.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateNewSiteActivity extends AppCompatActivity {

    private EditText siteName;
    private EditText siteLocation;
    private EditText riverName;
    private EditText description;
    private EditText date;
    private Button addSiteBtn;
    private DBHandler dbHandler;
    private String currentLocation;
    private Button takePhotoButton;
    private String sitePhotoLocation;
    private RecyclerView siteImagesView;
    private AutoCompleteTextView riverType;
    private final ArrayList<String> siteImages = new ArrayList<>();
    private boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_site);
        // Check that the device is connected to the internet
        isOnline = Utils.isNetworkAvailable(this);
        // Get location data from previous activity
        Intent intent = getIntent();
        currentLocation = intent.getStringExtra("location");
        // Create new database handler instance
        dbHandler = new DBHandler(CreateNewSiteActivity.this);
        // Initialize the view elements
        this.initElements();
        // Load site images into site image view
        this.loadSiteImagesInView();
        // Check if the required permissions to use the camera and storage of the device is given
        this.getPermissions();
        // Set required on click listeners
        this.setOnClickListeners();
    }

    /**
     * Set the onclick listeners for taking a photo, selecting a date, and adding a site
     */
    private void setOnClickListeners() {
        takePhotoButton.setOnClickListener(view -> {
            Intent intent1 = new Intent(CreateNewSiteActivity.this, Camera.class);
            startActivityForResult(intent1, 100);
        });

        date.setOnClickListener(view -> {
            final Calendar calender =  Calendar.getInstance();
            int year = calender.get(Calendar.YEAR);
            int month = calender.get(Calendar.MONTH);
            int day = calender.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    CreateNewSiteActivity.this,
                    (view1, year1, monthOfYear, dayOfMonth) -> {
                        String month1 = Integer.toString(monthOfYear + 1);
                        if ((monthOfYear + 1) <= 9) {
                            month1 = "0"+ month1;
                        }
                        date.setText(year1 + "-" + month1 + "-" + dayOfMonth);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        addSiteBtn.setOnClickListener(view -> {
            // Get text input values
            String siteNameValue = siteName.getText().toString();
            String siteLocationValue = siteLocation.getText().toString();
            String riverNameValue = riverName.getText().toString();
            String descriptionValue = description.getText().toString();
            String dateValue = date.getText().toString();
            String riverTypeValue = riverType.getText().toString();

            // Validate required fields
            if (TextUtils.isEmpty(siteNameValue) || TextUtils.isEmpty(siteLocationValue) ||
                    TextUtils.isEmpty(dateValue) || TextUtils.isEmpty(riverTypeValue)) {
                Toast.makeText(CreateNewSiteActivity.this,
                        getResources().getString(R.string.fill_in_all_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            findViewById(R.id.idSavingSiteView).setVisibility(View.VISIBLE);

            // Add the site data to the device database
            long siteId = dbHandler.addNewSite(siteNameValue, siteLocationValue, riverNameValue,
                    descriptionValue, dateValue, riverTypeValue, false);

            // Add the site images to the device database
            for(String imagePath : siteImages) {
                dbHandler.addNewSiteImage(siteId, imagePath);
            }

            // Add the site to the miniSASS website, if the device has a internet connection
            if (isOnline) {
                JSONObject siteObject = new JSONObject();
                JSONObject siteDetails = new JSONObject();
                Map<String, File> imageFiles = new HashMap<>();

                try {
                    ApiService service = new ApiService(this);

                    String[] locationLngLat = siteLocationValue.split(",");

                    siteDetails.put("the_geom", "SRID=4326;POINT ("+locationLngLat[0]+" "+locationLngLat[1]+")");
                    siteDetails.put("site_name", siteNameValue);
                    siteDetails.put("river_name", riverNameValue);
                    siteDetails.put("description", descriptionValue);
                    siteDetails.put("river_cat", riverTypeValue.toLowerCase());

                    Integer counter = 0;
                    for(String imagePath : siteImages) {
                        String imageKey = "images_" + counter;
                        File image = new File(imagePath);

                        imageFiles.put(imageKey, image);
                        counter +=1;
                    }

                    siteObject.put("site_data", siteDetails);

                    Integer onlineSiteId = service.createSite(imageFiles, siteObject);
                    if (onlineSiteId == 0) {
                        this.showCouldNotSaveSiteDialog(siteId);
                    } else {
                        dbHandler.updateSiteUploaded(String.valueOf(siteId), onlineSiteId);
                        Intent intent = new Intent(CreateNewSiteActivity.this, SiteDetailActivity.class);
                        intent.putExtra("siteId", Long.toString(siteId));
                        intent.putExtra("type", "offline");
                        startActivity(intent);
                        finish();
                    }

                } catch (JSONException e) {
                    this.showCouldNotSaveSiteDialog(siteId);
                    e.printStackTrace();
                }
            } else {
                this.showCouldNotSaveSiteDialog(siteId);
            }

        });
    }

    private void showCouldNotSaveSiteDialog(long siteId) {
        new AlertDialog.Builder(CreateNewSiteActivity.this)
                .setTitle(getResources().getString(R.string.could_not_save_online_site))
                .setMessage(getResources().getString(R.string.online_site_save))
                .setNeutralButton("Ok", (dialog, which) -> {
                    Intent intent12 = new Intent(CreateNewSiteActivity.this, SiteDetailActivity.class);
                    intent12.putExtra("siteId", Long.toString(siteId));
                    intent12.putExtra("type", "offline");

                    startActivity(intent12);
                    finish();
                })
                .setIcon(R.drawable.ic_baseline_error_24)
                .show();
    }

    /**
     * Initialize the view elements and set their options and values
     */
    private void initElements() {
        // Get view elements
        siteName = findViewById(R.id.idSiteNameEdit);
        siteLocation = findViewById(R.id.idSiteLocationEdit);
        riverName = findViewById(R.id.idRiverNameEdit);
        description = findViewById(R.id.idDescriptionEdit);
        date = findViewById(R.id.idDateEdit);
        addSiteBtn = findViewById(R.id.idAddSiteButton);
        takePhotoButton = findViewById(R.id.idBtnTakeSitePhoto);
        siteImagesView = findViewById(R.id.rvSiteImages);

        // Set element options and values
        description.setRawInputType(InputType.TYPE_CLASS_TEXT);
        description.setImeActionLabel("Next", EditorInfo.IME_ACTION_NEXT);
        description.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        siteLocation.setText(currentLocation);

        // Populate river type dropdown with list of river types
        riverType = (AutoCompleteTextView) findViewById(R.id.riverTypeSpinner);
        String[] riverTypes = getResources().getStringArray(R.array.riverTypes);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.dropdown_item, riverTypes);
        riverType.setAdapter(adapter);
    }

    /**
     * Make sure the app has permission to access the device's camera, to read from storage, and
     * to write to storage
     */
    private void getPermissions() {
        // Get required permissions to be able to use the device camera
        if(ContextCompat.checkSelfPermission(
                CreateNewSiteActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreateNewSiteActivity.this, new String[]{
                    Manifest.permission.CAMERA},100);
        }
        // Get required permissions to be able to use the device storage
        if(ContextCompat.checkSelfPermission(
                CreateNewSiteActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreateNewSiteActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},200);
        }
    }

    /**
     * Get the image location from the camera activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100) {
            assert data != null;
            sitePhotoLocation = data.getExtras().get("data").toString();
            if (!TextUtils.isEmpty(sitePhotoLocation)) {
                siteImages.add(sitePhotoLocation);
                this.loadSiteImagesInView();
            }
        }
    }

    /**
     * Load the site images into the site image view
     */
    private void loadSiteImagesInView() {
        SiteImageAdapter siteImageAdapter = new SiteImageAdapter(this, siteImages);
        siteImagesView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        siteImagesView.setAdapter(siteImageAdapter);
    }
}