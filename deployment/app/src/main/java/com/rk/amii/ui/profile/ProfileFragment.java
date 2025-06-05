package com.rk.amii.ui.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.rk.amii.R;
import com.rk.amii.database.DBHandler;
import com.rk.amii.models.UserModel;
import com.rk.amii.services.ApiService;
import com.rk.amii.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;



public class ProfileFragment extends Fragment {

    private static final String PREFS = "profile_prefs";
    private static final String KEY_UPLOAD_PREF = "upload_pref";
    private SharedPreferences prefs;
    private EditText editUsername, editEmail, editName, editSurname, editOrganisationName;
    private Button buttonSave;
    private DBHandler dbHandler;
    private Spinner spinnerUploadPreference, spinnerOrganisationType, spinnerCountry;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        dbHandler = new DBHandler(getContext());

        editUsername = view.findViewById(R.id.editUsername);
        editEmail = view.findViewById(R.id.editEmail);
        editName = view.findViewById(R.id.editName);
        editSurname = view.findViewById(R.id.editSurname);
        editOrganisationName = view.findViewById(R.id.editOrganisationName);
        buttonSave = view.findViewById(R.id.buttonSave);

        // Get Spinner reference using view.findViewById
        spinnerUploadPreference = view.findViewById(R.id.spinnerUploadPreference);

