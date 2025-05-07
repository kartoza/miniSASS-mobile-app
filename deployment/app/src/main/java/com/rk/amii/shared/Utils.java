package com.rk.amii.shared;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import com.rk.amii.MainActivity;

import java.util.HashMap;

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
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = false;
        if (cm != null) {
            Network network = cm.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities nc = cm.getNetworkCapabilities(network);
                isConnected = nc != null &&
                        nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            }
        }

        boolean hasInternet = false;
        try {
            Process p = Runtime.getRuntime().exec("ping -c 1 www.google.com");
            hasInternet = p.waitFor() == 0;
        } catch (Exception e) {
            hasInternet = false;
        }

        return isConnected && hasInternet;
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
