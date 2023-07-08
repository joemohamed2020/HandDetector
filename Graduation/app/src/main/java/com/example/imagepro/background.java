package com.example.imagepro;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

class BackgroundTask extends AsyncTask<String, Void, String> {
    private static final String TAG = BackgroundTask.class.getSimpleName();

    private AlertDialog dialog;
    private Context context;

    public BackgroundTask(Context ctx) {
        context = ctx;
    }

    @Override
    protected void onPreExecute() {
        dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle("Status");
    }

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        String loginUrl = "http://192.168.1.7:80/login.php";
        String signupUrl = "http://192.168.1.7:80/signup.php";
        String profile = "http://192.168.1.7:80/profile.php";

        if (type.equals("login")) {
            try {
                String email = params[1];
                String password = params[2];

                URL url = new URL(loginUrl);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream os = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")
                        + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                writer.write(data);
                writer.flush();
                writer.close();
                os.close();

                InputStream is = http.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));
                String result = "";
                String line;
                while ((line = reader.readLine()) != null) {
                    result += line;
                }
                reader.close();
                is.close();
                http.disconnect();
                return result;
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            }
        } else if (type.equals("Signup")) {
            try {
                String name = params[1];
                String email = params[2];
                String password = params[3];
                String confirm = params[4];

                URL url = new URL(signupUrl);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream os = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&"
                        + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&"
                        + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&"
                        + URLEncoder.encode("confirm", "UTF-8") + "=" + URLEncoder.encode(confirm, "UTF-8");
                writer.write(data);
                writer.flush();
                writer.close();
                os.close();

                InputStream is = http.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));
                String result = "";
                String line;
                while ((line = reader.readLine()) != null) {
                    result += line;
                }
                reader.close();
                is.close();
                http.disconnect();
                return result;
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            }
        }
        else if (type.equals("update")) {
            try {
                String email = params[1];
                String userName = params[2];
                String newEmail = params[3];
                String password = params[4];

                URL url = new URL(profile);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream os = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")
                        + "&" + URLEncoder.encode("new_email", "UTF-8") + "=" + URLEncoder.encode(newEmail, "UTF-8")
                        + "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8")
                        + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                writer.write(data);
                writer.flush();
                writer.close();
                os.close();

                InputStream is = http.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));
                String result = "";
                String line;
                while ((line = reader.readLine()) != null) {
                    result += line;
                }
                reader.close();
                is.close();
                http.disconnect();
                return result;
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            }
        }


        return null;
    }

    @Override
    protected void onPostExecute(String s) {

        dialog.setMessage(s);
        dialog.show();
    }
}