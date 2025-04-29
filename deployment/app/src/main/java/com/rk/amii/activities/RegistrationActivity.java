package com.rk.amii.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rk.amii.R;
import com.rk.amii.services.ApiService;
import com.rk.amii.shared.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    public static final int FILE_PICKER_RESULT_CODE = 10;

    public TextView certificate;
    private TextView username;
    private TextView name;
    private TextView surname;
    private TextView email;
    private TextView password;
    private AutoCompleteTextView organisationType;
    private TextView organisationName;
    private AutoCompleteTextView country;
    private Button registrationButton;
    private boolean isOnline;
    private boolean validPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        isOnline = Utils.isNetworkAvailable(this);

        // Initialise the view elements
        this.initElements();

        registrationButton.setOnClickListener(view -> {
            // Get input field values
            String usernameValue = username.getText().toString();
            String nameValue = name.getText().toString();
            String surnameValue = surname.getText().toString();
            String emailValue = email.getText().toString();
            String passwordValue = password.getText().toString();
            String organisationTypeValue = organisationType.getText().toString();
            String organisationNameValue = organisationName.getText().toString();
            String countryValue = country.getText().toString();

            // Validate input fields
            if (
                    TextUtils.isEmpty(usernameValue) || TextUtils.isEmpty(nameValue) ||
                    TextUtils.isEmpty(surnameValue) || TextUtils.isEmpty(emailValue) ||
                    TextUtils.isEmpty(passwordValue) || TextUtils.isEmpty(organisationTypeValue) ||
                    TextUtils.isEmpty(organisationNameValue) || TextUtils.isEmpty(countryValue) &&
                    validPassword)
            {
                Toast.makeText(RegistrationActivity.this,
                        getResources().getString(R.string.fill_in_all_fields), Toast.LENGTH_SHORT).show();
                return;
            } else {
                this.registerUser(usernameValue, nameValue, surnameValue, emailValue,
                        passwordValue, organisationTypeValue, organisationNameValue, countryValue);
            }
        });

        //certificate.setOnFocusChangeListener((view, b) -> {
        //    this.openFileChooser();
        //});

        //certificate.setOnClickListener(view -> {
        //    this.openFileChooser();
        //});
    }

    /**
     * Validate the password input. The password must contain the following:
     * 1 uppercase character
     * 1 lowercase character
     * 1 digit
     * 1 special character
     * Must be 6 characters long
     * @param password The password input
     * @return The password restrictions not met
     */
    private String validatePassword(String password) {
        validPassword = true;
        String required = "Password must: \n";
        Pattern uppercasePattern = Pattern.compile("[A-Z]");
        Pattern lowercasePattern = Pattern.compile("[a-z]");
        Pattern digitPattern = Pattern.compile("[0-9]");
        Pattern specialPattern = Pattern.compile("[`!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?~]");
        Pattern lengthPattern = Pattern.compile("^. {6,}$");

        if (!uppercasePattern.matcher(password).matches()) {
            required += "Contain at least 1 uppercase character \n";
            validPassword = false;
        }
        if (!lowercasePattern.matcher(password).matches()) {
            required += "Contain at least 1 lowercase character \n";
            validPassword = false;
        }
        if (!digitPattern.matcher(password).matches()) {
            required += "Contain at least 1 digit \n";
            validPassword = false;
        }
        if (!specialPattern.matcher(password).matches()) {
            required += "Contain at least 1 special character \n";
            validPassword = false;
        }
        if (!lengthPattern.matcher(password).matches()) {
            required += "Must be at least 6 characters long \n";
            validPassword = false;
        }

        return required;
    }

    /**
     * Initialise the view elements
     */
    private void initElements() {
        //certificate = findViewById(R.id.idRegistrationCertificate);
        username = findViewById(R.id.idRegistrationUsername);
        name = findViewById(R.id.idRegistrationName);
        surname = findViewById(R.id.idRegistrationSurname);
        email = findViewById(R.id.idRegistrationEmail);
        password = findViewById(R.id.idRegistrationPassword);
        organisationType = findViewById(R.id.idRegistrationOrganisationType);
        organisationName = findViewById(R.id.idRegistrationOrganisationName);
        country = findViewById(R.id.idRegistrationCountry);
        registrationButton = findViewById(R.id.idRegistrationButton);

        String[] organisationTypes = getResources().getStringArray(R.array.organisationTypes);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.dropdown_item, organisationTypes);
        organisationType.setAdapter(adapter);

        String[] countryDisplayNames = getResources().getStringArray(R.array.countries_display_names);
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this,
                R.layout.dropdown_item, countryDisplayNames);
        country.setAdapter(countryAdapter);
    }

    /**
     * Register a user on the miniSASS website through the API
     * @param usernameValue The users chosen username
     * @param nameValue The users name
     * @param surnameValue The users surname
     * @param emailValue The users email address
     * @param passwordValue The users chosen password
     * @param organisationTypeValue The users organisation type
     * @param organisationNameValue The users organisation name
     * @param countryValue The country the user lives in
     */
    private void registerUser(String usernameValue, String nameValue, String surnameValue,
                              String emailValue, String passwordValue, String organisationTypeValue,
                              String organisationNameValue, String countryValue) {
        if (isOnline) {
            JSONObject registrationDetails = new JSONObject();
            try {
                registrationDetails.put("username", usernameValue);
                registrationDetails.put("first_name", nameValue);
                registrationDetails.put("last_name", surnameValue);
                registrationDetails.put("email", emailValue);
                registrationDetails.put("password", passwordValue);
                registrationDetails.put("organizationType", organisationTypeValue);
                registrationDetails.put("organizationName", organisationNameValue);
                registrationDetails.put("country", countryValue);

                ApiService service = new ApiService(this);
                Boolean registered = service.register(registrationDetails);

                if (registered) {


                    new AlertDialog.Builder(RegistrationActivity.this)
                            .setTitle(getResources().getString(R.string.registration_in_progress))
                            .setMessage(getResources().getString(R.string.registration_message))
                            .setNeutralButton(getResources().getString(R.string.ok), (dialog, which) -> {
                                Intent intent = new Intent(RegistrationActivity.this, AuthenticationActivity.class);
                                startActivityForResult(intent, 100);
                            })
                            .setIcon(R.drawable.ic_baseline_wifi_off_24)
                            .show();


                } else {
                    Toast.makeText(RegistrationActivity.this,
                            getResources().getString(R.string.registration_failed), Toast.LENGTH_SHORT).show();
                    return;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // TODO use app offline
        }

    }

    /**
     * Open the file chooser
     */
    private void openFileChooser() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(chooseFile, FILE_PICKER_RESULT_CODE);
    }

    /**
     * Get the certificate location from the file chooser activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILE_PICKER_RESULT_CODE:
                if (resultCode == -1) {
                    Uri fileUri = data.getData();
                    String filePath = fileUri.getPath();
                    certificate.setText(filePath);
                }
                break;
        }
    }
}