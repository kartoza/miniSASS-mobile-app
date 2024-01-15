package com.rk.amii.shared;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.rk.amii.MainActivity;
import com.rk.amii.R;
import com.rk.amii.services.ApiService;

import org.json.JSONException;
import org.json.JSONObject;

public class ResetPasswordDialog extends DialogFragment {

    private boolean isOnline;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reset_password_dialog,container,false);
        getDialog().setTitle(getResources().getString(R.string.reset_password_title));

        isOnline = Utils.isNetworkAvailable(this.getContext());

        EditText email = view.findViewById(R.id.idResetPassword);
        Button resetBtn = view.findViewById(R.id.idResetPasswordBtn);
        Button okBtn = view.findViewById(R.id.idResetPasswordOkBtn);
        TextView message = view.findViewById(R.id.idResetPasswordMessage);

        resetBtn.setOnClickListener(view1 -> {
            hideKeyboardFrom(this.getContext(), view);
            if (isOnline) {
                try {
                    ApiService service = new ApiService(this.getContext());
                    JSONObject data = new JSONObject();
                    data.put("email", email.getText());
                    boolean success = service.resetPassword(data);
                    if (!success) {
                        message.setText(R.string.something_went_wrong_try_again);
                    }
                    message.setVisibility(View.VISIBLE);
                    resetBtn.setVisibility(View.GONE);
                    okBtn.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        okBtn.setOnClickListener(view1 -> {
            getDialog().hide();
        });


        return view;
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}