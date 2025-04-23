package com.rk.amii.shared;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.rk.amii.MainActivity;

public class Utils {

    /**
     * Calculate the river condition based on the mini-sass score of the river
     * @param score score
     * @return the condition of the river
     */
    public static String calculateCondition(float score, String riverType) {
        String condition = "";
        if (riverType.equals("Sandy")) {
            if(score > 6.9) {
                condition = "Natural";
            } else if (score >= 5.9 && score <= 6.8) {
                condition = "Good";
            } else if (score >= 5.4 && score <= 5.8) {
                condition = "Fair";
            } else if (score >= 4.8 && score <= 5.3) {
                condition = "Poor";
            } else if (score < 4.8) {
                condition = "Very Poor";
            }
        } else {
            if(score > 7.2) {
                condition = "Natural";
            } else if (score >= 6.2 && score <= 7.2) {
                condition = "Good";
            } else if (score >= 5.7 && score <= 6.1) {
                condition = "Fair";
            } else if (score >= 5.3 && score <= 5.6) {
                condition = "Poor";
            } else if (score < 5.3) {
                condition = "Very Poor";
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
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(MainActivity.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
