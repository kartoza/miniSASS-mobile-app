package com.rk.amii.ui.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.rk.amii.R;
import com.rk.amii.database.DBHandler;
import com.rk.amii.models.UserModel;
import com.rk.amii.services.ApiService;
import com.rk.amii.utils.Constants;
import com.rk.amii.activities.LanguageSelectionActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {
    private View view;
    private static final String PREFS = "profile_prefs";
    private static final String KEY_UPLOAD_PREF = "upload_pref";
    private SharedPreferences prefs;
    private TextInputEditText editEmail, editName, editSurname, editOrganisationName;
    private Button buttonSave;
    private DBHandler dbHandler;
    private AutoCompleteTextView spinnerUploadPreference, spinnerOrganisationType, autoCompleteCountry;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        setupLanguageSelection();
        prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        dbHandler = new DBHandler(getContext());

        editEmail = view.findViewById(R.id.editEmail);
        editName = view.findViewById(R.id.editName);
        editSurname = view.findViewById(R.id.editSurname);
        editOrganisationName = view.findViewById(R.id.editOrganisationName);
        buttonSave = view.findViewById(R.id.buttonSave);

        // Get AutoCompleteTextView references
        spinnerUploadPreference = view.findViewById(R.id.spinnerUploadPreference);
        spinnerOrganisationType = view.findViewById(R.id.spinnerOrganisationType);
        autoCompleteCountry = view.findViewById(R.id.autoCompleteCountry);

        // Setup upload preference dropdown
        String[] uploadPreferenceDisplayNames = getResources().getStringArray(R.array.upload_preference_display_names);
        ArrayAdapter<String> uploadAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                uploadPreferenceDisplayNames
        );
        spinnerUploadPreference.setAdapter(uploadAdapter);

        spinnerUploadPreference.setOnItemClickListener((parent, view1, position, id) -> {
            String[] uploadPreferenceValues = getResources().getStringArray(R.array.upload_preference_values);
            String selectedValue = uploadPreferenceValues[position];
            String oldPref = prefs.getString(KEY_UPLOAD_PREF, "wifi");

            if (!selectedValue.equals(oldPref) &&
                    (selectedValue.equals("mobile") || selectedValue.equals("both"))) {
                showDataWarning();
            }

            prefs.edit().putString(KEY_UPLOAD_PREF, selectedValue).apply();
        });

        buttonSave.setOnClickListener(v -> saveProfile());

        return view;
    }

    private void setupLanguageSelection() {
//        int a = 1;
        View languageSettingItem = view.findViewById(R.id.language_setting_item);
        if (languageSettingItem != null) {
            languageSettingItem.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), LanguageSelectionActivity.class);
                startActivity(intent);
            });
        }
