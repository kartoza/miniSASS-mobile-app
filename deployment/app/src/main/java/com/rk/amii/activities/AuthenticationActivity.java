package com.rk.amii.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.rk.amii.MainActivity;
import com.rk.amii.R;
import com.rk.amii.services.ApiService;
import com.rk.amii.shared.ResetPasswordDialog;
import com.rk.amii.database.DBHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class AuthenticationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        // Create a new API instance
        ApiService service = new ApiService(this);

        // Auto login, can remove
        //if(service.autoLogin()) {
        //    Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
        //    startActivityForResult(intent, 100);
        //}

        // Get view elements
        Button loginBtn = findViewById(R.id.loginButton);
        ImageView logo = findViewById(R.id.idLoginMiniSASSLogo);
        TextView register = findViewById(R.id.registerBtn);
        TextView resetPassword = findViewById(R.id.resetPasswordBtn);

        logo.setBackgroundResource(R.drawable.minisass_logo);

        loginBtn.setOnClickListener(view -> {
            TextView usernameField = findViewById(R.id.idLoginUsername);
            TextView password = findViewById(R.id.idLoginPassword);
            JSONObject loginDetails = new JSONObject();
            try {
                loginDetails.put("email", usernameField.getText().toString());
                loginDetails.put("password", password.getText().toString());
                Map<String, Object> loginResult = service.login(loginDetails);

                if ((boolean) loginResult.get("authenticated")) {
                    Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
                    intent.putExtra(
                            "is_agreed_to_privacy_policy",
                            (Boolean) loginResult.getOrDefault("is_agreed_to_privacy_policy", null)
                    );
                    startActivityForResult(intent, 100);

                    JSONObject response = service.getUserProfile();
                    DBHandler dbHandler = new DBHandler(getApplicationContext());
                    try {
                        String username = safeOptString(response, "username");
                        String email = safeOptString(response, "email");
                        String name = safeOptString(response, "name");
                        String surname = safeOptString(response, "surname");
                        String organisationType = safeOptString(response, "organisation_type");
                        String organisationName = safeOptString(response, "organisation_name");
                        String country = safeOptString(response, "country");
                        String uploadPreference = safeOptString(response, "upload_preference");

                        dbHandler.addNewUser(username, email, name, surname,
                                organisationType, organisationName, country, uploadPreference);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    TextView error =  findViewById(R.id.idLoginError);
                    error.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        resetPassword.setOnClickListener(view -> {
            FragmentManager manager = getSupportFragmentManager();
            ResetPasswordDialog dialog = new ResetPasswordDialog();
            dialog.show(manager, "reset_password");
        });

        register.setOnClickListener(view -> {
            Intent intent = new Intent(AuthenticationActivity.this, RegistrationActivity.class);
            this.startActivity(intent);
        });
    }

    private String safeOptString(JSONObject object, String key) {
        if (object.isNull(key)) {
            return "";
        }
        return object.optString(key, "");
    }
}

