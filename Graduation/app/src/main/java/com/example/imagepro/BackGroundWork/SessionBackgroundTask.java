package com.example.imagepro.BackGroundWork;

import android.os.AsyncTask;

import com.example.imagepro.Constants.Constants;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.net.URLEncoder;

public class SessionBackgroundTask extends AsyncTask<String, Void, String> {
    private static final String TAG = SessionBackgroundTask.class.getSimpleName();

    private final String email;
    private final SessionManager sessionManager;
    private final OnTaskCompleted listener;

    public interface OnTaskCompleted {
        void onTaskCompleted(JSONObject profile);
        void onError(String errorMessage);
    }

    public SessionBackgroundTask(String email, SessionManager sessionManager, OnTaskCompleted listener) {
        this.email = email;
        this.sessionManager = sessionManager;
        this.listener = listener;
    }


    @Override
    protected String doInBackground(String... params) {
        try {
            URL requestUrl = new URL(Constants.retrieveData);
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
            connection.getOutputStream().write(data.getBytes());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                return response.toString();
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String name = jsonObject.getString("name");
                String password = jsonObject.getString("password");

                // Set the retrieved username and password in SessionManager
                sessionManager.setLoggedInUsername(name);
                sessionManager.setLoggedInPassword(password);

                listener.onTaskCompleted(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
                listener.onError("Error parsing JSON");
            }
        } else {
            listener.onError("Error retrieving user information");
        }
    }

}
