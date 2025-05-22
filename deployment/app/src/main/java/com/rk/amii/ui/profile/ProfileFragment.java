package com.rk.amii.ui.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import androidx.fragment.app.Fragment;

import com.rk.amii.R;
import com.rk.amii.database.DBHandler;
import com.rk.amii.models.UserModel;
import com.rk.amii.services.ApiService;

import org.json.JSONException;
import org.json.JSONObject;


public class ProfileFragment extends Fragment {

    private static final String PREFS = "profile_prefs";
    private static final String KEY_UPLOAD_PREF = "upload_pref";
    private SharedPreferences prefs;
    private EditText editUsername, editEmail, editName, editSurname, editOrganisationName, editCountry;
    private Button buttonSave;
    private DBHandler dbHandler;
    private Spinner spinnerUploadPreference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        dbHandler = new DBHandler(getContext());

        editUsername = view.findViewById(R.id.editUsername);
        editEmail = view.findViewById(R.id.editEmail);
        editName = view.findViewById(R.id.editName);
        editSurname = view.findViewById(R.id.editSurname);
//        editOrganisationType = view.findViewById(R.id.editOrganisationType);
        editOrganisationName = view.findViewById(R.id.editOrganisationName);
        editCountry = view.findViewById(R.id.editCountry);
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

        // Load user data from DB
        loadProfile();

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

    private void loadProfile() {
        UserModel user = dbHandler.getUserProfile();
        if (user != null) {
            editUsername.setText(user.getUsername());
            editEmail.setText(user.getEmail());
            editName.setText(user.getName());
            editSurname.setText(user.getSurname());
//            editOrganisationType.setText(user.getOrganisationType());
            editOrganisationName.setText(user.getOrganisationName());
            editCountry.setText(user.getCountry());

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
//        String organisationType = editOrganisationType.getText().toString().trim();
        String organisationName = editOrganisationName.getText().toString().trim();
        String country = editCountry.getText().toString().trim(); // Not required
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

//        // Validate organisation type
//        if (organisationType.isEmpty()) {
//            editOrganisationType.setError("This field is required");
//            isValid = false;
//        }

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
//        String organisationType = editOrganisationType.getText().toString();
        String organisationName = editOrganisationName.getText().toString();
        String country = editCountry.getText().toString();
        int selectedPosition = spinnerUploadPreference.getSelectedItemPosition();
        String[] uploadPreferenceValues = getResources().getStringArray(R.array.upload_preference_values);
        String selectedUploadPreference = uploadPreferenceValues[selectedPosition];

        if (isFormValid()) {
            dbHandler.updateUserProfile(username, email, name, surname, "", organisationName, country, selectedUploadPreference);

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
                service.updateUserProfile(payload);
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
