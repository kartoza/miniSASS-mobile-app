package com.rk.amii.services;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import android.util.Log;
import java.util.concurrent.atomic.AtomicInteger;



public class ApiService {

    private final Context context;
    private final String domain = "https://minisass.sta.do.kartoza.com/";

    public ApiService(Context context) {
        this.context = context;
    }

    public void getSites(final VolleyCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(this.context);
        String url = this.domain+"monitor/sites/";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        // Display the first 500 characters of the response string.
                        callback.onSuccess(res);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void getSiteById(final VolleyCallback callback, String siteId) {
        RequestQueue queue = Volley.newRequestQueue(this.context);
        String url = this.domain+"monitor/sites/"+siteId+"/";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        // Display the first 500 characters of the response string.
                        callback.onSuccess(res);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }



    public void getAssessmentsBySiteById(final VolleyCallback callback, String siteId) {
        RequestQueue queue = Volley.newRequestQueue(this.context);
        String url = this.domain+"monitor/observations/by-site/"+siteId+"/";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        // Display the first 500 characters of the response string.
                        callback.onSuccess(res);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public boolean updateSiteById(String siteId, JSONObject data) {
        JSONObject response = sendRequestWithHeaders(this.domain+"monitor/sites/"+siteId+"/", data, "PATCH");
        try {
            if (response.get("status").toString().trim().equals("200")) {
                System.out.println("Site updated");
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("Login exception: " + e);
            return false;
        }
    }

    public boolean deleteSiteById(String siteId) {
        JSONObject response = sendRequestWithHeaders(this.domain+"monitor/sites/"+siteId+"/", new JSONObject(), "DELETE");
        try {
            if (response.get("status").toString().trim().equals("200")) {
                System.out.println("Site updated");
            }
            return false;
        } catch (Exception e) {
            System.out.println("Login exception: " + e);
            return false;
        }
    }

    public void getObservations(final VolleyCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(this.context);
        String url = this.domain+"monitor/observations/recent-observations/";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        // Display the first 500 characters of the response string.
                        callback.onSuccess(res);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 10000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public interface VolleyCallback{
        void onSuccess(String result);
    }

    public Map<String, Object> login(JSONObject details) {
        Map<String, Object> result = new HashMap<>();
        JSONObject response = sendPostRequest(this.domain + "authentication/api/login/?app=mobile", details);

        try {
            if (response.get("status").toString().trim().equals("200")) {
                if (!TextUtils.isEmpty(response.get("data").toString())) {
                    JSONObject tokens = new JSONObject(response.get("data").toString());
                    String accessToken = tokens.optString("access_token", "").trim();
                    String refreshToken = tokens.optString("refresh_token", "").trim();
                    Boolean gaveConsent = null;
                    if (tokens.has("is_agreed_to_privacy_policy")) {
                        gaveConsent = tokens.optBoolean("is_agreed_to_privacy_policy", false);
                    }
                    if (!TextUtils.isEmpty(accessToken) || !TextUtils.isEmpty(refreshToken)) {
                        writeToStorage("refresh_token.txt", refreshToken);
                        writeToStorage("access_token.txt", accessToken);

                        result.put("authenticated", true);
                        result.put("is_agreed_to_privacy_policy", gaveConsent);
                        return result;
                    }
                }
            }

            result.put("authenticated", false);
            return result;

        } catch (Exception e) {
            System.out.println("Login exception: " + e);
            result.put("authenticated", false);
            return result;
        }
    }

    public Map<String, Object> checkAuthStatus() {
        Map<String, Object> result = new HashMap<>();
        final Map<String, Object> finalResult = result; // For thread access

        Thread thread = new Thread(() -> {
            try {
                URL endpointUrl = new URL(this.domain+"authentication/api/check-auth-status/");
                HttpURLConnection conn = (HttpURLConnection) endpointUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");

                String token = readFromStorage("access_token.txt");
                System.out.println("Access Token: " + token);
                conn.setRequestProperty("Authorization", "Bearer " + token);

                conn.setConnectTimeout(5000);
                conn.setDoInput(true);

                int statusCode = conn.getResponseCode();
                InputStream stream = (statusCode >= 400) ? conn.getErrorStream() : conn.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                StringBuilder content = new StringBuilder();
                String current;
                while ((current = br.readLine()) != null) {
                    content.append(current);
                }

                System.out.println("CheckAuthStatus - Status: " + statusCode);
                System.out.println("CheckAuthStatus - Response: " + content.toString());

                if (statusCode == 200) {
                    JSONObject data = new JSONObject(content.toString());
                    Boolean gaveConsent = null;
                    Boolean isAuthenticated = null;
                    if (data.has("is_agreed_to_privacy_policy")) {
                        gaveConsent = data.optBoolean("is_agreed_to_privacy_policy", false);
                    }
                    if (data.has("is_authenticated")) {
                        isAuthenticated = data.optBoolean("is_authenticated", false);
                    }
                    finalResult.put("is_agreed_to_privacy_policy", gaveConsent);
                    finalResult.put("is_authenticated", isAuthenticated);
                    finalResult.put("status", "success");
                } else {
                    finalResult.put("status", "error");
                    finalResult.put("is_authenticated", false);
                }

                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                finalResult.put("status", "error");
                finalResult.put("is_authenticated", false);
                finalResult.put("error", e.getMessage());
            }
        });

        thread.start();
        try {
            thread.join(); // Wait for completion
        } catch (InterruptedException e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("is_authenticated", false);
        }

        System.out.println("Final checkAuthStatus result: " + result);
        return result;
    }

    public boolean refreshAccessToken() {
        JSONObject details = new JSONObject();
        try {
            details.put("refresh", readFromStorage("refresh_token.txt"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject response = sendPostRequest(
            this.domain + "authentication/api/token/refresh/",
                details
        );

        try {
            if (response.get("status").toString().trim().equals("200")) {
                JSONObject tokens = new JSONObject(response.get("data").toString());
                String accessToken = tokens.optString("access_token", "").trim();
                if (!TextUtils.isEmpty(accessToken)) {
                    writeToStorage("access_token.txt", accessToken);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean register(JSONObject details) {
        System.out.println(details);
        JSONObject response = sendPostRequest(this.domain+"authentication/api/register/", details);
        try {
            if (response.get("status").toString().trim().equals("201")) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public JSONObject getUserProfile() {
        JSONObject response = sendRequestWithHeaders(
            this.domain+"authentication/api/user/update/",
                new JSONObject(),
            "GET"
        );
        JSONObject result = new JSONObject();
        try {
            if (response.getString("status").trim().equals("200")) {
                String dataString = response.getString("data");
                result = new JSONObject(dataString);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public JSONObject updateUserProfile(JSONObject payload) {
        JSONObject response = sendRequestWithHeaders(
                this.domain+"authentication/api/user/update/",
                payload,
                "POST"
        );
        JSONObject result = new JSONObject();
        try {
            if (response.getString("status").trim().equals("200")) {
                String dataString = response.getString("data");
                result = new JSONObject(dataString);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean resetPassword(JSONObject details) {
        System.out.println(details);
        JSONObject response = sendPostRequest(this.domain+"authentication/api/request-reset/",details);
        try {
            if (response.get("status").toString().trim().equals("200")) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean logout() {
        JSONObject response = sendRequestWithHeaders(this.domain+"authentication/api/logout/", new JSONObject(), "POST");
        try {
            if (response.get("status").toString().trim().equals("200")) {
                writeToStorage("refresh_token.txt", "");
                writeToStorage("access_token.txt", "");
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> autoLogin() {
        /**
         * Check if the user is logged in
         */
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> authStatus = checkAuthStatus();

            if (authStatus != null && !authStatus.isEmpty()) {
                Boolean isAuthenticated = (Boolean) authStatus.get("is_authenticated");
                Boolean agreedToPolicy = (Boolean) authStatus.get("is_agreed_to_privacy_policy");

                if (isAuthenticated != null && isAuthenticated) {
                    result.put("success", true);
                    result.put("is_authenticated", true);
                    result.put("is_agreed_to_privacy_policy", agreedToPolicy);
                } else {
                    result.put("success", false);
                    result.put("is_authenticated", false);
                }
            } else {
                result.put("success", false);
                result.put("is_authenticated", false);
            }
        } catch (Exception e) {
            System.out.println("AutoLogin exception: " + e);
            result.put("success", false);
            result.put("is_authenticated", false);
        }

        return result;
    }

    public Integer createSite(Map<String, File> imageFiles, JSONObject details) {
        AtomicInteger result = new AtomicInteger(0);
        Thread thread = new Thread(() -> {
            try {
                JSONObject response = uploadMultipleImages(this.domain+"monitor/sites/", imageFiles, details);
                try {
                    if (response.get("status").toString().trim().equals("201")) {
                        JSONObject data = new JSONObject(response.get("data").toString());
                        result.set(Integer.parseInt(data.getString("gid")));
                    } else {
                        result.set(0);
                    }
                } catch (Exception e) {
                    System.out.println("Create Site exception: " + e);
                    result.set(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result.get();
    }

    public Integer createAssessment(Map<String, File> imageFiles, JSONObject details) throws IOException, JSONException {
        AtomicInteger result = new AtomicInteger(0);
        Thread thread = new Thread(() -> {
            try {
                JSONObject response = uploadMultipleImages(this.domain+"monitor/observations/", imageFiles, details);
                try {
                    if (response.get("status").toString().trim().equals("201")) {
                        JSONObject data = new JSONObject(response.get("data").toString());
                        result.set(Integer.parseInt(data.getString("observation_id")));
                    }
                } catch (Exception e) {
                    System.out.println("Create assessment exception: " + e);
                }
            } catch (Exception e) {

                e.printStackTrace();
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result.get();
    }

    public boolean sendPrivacyConsent(boolean agree) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("agree", agree);

            JSONObject response = sendRequestWithHeaders(
                    this.domain + "privacy-policy/consent/",
                    payload,
                    "POST"
            );

            return response.get("status").toString().trim().equals("200");
        } catch (Exception e) {
            System.out.println("Consent exception: " + e);
            return false;
        }
    }


    public JSONObject sendPostRequest(String url, JSONObject jsonParam) {
        final JSONObject response = new JSONObject();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL endpointUrl = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) endpointUrl.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());

                    os.writeBytes(jsonParam.toString());
                    os.flush();
                    os.close();

                    System.out.println("STATUS " + conn.getResponseCode());
                    System.out.println("MSG " + conn.getResponseMessage());

                    conn.disconnect();

                    response.put("status", conn.getResponseCode());
                    response.put("message", conn.getResponseMessage());

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String current;
                    String content = "";
                    while ((current = br.readLine()) != null) {
                        content += current;
                    }
                    response.put("data", content);

                    System.out.println(response);
                    System.out.println(content);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    public JSONObject uploadMultipleImages(String urlStr, Map<String, File> imageFiles, JSONObject jsonPart) throws IOException, JSONException {
        String boundary = "===" + System.currentTimeMillis() + "===";
        String LINE_FEED = "\r\n";

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setUseCaches(false);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        conn.setConnectTimeout(15000); // 15 sec to connect
        conn.setReadTimeout(30000);    // 30 sec to read

        String token = readFromStorage("access_token.txt");
        conn.setRequestProperty("Authorization", "Bearer " + token);

        OutputStream outputStream = conn.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

        // Add JSON fields
        if (jsonPart != null) {
            for (Iterator<String> it = jsonPart.keys(); it.hasNext(); ) {
                String key = it.next();
                String value = jsonPart.optString(key);
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_FEED);
                writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
                writer.append(LINE_FEED).append(value).append(LINE_FEED);
            }
        }

        // Add image files with imageKey as the field name
        for (Map.Entry<String, File> entry : imageFiles.entrySet()) {
            String fieldName = entry.getKey();
            File file = entry.getValue();

            writer.append("--").append(boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"").append(fieldName)
                    .append("\"; filename=\"").append(file.getName()).append("\"").append(LINE_FEED);
            writer.append("Content-Type: ").append("image/jpeg").append(LINE_FEED); // or guess from file
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            writer.append(LINE_FEED).flush();

            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();
            writer.append(LINE_FEED).flush();
        }

        // Finish request
        writer.append("--").append(boundary).append("--").append(LINE_FEED);
        writer.flush();
        writer.close();

        // Flush and close the binary stream too!
        outputStream.flush();
        outputStream.close();

        int responseCode = conn.getResponseCode();
        InputStream is = (responseCode >= 400) ? conn.getErrorStream() : conn.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Wrap in JSONObject before returning
        JSONObject result = new JSONObject();
        try {
            result.put("status", responseCode);
            result.put("data", response);
        } catch (JSONException e) {
            e.printStackTrace();
            result.put("status", 500);
            result.put("response", "Failed to build JSON response");
        }
        return result;
    }

    public JSONObject sendRequestWithHeaders(String url, JSONObject jsonParam, String type) {
        final JSONObject response = new JSONObject();

        Thread thread = new Thread(() -> {
            try {
                URL endpointUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) endpointUrl.openConnection();
                conn.setRequestMethod(type);
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");

                String token = readFromStorage("access_token.txt");
                System.out.println("Access Token: " + token);
                conn.setRequestProperty("Authorization", "Bearer " + token);

                conn.setConnectTimeout(5000);
                conn.setDoInput(true);

                // Only write to output stream if not a GET request
                if (!type.equals("GET")) {
                    conn.setDoOutput(true);
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());
                    os.flush();
                    os.close();
                }

                int statusCode = conn.getResponseCode();
                InputStream stream = (statusCode >= 400) ? conn.getErrorStream() : conn.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                StringBuilder content = new StringBuilder();
                String current;
                while ((current = br.readLine()) != null) {
                    content.append(current);
                }

                response.put("status", statusCode);
                response.put("message", conn.getResponseMessage());
                response.put("data", content.toString());

                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    response.put("status", 500);
                    response.put("message", "Error: " + e.getMessage());
                } catch (JSONException jsonEx) {
                    jsonEx.printStackTrace();
                }
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void writeToStorage(String filename, String content) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fileOutputStream.write(content.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFromStorage(String filename) {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    // Add this method to your ApiService class
    public void getVectorTileData(String url, final VolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    callback.onSuccess(response);
                },
                error -> {
                    Log.e("ApiService", "Error fetching vector tile data: " + error.toString());
                    callback.onSuccess("[]"); // Return empty array on error
                });

        // Add the request to the RequestQueue
        requestQueue.add(stringRequest);
    }
}
