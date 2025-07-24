package com.rk.amii.shared;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.rk.amii.R;
import com.rk.amii.database.DBHandler;
import com.rk.amii.models.UserModel;

import com.rk.amii.MainActivity;

import java.util.HashMap;

public class Utils {

    /**
     * Calculate the river condition based on the mini-sass score of the river
     * @param score score
     * @return the condition of the river
     */
    public static String calculateCondition(Context context, float score, String riverType) {
        String condition = "";
        if (riverType.equals("Sandy")) {
            if(score > 6.9) {
                condition = context.getString(R.string.natural);
            } else if (score >= 5.9 && score <= 6.8) {
                condition = context.getString(R.string.good);
            } else if (score >= 5.4 && score <= 5.8) {
                condition = context.getString(R.string.fair);
            } else if (score >= 4.8 && score <= 5.3) {
                condition = context.getString(R.string.poor);
            } else if (score < 4.8) {
                condition = context.getString(R.string.very_poor);
            }
        } else {
            if(score > 7.2) {
                condition = context.getString(R.string.natural);
            } else if (score >= 6.2 && score <= 7.2) {
                condition = context.getString(R.string.good);
            } else if (score >= 5.7 && score <= 6.1) {
                condition = context.getString(R.string.fair);
            } else if (score >= 5.3 && score <= 5.6) {
                condition = context.getString(R.string.poor);
            } else if (score < 5.3) {
                condition = context.getString(R.string.very_poor);
            }
        }
        return condition;
    }

    /**
     * get the colour value based on the condition of the river
     * @param score score
     * @return colour
     */
    public static String getStatusColor(float score, String riverType) {
        String color = "";
        if (riverType.equals("Sandy")) {
            if(score == 0) {
                color = "#A9A7A8";
            } else if(score > 6.9) {
                color = "#051CA8";
            } else if (score >= 5.9 && score <= 6.8) {
                color = "#288B31";
            } else if (score >= 5.4 && score <= 5.8) {
                color = "#EFAA33";
            } else if (score >= 4.8 && score <= 5.3) {
                color = "#D00501";
            } else if (score < 4.8) {
                color = "#81007F";
            }
        } else {
            if(score == 0) {
                color = "#A9A7A8";
            } else if(score > 7.2) {
                color = "#051CA8";
            } else if (score >= 6.2 && score <= 7.2) {
                color = "#288B31";
            } else if (score >= 5.7 && score <= 6.1) {
                color = "#EFAA33";
            } else if (score >= 5.3 && score <= 5.6) {
                color = "#D00501";
            } else if (score < 5.3) {
                color = "#81007F";
            }
        }
        return color;
    }

    public static boolean isNetworkAvailable(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        UserModel user = dbHandler.getUserProfile();
        String uploadPreference = "both";
        if (user != null) {
            uploadPreference = user.getUploadPreference();
        }

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            return false;
        }

        int type = activeNetworkInfo.getType();
        if ("wifi".equals(uploadPreference) && type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if ("mobile".equals(uploadPreference) && type == ConnectivityManager.TYPE_MOBILE) {
            return true;
        } else if ("both".equals(uploadPreference)) {
            return true;
        }

        // If uploadPreference is "wifi" but current network is mobile, etc.
        return false;
    }

    public static HashMap<String, String> getOnlineInvertMapping() {
        HashMap<String, String> onlineInvertMapping = new HashMap<String, String>();
        onlineInvertMapping.put("Bugs & Beetles", "bugs_beetles");
        onlineInvertMapping.put("Caddisflies", "caddisflies");
        onlineInvertMapping.put("Damselflies", "damselflies");
        onlineInvertMapping.put("Dragonflies", "dragonflies");
        onlineInvertMapping.put("Flat worms", "flatworms");
        onlineInvertMapping.put("Crabs & Shrimps", "crabs_shrimps");
        onlineInvertMapping.put("Leeches", "leeches");
        onlineInvertMapping.put("Minnow Mayflies", "minnow_mayflies");
        onlineInvertMapping.put("Other Mayflies", "other_mayflies");
        onlineInvertMapping.put("Snails/Clams/Mussels", "snails");
        onlineInvertMapping.put("Stoneflies", "stoneflies");
        onlineInvertMapping.put("Trueflies", "true_flies");
        onlineInvertMapping.put("Worms", "worms");

        return onlineInvertMapping;

    }
}
