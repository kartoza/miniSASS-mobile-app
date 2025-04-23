package com.rk.amii.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rk.amii.shared.Utils;
import com.rk.amii.database.DBHandler;
import com.rk.amii.models.SitesModel;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Utils.isNetworkAvailable(context)) {
            DBHandler dbHandler = new DBHandler(context);
            ArrayList<SitesModel> sites = dbHandler.getSites(
                "onlineSiteId = ?",
                new String[]{"0"}
            );
            // Sync offline sites
//            Toast.makeText(context, "Site uploaded: " + site.getSiteName(), Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "Site uploaded: ", Toast.LENGTH_SHORT).show();
        }
    }
}
