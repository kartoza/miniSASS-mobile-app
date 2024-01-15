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

import org.json.JSONException;
import org.json.JSONObject;

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
            TextView username = findViewById(R.id.idLoginUsername);
            TextView password = findViewById(R.id.idLoginPassword);
            JSONObject loginDetails = new JSONObject();
            try {
                loginDetails.put("email", username.getText().toString());
                loginDetails.put("password", password.getText().toString());
                Boolean authenticated = service.login(loginDetails);

                if (authenticated) {
                    Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
                    startActivityForResult(intent, 100);
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
}

