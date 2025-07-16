package com.rk.amii.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.rk.amii.R;
import com.rk.amii.database.DBHandler;
import com.rk.amii.models.SitesModel;
import com.rk.amii.services.ApiService;
import com.rk.amii.shared.Utils;
import com.rk.amii.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class EditSiteActivity extends AppCompatActivity {

    private EditText siteName;
    private EditText siteLocation;
    private EditText riverName;
    private EditText description;
    private EditText date;
    private AutoCompleteTextView riverType;
    private Button updateSiteButton;
    private DBHandler dbHandler;
    private long siteId = -1;
    private long onlineSiteId = -1;
    SitesModel site;
    private boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_site);

        isOnline = Utils.isNetworkAvailable(this);

        Intent intent = getIntent();

        siteId = Integer.parseInt(intent.getStringExtra("siteId"));
        onlineSiteId = Integer.parseInt(intent.getStringExtra("onlineSiteId"));

        this.initElements();

        dbHandler = new DBHandler(EditSiteActivity.this);
        site = dbHandler.getSiteById((int) siteId);

        this.setElementValues(site);

        // Set the onclick listener for the date field, if the user taps
        // on the element a date picker will open with the default values
        // being the values of the current day
        date.setOnClickListener(view -> {
            final Calendar calender =  Calendar.getInstance();

            int year = calender.get(Calendar.YEAR);
            int month = calender.get(Calendar.MONTH);
            int day = calender.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(EditSiteActivity.this,
                    (view1, year1, monthOfYear, dayOfMonth) -> {
                        String month1 = Integer.toString(monthOfYear + 1);
                        if ((monthOfYear + 1) <= 9) {
                            month1 = "0" + month1;
                        }
                        date.setText(year1 + "-" + month1 + "-" + dayOfMonth);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        // Set the onclick listener for the update site button, if the user
        // taps on the button all fields will be validated to make sure all
        // required fields have values. If all fields are valid the database
        // will be updated with the new field values
        updateSiteButton.setOnClickListener(view -> {
            String siteNameValue = siteName.getText().toString();
            String siteLocationValue = siteLocation.getText().toString();
            String riverNameValue = riverName.getText().toString();
            String descriptionValue = description.getText().toString();
            String dateValue = date.getText().toString();
            SitesModel site = dbHandler.getSiteById((int) siteId);
            String selectedRiverType = riverType.getText().toString();
            String riverTypeValue = "";

            String[] riverTypes = getResources().getStringArray(R.array.riverTypes);
            if (!selectedRiverType.isEmpty()) {
                for (int i = 0; i < riverTypes.length; i++) {
                    if (riverTypes[i].equals(selectedRiverType)) {
                        riverTypeValue = Constants.RIVER_TYPES[i];
                        break;
                    }
                }
            }

            // Validate the required fields
            if (TextUtils.isEmpty(siteNameValue) || TextUtils.isEmpty(siteLocationValue) ||
                    TextUtils.isEmpty(dateValue) || TextUtils.isEmpty(riverTypeValue)) {
                Toast.makeText(EditSiteActivity.this,
                        getResources().getString(R.string.fill_in_all_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            findViewById(R.id.idUpdatingSiteView).setVisibility(View.VISIBLE);

            // Make sure the siteId exists, otherwise we can't update the site
            // Site Country is not updateable by user.
            if (siteId != -1) {
                dbHandler.updateSite(
                        Long.toString(siteId),
                        siteNameValue,
                        siteLocationValue,
                        riverNameValue,
                        descriptionValue,
                        dateValue,
                        riverTypeValue,
                        site.getCountry()
                );
            }

            // Make sure the onlineSiteId exists, otherwise we can't update the site
            if (siteId != -1 && onlineSiteId != 0) {
                if (isOnline) {
                    JSONObject siteDetails = new JSONObject();
                    try {
                        ApiService service = new ApiService(this);

                        siteDetails.put("site_name", siteNameValue);
                        siteDetails.put("river_name", riverNameValue);
                        siteDetails.put("description", descriptionValue);
                        siteDetails.put("river_cat", riverTypeValue.toLowerCase());

                        boolean updated = service.updateSiteById(String.valueOf(onlineSiteId), siteDetails);

                        if (updated) {
                            finish();
                        } else {
                            this.showCouldNOtUpdateSiteDialog();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        this.showCouldNOtUpdateSiteDialog();
                    }
                } else {
                    this.showCouldNOtUpdateSiteDialog();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        isOnline = Utils.isNetworkAvailable(this);
    }

    private void showCouldNOtUpdateSiteDialog() {
        new AlertDialog.Builder(EditSiteActivity.this)
                .setTitle(getResources().getString(R.string.could_not_update_online_site))
                .setMessage(getResources().getString(R.string.online_site_update))
                .setNeutralButton(getResources().getString(R.string.ok), (dialog, which) -> {
                    finish();
                })
                .setIcon(R.drawable.ic_baseline_error_24)
                .show();
    }

    private void initElements() {
        siteName = findViewById(R.id.idEditSiteName);
        siteLocation = findViewById(R.id.idEditSiteLocation);
        riverName = findViewById(R.id.idEditRiverName);
        description = findViewById(R.id.idEditDescription);
        date = findViewById(R.id.idEditDate);
        updateSiteButton = findViewById(R.id.idUpdateSiteButton);
        riverType = findViewById(R.id.riverTypeSpinnerUpdate);

        String[] riverTypes = getResources().getStringArray(R.array.riverTypes);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.dropdown_item, riverTypes);
        riverType.setAdapter(adapter);
    }

    private void setElementValues(SitesModel site) {
        siteName.setText(site.getSiteName());
        siteLocation.setText(site.getSiteLocation());
        riverName.setText(site.getRiverName());
        description.setText(site.getDescription());
        date.setText(site.getDate());
        String riverTypeValue = site.getRiverType();
        String riverTypeDisplay = "";

        String[] riverTypes = getResources().getStringArray(R.array.riverTypes);
        if (!riverTypeValue.isEmpty()) {
            for (int i = 0; i < Constants.RIVER_TYPES.length; i++) {
                if (Constants.RIVER_TYPES[i].toLowerCase().equals(riverTypeValue.toLowerCase())) {
                    riverTypeDisplay = riverTypes[i];
                    break;
                }
            }
        }

        riverType.setText(riverTypeDisplay, false);
    }

}
