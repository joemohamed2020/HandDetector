package com.example.imagepro;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

class ProfileBackgroundTask extends AsyncTask<String, Void, String> {

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

        String profileUrl = "http://192.168.1.7:80/profile.php";

        if (type.equals("Profile")) {
            try {
                URL url = new URL(profileUrl);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("PUT");
                http.setDoInput(true);
                http.setDoOutput(true);

                String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&"
                        + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8") + "&"
                        + URLEncoder.encode("new_email", "UTF-8") + "=" + URLEncoder.encode(newEmail, "UTF-8") + "&"
                        + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                http.getOutputStream().write(data.getBytes());

                int responseCode = http.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Process the response from the server if needed
                    // For example, read the response using http.getInputStream()
                    // and parse the JSON data

                    return "Profile update successful";
                } else {
                    return "Profile update failed";
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
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