//        View languageSettingItem = view.findViewById(R.id.language_setting_item);
//        languageSettingItem.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), LanguageSelectionActivity.class);
//            startActivity(intent);
//        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup organization type dropdown
        ArrayAdapter<String> orgTypeAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                Constants.ORGANISATION_TYPES
        );
        spinnerOrganisationType.setAdapter(orgTypeAdapter);

        // Setup country dropdown
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                Constants.COUNTRIES
        );
        autoCompleteCountry.setAdapter(countryAdapter);

        loadProfile();
    }

    private void loadProfile() {
        UserModel user = dbHandler.getUserProfile();
        if (user != null) {
            editEmail.setText(user.getEmail());
            editName.setText(user.getName());
            editSurname.setText(user.getSurname());
            editOrganisationName.setText(user.getOrganisationName());

            // Set country selection
            String userCountryCode = user.getCountry();
            if (userCountryCode != null && !userCountryCode.trim().isEmpty()) {
                String[] countryCodes = Constants.COUNTRY_CODES;
                for (int i = 0; i < countryCodes.length; i++) {
                    if (countryCodes[i].equals(userCountryCode.trim())) {
                        if (i < Constants.COUNTRIES.length) {
                            autoCompleteCountry.setText(Constants.COUNTRIES[i], false);
                        }
                        break;
                    }
                }
            }

            // Set organization type selection
            String userOrganisationType = user.getOrganisationType();
            if (userOrganisationType != null && !userOrganisationType.trim().isEmpty()) {
                for (int i = 0; i < Constants.ORGANISATION_TYPES.length; i++) {
                    if (Constants.ORGANISATION_TYPES[i].equals(userOrganisationType.trim())) {
                        spinnerOrganisationType.setText(Constants.ORGANISATION_TYPES[i], false);
                        break;
                    }
                }
            }

            // Set upload preference selection
            String uploadPreferenceFromDb = user.getUploadPreference();
            String[] values = getResources().getStringArray(R.array.upload_preference_values);
            String[] displayNames = getResources().getStringArray(R.array.upload_preference_display_names);

            for (int i = 0; i < values.length; i++) {
                if (values[i].equals(uploadPreferenceFromDb)) {
                    if (i < displayNames.length) {
                        spinnerUploadPreference.setText(displayNames[i], false);
                    }
                    break;
                }
            }
        }
    }

    public boolean isFormValid() {
        String email = editEmail.getText().toString().trim();
        String name = editName.getText().toString().trim();
        String surname = editSurname.getText().toString().trim();
        String organisationName = editOrganisationName.getText().toString().trim();

        boolean isValid = true;

        // Validate email
        if (email.isEmpty()) {
            editEmail.setError("This field is required");
            isValid = false;
        }

        // Validate name
        if (name.isEmpty()) {
            editName.setError("This field is required");
            isValid = false;
        }

        // Validate surname
        if (surname.isEmpty()) {
            editSurname.setError("This field is required");
            isValid = false;
        }

        // Validate organisation name
        if (organisationName.isEmpty()) {
            editOrganisationName.setError("This field is required");
            isValid = false;
        }

        return isValid;
    }

    private void saveProfile() {
        String email = editEmail.getText().toString();
        String name = editName.getText().toString();
        String surname = editSurname.getText().toString();
        String organisationName = editOrganisationName.getText().toString();

        // Get selected organization type
        String organisationType = spinnerOrganisationType.getText().toString();

        // Get selected country code
        String country = "";
        String selectedCountryName = autoCompleteCountry.getText().toString();
        if (!selectedCountryName.isEmpty()) {
            for (int i = 0; i < Constants.COUNTRIES.length; i++) {
                if (Constants.COUNTRIES[i].equals(selectedCountryName)) {
                    if (i < Constants.COUNTRY_CODES.length) {
                        country = Constants.COUNTRY_CODES[i];
                    }
                    break;
                }
            }
        }

        // Get selected upload preference value
        String selectedUploadPreference = "";
        String selectedUploadDisplay = spinnerUploadPreference.getText().toString();
        if (!selectedUploadDisplay.isEmpty()) {
            String[] displayNames = getResources().getStringArray(R.array.upload_preference_display_names);
            String[] values = getResources().getStringArray(R.array.upload_preference_values);
            for (int i = 0; i < displayNames.length; i++) {
                if (displayNames[i].equals(selectedUploadDisplay)) {
                    if (i < values.length) {
                        selectedUploadPreference = values[i];
                    }
                    break;
                }
            }
        }

        if (isFormValid()) {
            ApiService service = new ApiService(getContext());

            JSONObject payload = new JSONObject();
            try {
                payload.put("email", email);
                payload.put("name", name);
                payload.put("surname", surname);
                payload.put("organisation_name", organisationName);
                payload.put("organisation_type", organisationType);
                payload.put("country", country);
                payload.put("upload_preference", selectedUploadPreference);

                JSONObject result = service.updateUserProfile(payload);
                if (result.length() > 0) {
                    Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                    dbHandler.updateUserProfile(
                            email, name, surname, organisationType,
                            organisationName, country, selectedUploadPreference
                    );
                } else {
                    Toast.makeText(getContext(), "Profile update failed", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error updating profile", Toast.LENGTH_SHORT).show();
            }
        } else {
            new AlertDialog.Builder(getContext())
                    .setTitle("Profile Warning")
                    .setMessage("Make sure all fields are filled!")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    private void showDataWarning() {
        new AlertDialog.Builder(getContext())
                .setTitle("Data Usage Warning")
                .setMessage("Uploading over mobile data may incur charges. Please ensure you are aware of your data plan.")
                .setPositiveButton("OK", null)
                .show();
    }
}
