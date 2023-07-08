package com.example.imagepro.BackGroundWork;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.imagepro.Constants.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class ProfileBackgroundTask extends AsyncTask<String, Void, String> {

    private Context context;

    public ProfileBackgroundTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        String email = params[1];
        String userName = params[2];
        String newEmail = params[3];
        String password = params[4];


        if (type.equals("Profile")) {
            try {
                URL url = new URL(Constants.profile);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&"
                        + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8") + "&"
                        + URLEncoder.encode("new_email", "UTF-8") + "=" + URLEncoder.encode(newEmail, "UTF-8") + "&"
                        + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                http.getOutputStream().write(data.getBytes());

                int responseCode = http.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    return "Profile Updated Successfully";
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return "Error Updating Record";
    }



    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(context, "Profile update failed", Toast.LENGTH_SHORT).show();
        }
    }
}