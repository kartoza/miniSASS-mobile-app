package com.rk.amii.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.fragment.app.Fragment;

import com.rk.amii.R;
import com.rk.amii.database.DBHandler;
import com.rk.amii.models.UserModel;


public class ProfileFragment extends Fragment {

    private EditText editUsername, editEmail, editName, editSurname, editOrganisationType, editOrganisationName, editCountry;
    private Button buttonSave;
    private DBHandler dbHandler;
    private Spinner spinnerUploadPreference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        dbHandler = new DBHandler(getContext());

        editUsername = view.findViewById(R.id.editUsername);
        editEmail = view.findViewById(R.id.editEmail);
        editName = view.findViewById(R.id.editName);
        editSurname = view.findViewById(R.id.editSurname);
        editOrganisationType = view.findViewById(R.id.editOrganisationType);
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
            editOrganisationType.setText(user.getOrganisationType());
            editOrganisationName.setText(user.getOrganisationName());
            editCountry.setText(user.getCountry());
//            editUploadPreference.setText(user.getUploadPreference());

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

    private void saveProfile() {
        String username = editUsername.getText().toString();
        String email = editEmail.getText().toString();
        String name = editName.getText().toString();
        String surname = editSurname.getText().toString();
        String organisationType = editOrganisationType.getText().toString();
        String organisationName = editOrganisationName.getText().toString();
        String country = editCountry.getText().toString();
        int selectedPosition = spinnerUploadPreference.getSelectedItemPosition();
        String[] uploadPreferenceValues = getResources().getStringArray(R.array.upload_preference_values);
        String selectedUploadPreference = uploadPreferenceValues[selectedPosition];

        dbHandler.updateUserProfile(username, email, name, surname, organisationType, organisationName, country, selectedUploadPreference);

        Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
    }
}
