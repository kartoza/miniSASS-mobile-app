package com.rk.amii.ui.profile; // Adjust the package path as per your project structure

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import com.rk.amii.R; // Use your actual resource id

public class ProfileFragment extends Fragment {

    private TextView profileMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        profileMessage = rootView.findViewById(R.id.profile_message);

        // Optionally, you can update the message here if needed
        profileMessage.setText("You can access, update, or delete your profile at any time via the miniSASS website.");

        return rootView;
    }
}