        // Define the arrays for the dropdown
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),  // <-- use requireContext() instead of this
                R.array.upload_preference_display_names,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUploadPreference.setAdapter(adapter);

        spinnerUploadPreference.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] uploadPreferenceValues = getResources().getStringArray(R.array.upload_preference_values);
                String selectedValue  = uploadPreferenceValues[position];
                String oldPref = prefs.getString(KEY_UPLOAD_PREF, "wifi");

                if (!selectedValue.equals(oldPref) &&
                    (selectedValue.equals("mobile") || selectedValue.equals("both"))) {
                    showDataWarning();
                }

                prefs.edit().putString(KEY_UPLOAD_PREF, selectedValue).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        buttonSave.setOnClickListener(v -> saveProfile());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup spinner first
        spinnerOrganisationType = view.findViewById(R.id.spinnerOrganisationType);

        // Create adapter
        ArrayAdapter<String> orgTypeAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                Constants.ORGANISATION_TYPES
        );
        orgTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set adapter to spinner
        spinnerOrganisationType.setAdapter(orgTypeAdapter);

        spinnerCountry = view.findViewById(R.id.spinnerCountry);

        // Create adapter
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                Constants.COUNTRIES
        );
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set adapter to spinner
        spinnerCountry.setAdapter(countryAdapter);

        loadProfile();
    }


    private void loadProfile() {
        UserModel user = dbHandler.getUserProfile();
        if (user != null) {
            editUsername.setText(user.getUsername());
            editEmail.setText(user.getEmail());
            editName.setText(user.getName());
            editSurname.setText(user.getSurname());
            editOrganisationName.setText(user.getOrganisationName());

            spinnerCountry = getView().findViewById(R.id.spinnerCountry);
            String userCountryCode = user.getCountry();
            int position = 0; // Default to first item

            if (userCountryCode != null && !userCountryCode.trim().isEmpty()) {
                String[] countryCodes = Constants.COUNTRY_CODES;
                for (int i = 0; i < countryCodes.length; i++) {
                    if (countryCodes[i].equals(userCountryCode.trim())) {
                        position = i;
                        break;
                    }
                }
            }

            spinnerCountry.setSelection(position);

            spinnerOrganisationType = getView().findViewById(R.id.spinnerOrganisationType);
            String userOrganisationType = user.getOrganisationType();
            int position = 0; // Default to first item

            if (userOrganisationType != null && !userOrganisationType.trim().isEmpty()) {
                for (int i = 0; i < Constants.ORGANISATION_TYPES.length; i++) {
                    if (Constants.ORGANISATION_TYPES[i].equals(userOrganisationType.trim())) {
                        position = i;
                        break;
                    }
                }
            }

            spinnerOrganisationType.setSelection(position);

            // Set spinner selection
            String uploadPreferenceFromDb = user.getUploadPreference();
            String[] values = getResources().getStringArray(R.array.upload_preference_values);

            for (int i = 0; i < values.length; i++) {
                if (values[i].equals(uploadPreferenceFromDb)) {
                    spinnerUploadPreference.setSelection(i);
                    break;
                }
            }
        }
    }

    public boolean isFormValid() {
        String username = editUsername.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String name = editName.getText().toString().trim();
        String surname = editSurname.getText().toString().trim();
        String organisationName = editOrganisationName.getText().toString().trim();

        spinnerOrganisationType = getView().findViewById(R.id.spinnerOrganisationType);
        String organisationType = "";
        if (spinnerOrganisationType.getSelectedItemPosition() != AdapterView.INVALID_POSITION) {
            int selectedPosition = spinnerOrganisationType.getSelectedItemPosition();
            if (selectedPosition < Constants.ORGANISATION_TYPES.length) {
                organisationType = Constants.ORGANISATION_TYPES[selectedPosition];
            }
        }
        
        spinnerCountry = getView().findViewById(R.id.spinnerCountry);
        String country = "";
        if (spinnerCountry.getSelectedItemPosition() != AdapterView.INVALID_POSITION) {
            int selectedPosition = spinnerCountry.getSelectedItemPosition();
            String[] countryCodes = Constants.COUNTRY_CODES;
            if (selectedPosition < countryCodes.length) {
                country = countryCodes[selectedPosition]; // Gets the country code (e.g., "AW", "AF")
            }
        }
        String uploadPreference = spinnerUploadPreference.getSelectedItem().toString();

        boolean isValid = true;

        // Validate username
        if (username.isEmpty()) {
            editUsername.setError("This field is required");
            isValid = false;
        }

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

        // Validate organisation type
        if (organisationType.isEmpty()) {
            spinnerOrganisationType.setError("This field is required");
            isValid = false;
        }

        // Validate country
        if (country.isEmpty()) {
            spinnerCountry.setError("This field is required");
            isValid = false;
        }

        // Validate organisation name
        if (organisationName.isEmpty()) {
            editOrganisationName.setError("This field is required");
            isValid = false;
        }

        // Upload preference validation (if needed)
        if (uploadPreference.isEmpty()) {
            // Handle error
            isValid = false;
        }

        return isValid;
    }

    private void saveProfile() {
        String username = editUsername.getText().toString();
        String email = editEmail.getText().toString();
        String name = editName.getText().toString();
        String surname = editSurname.getText().toString();
        String organisationType = editOrganisationType.getText().toString();
        String organisationName = editOrganisationName.getText().toString();

        spinnerCountry = getView().findViewById(R.id.spinnerCountry);
        String country = "";
        if (spinnerCountry.getSelectedItemPosition() != AdapterView.INVALID_POSITION) {
            int selectedPosition = spinnerCountry.getSelectedItemPosition();
            String[] countryCodes = Constants.COUNTRY_CODES;
            if (selectedPosition < countryCodes.length) {
                country = countryCodes[selectedPosition]; // Gets the country code (e.g., "AW", "AF")
            }
        }

        int selectedPosition = spinnerUploadPreference.getSelectedItemPosition();
        String[] uploadPreferenceValues = getResources().getStringArray(R.array.upload_preference_values);
        String selectedUploadPreference = uploadPreferenceValues[selectedPosition];

        if (isFormValid()) {
            ApiService service = new ApiService(getContext());

            JSONObject payload = new JSONObject();
            try {
                payload.put("username", username);
                payload.put("email", email);
                payload.put("name", name);
                payload.put("surname", surname);
//                payload.put("organisation_type", organisationType);
                payload.put("organisation_name", organisationName);
                payload.put("country", country);
                payload.put("upload_preference", selectedUploadPreference);
                JSONObject result = service.updateUserProfile(payload);
                if (result.length() > 0) {
                    Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                    dbHandler.updateUserProfile(
                        username, email, name, surname, "",
                        organisationName, country, selectedUploadPreference
                    );
                } else {
                    Toast.makeText(getContext(), "Profile update failed", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
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
