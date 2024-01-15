package com.rk.amii.services;

import android.app.Service;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        JSONObject response = sendRequestWithHeaders(this.domain+"monitor/sites/"+siteId+"/", data, "PUT");
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

    public boolean login(JSONObject details) {
        JSONObject response = sendPostRequest(this.domain+"authentication/api/login/", details);
        try {
            if (response.get("status").toString().trim().equals("200")) {
                if (!TextUtils.isEmpty(response.get("data").toString())) {
                    JSONObject tokens = new JSONObject(response.get("data").toString());
                    String accessToken = tokens.get("access_token").toString().trim();
                    String refreshToken = tokens.get("refresh_token").toString().trim();
                    System.out.println("ACCESS_TOKEN: " + accessToken);
                    if (!TextUtils.isEmpty(accessToken) || !TextUtils.isEmpty(refreshToken)) {
                        writeToStorage("refresh_token.txt", refreshToken);
                        writeToStorage("access_token.txt", accessToken);
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            System.out.println("Login exception: " + e);
            return false;
        }
    }

    public boolean checkAuthStatus() {
        JSONObject response = sendRequestWithHeaders(this.domain+"authentication/api/check-auth-status/", new JSONObject(), "POST");
        try {
            if (response.get("status").toString().trim().equals("200")) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean register(JSONObject details) {
        System.out.println(details);
        JSONObject response = sendPostRequest(this.domain+"authentication/api/register/",details);
        try {
            if (response.get("status").toString().trim().equals("201")) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
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
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean autoLogin() {
        String refresh_token = readFromStorage("refresh_token.txt");
        if (TextUtils.isEmpty(refresh_token)) {
            return false;
        }
        return true;
    }

    public Integer createSite(JSONObject details) {
        JSONObject response = sendRequestWithHeaders(this.domain+"monitor/sites/", details, "POST");
        try {
            if (response.get("status").toString().trim().equals("201")) {
                JSONObject data = new JSONObject(response.get("data").toString());
                System.out.println(data);
                return Integer.parseInt(data.getString("gid"));
            }
            return 0;
        } catch (Exception e) {
            System.out.println("Create Site exception: " + e);
            return 0;
        }
    }

    public boolean createAssessment(JSONObject details) {
        JSONObject response = sendRequestWithHeaders(this.domain+"/monitor/observations-create/", details, "POST");
        try {
            if (response.get("status").toString().trim().equals("201")) {
                JSONObject data = new JSONObject(response.get("data").toString());
                System.out.println(data);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("Create assessment exception: " + e);
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


    public JSONObject sendRequestWithHeaders(String url, JSONObject jsonParam, String type) {
        final JSONObject response = new JSONObject();
        System.out.println(jsonParam);
        System.out.println(type);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL endpointUrl = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) endpointUrl.openConnection();
                    conn.setRequestMethod(type);
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setRequestProperty("Authorization", "Bearer " + readFromStorage("access_token.txt"));
                    conn.setConnectTimeout(5000);
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
                    System.out.println("CONTENT " + content);
                    response.put("data", content);

                } catch (Exception e) {
                    try {
                        throw e;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
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
}
