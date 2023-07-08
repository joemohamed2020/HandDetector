package com.example.imagepro;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class RetrieveEmailsTask extends AsyncTask<Void, Void, String> {
    private Context context; // Add this field to your AsyncTask class

    // Modify your AsyncTask constructor to receive the context
    public RetrieveEmailsTask(Context context) {
        this.context = context;
    }


    @Override
    protected String doInBackground(Void... voids) {
        try {
            URL url = new URL("http://192.168.1.7:80/retrieve_emails.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                // Handle error response
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String response) {
        if (response != null) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                // Process the array of emails
                if(jsonArray.length()>0) {

                    String message = "Email already exists";
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

        }
    }
}
